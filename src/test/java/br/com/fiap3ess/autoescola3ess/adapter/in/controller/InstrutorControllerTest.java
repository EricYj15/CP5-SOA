package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrutor.DadosCadastroInstrutor;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrutor.DadosDetalhamentoInstrutor;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrutor.DadosListagemInstrutor;
import br.com.fiap3ess.autoescola3ess.application.service.InstrutorService;
import br.com.fiap3ess.autoescola3ess.exception.type.InstrutorNotFoundException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InstrutorControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    InstrutorService service;

    private static final String JSON_CADASTRO = """
            {
              "nome": "Instrutor Teste",
              "email": "instrutor@email.com.br",
              "telefone": "(11) 91234-5678",
              "cnh": "01234567890",
              "especialidade": "MOTOS",
              "endereco": {
                "logradouro": "Rua Teste",
                "numero": "100",
                "bairro": "Centro",
                "cidade": "São Paulo",
                "uf": "SP",
                "cep": "01234-567"
              }
            }
            """;

    @Test
    @DisplayName("Expectativa: Retornar código 201 e o header Location ao cadastrar instrutor com dados válidos.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario1() throws Exception {
        //arrange
        when(service.cadastrarInstrutor(any(DadosCadastroInstrutor.class))).thenReturn(detalhamento());

        //act + assert
        mockMvc.perform(post("/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/instrutores/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.especialidade").value("MOTOS"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 com a lista de campos inválidos.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario2() throws Exception {
        mockMvc.perform(post("/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].campo").isArray())
                .andExpect(jsonPath("$[?(@.campo == 'cnh')]").exists());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 403 quando um usuário com perfil USER tentar cadastrar.")
    @WithMockUser(roles = "USER")
    void cadastrarCenario3() throws Exception {
        mockMvc.perform(post("/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 401 quando a requisição não tiver token.")
    void cadastrarCenario4() throws Exception {
        mockMvc.perform(post("/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 200 e a página de instrutores para o perfil USER.")
    @WithMockUser(roles = "USER")
    void listarCenario1() throws Exception {
        //arrange
        var instrutor = new DadosListagemInstrutor(1L, "Instrutor Teste", "instrutor@email.com.br", Especialidade.MOTOS);
        when(service.listarInstrutores(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(instrutor)));

        //act + assert
        mockMvc.perform(get("/instrutores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Instrutor Teste"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 404 com mensagem quando o instrutor não existir.")
    @WithMockUser(roles = "ADMIN")
    void detalharCenario1() throws Exception {
        //arrange
        when(service.detalharInstrutor(99L))
                .thenThrow(new InstrutorNotFoundException("ID do instrutor informado não existe!"));

        //act + assert
        mockMvc.perform(get("/instrutores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("ID do instrutor informado não existe!"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 200 ao atualizar instrutor.")
    @WithMockUser(roles = "ADMIN")
    void atualizarCenario1() throws Exception {
        //arrange
        when(service.atualizarInstrutor(any())).thenReturn(detalhamento());

        //act + assert
        mockMvc.perform(put("/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": 1, "telefone": "(11) 99999-0000"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 204 ao excluir instrutor.")
    @WithMockUser(roles = "ADMIN")
    void excluirCenario1() throws Exception {
        mockMvc.perform(delete("/instrutores/1"))
                .andExpect(status().isNoContent());

        verify(service).excluirInstrutor(1L);
    }

    private DadosDetalhamentoInstrutor detalhamento() {
        return new DadosDetalhamentoInstrutor(
                1L,
                "Instrutor Teste",
                "instrutor@email.com.br",
                "(11) 91234-5678",
                "01234567890",
                Especialidade.MOTOS,
                new DadosEndereco("Rua Teste", "100", null, "Centro", "São Paulo", "SP", "01234-567"),
                true
        );
    }
}
