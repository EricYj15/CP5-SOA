package br.com.fiap3ess.autoescola3ess.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Testa o fluxo completo sem usar mock
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FluxoInstrucaoIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Expectativa: Executar o fluxo completo de agendamento e cancelamento respeitando as regras de negócio.")
    void fluxoCompletoCenario1() throws Exception {
        String token = "Bearer " + login();

        //cadastrar aluno e instrutor
        String idAluno = cadastrar(token, "/alunos", """
                {"nome": "Maria Aluna", "email": "maria@email.com.br", "telefone": "(11) 98888-7777",
                 "cpf": "98765432100", "endereco": {"logradouro": "Rua A", "numero": "1", "bairro": "Centro",
                 "cidade": "São Paulo", "uf": "SP", "cep": "01001-000"}}
                """);
        cadastrar(token, "/instrutores", """
                {"nome": "João Instrutor", "email": "joao@email.com.br", "telefone": "(11) 97777-6666",
                 "cnh": "98765432100", "especialidade": "CARROS", "endereco": {"logradouro": "Rua B",
                 "numero": "2", "bairro": "Centro", "cidade": "São Paulo", "uf": "SP", "cep": "01001-000"}}
                """);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
        LocalDate segunda = LocalDate.now().plusWeeks(1).with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        //agendar escolhendo instrutor pela especialidade
        String resposta = mockMvc.perform(post("/instrucoes")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id_aluno": %s, "especialidade": "CARROS", "data_hora": "%s"}
                                """.formatted(idAluno, segunda.atTime(10, 0).format(formato))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome_aluno").value("Maria Aluna"))
                .andExpect(jsonPath("$.nome_instrutor").value("João Instrutor"))
                .andReturn().getResponse().getContentAsString();
        String idInstrucao = resposta.replaceAll(".*\"id\":(\\d+).*", "$1");

        //regra: no máximo uma instrução por dia para o mesmo aluno
        mockMvc.perform(post("/instrucoes")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id_aluno": %s, "especialidade": "CARROS", "data_hora": "%s"}
                                """.formatted(idAluno, segunda.atTime(15, 0).format(formato))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Aluno já possui instrução agendada para este dia!"));

        //regra: domingo fora do horário de funcionamento
        mockMvc.perform(post("/instrucoes")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id_aluno": %s, "especialidade": "CARROS", "data_hora": "%s"}
                                """.formatted(idAluno, segunda.minusDays(1).atTime(10, 0).format(formato))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Tentativa de agendamento fora do horário de funcionamento!"));

        //cancelar com motivo
        mockMvc.perform(delete("/instrucoes")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id_instrucao": %s, "motivo": "ALUNO_DESISTIU"}
                                """.formatted(idInstrucao)))
                .andExpect(status().isNoContent());

        //a instrução cancelada guarda o motivo e não aparece mais na listagem
        mockMvc.perform(get("/instrucoes/" + idInstrucao).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo_cancelamento").value("ALUNO_DESISTIU"));
        mockMvc.perform(get("/instrucoes").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id == " + idInstrucao + ")]").isEmpty());

        //regra: não é possível cancelar duas vezes
        mockMvc.perform(delete("/instrucoes")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id_instrucao": %s, "motivo": "OUTROS"}
                                """.formatted(idInstrucao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Esta instrução já foi cancelada!"));
    }

    private String login() throws Exception {
        String resposta = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "admin", "senha": "admin"}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return resposta.replaceAll(".*\"tokenJWT\":\"([^\"]+)\".*", "$1");
    }

    private String cadastrar(String token, String uri, String json) throws Exception {
        String location = mockMvc.perform(post(uri)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
        return location.substring(location.lastIndexOf('/') + 1);
    }
}
