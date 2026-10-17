package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.aluno.DadosCadastroAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.aluno.DadosDetalhamentoAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.aluno.DadosListagemAluno;
import br.com.fiap3ess.autoescola3ess.application.service.AlunoService;
import br.com.fiap3ess.autoescola3ess.exception.type.AlunoNotFoundException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
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
class AlunoControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AlunoService service;

    private static final String JSON_CADASTRO = """
            {
              "nome": "Aluno Teste",
              "email": "aluno@email.com.br",
              "telefone": "(11) 98765-4321",
              "cpf": "12345678901",
              "endereco": {
                "logradouro": "Rua Teste",
                "numero": "10",
                "bairro": "Centro",
                "cidade": "São Paulo",
                "uf": "SP",
                "cep": "01234-567"
              }
            }
            """;

    @Test
    @DisplayName("Expectativa: Retornar código 201 e o header Location ao cadastrar aluno com dados válidos.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario1() throws Exception {
        //arrange
        when(service.cadastrarAluno(any(DadosCadastroAluno.class))).thenReturn(detalhamento());

        //act + assert
        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/alunos/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.endereco.cidade").value("São Paulo"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 quando o CPF não tiver 11 dígitos.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario2() throws Exception {
        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO.replace("12345678901", "123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("cpf"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 403 quando um usuário com perfil USER tentar cadastrar.")
    @WithMockUser(roles = "USER")
    void cadastrarCenario3() throws Exception {
        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 200 e a página de alunos ativos.")
    @WithMockUser(roles = "USER")
    void listarCenario1() throws Exception {
        //arrange
        var aluno = new DadosListagemAluno(1L, "Aluno Teste", "aluno@email.com.br", "12345678901");
        when(service.listarAlunos(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(aluno)));

        //act + assert
        mockMvc.perform(get("/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Aluno Teste"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 404 com mensagem quando o aluno não existir.")
    @WithMockUser(roles = "USER")
    void detalharCenario1() throws Exception {
        //arrange
        when(service.detalharAluno(99L)).thenThrow(new AlunoNotFoundException("ID do aluno informado não existe!"));

        //act + assert
        mockMvc.perform(get("/alunos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("ID do aluno informado não existe!"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 ao atualizar sem informar o ID.")
    @WithMockUser(roles = "ADMIN")
    void atualizarCenario1() throws Exception {
        mockMvc.perform(put("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Novo Nome"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("id"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 200 ao atualizar aluno.")
    @WithMockUser(roles = "ADMIN")
    void atualizarCenario2() throws Exception {
        //arrange
        when(service.atualizarAluno(any())).thenReturn(detalhamento());

        //act + assert
        mockMvc.perform(put("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": 1, "nome": "Aluno Teste"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Aluno Teste"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 204 ao excluir aluno.")
    @WithMockUser(roles = "ADMIN")
    void excluirCenario1() throws Exception {
        mockMvc.perform(delete("/alunos/1"))
                .andExpect(status().isNoContent());

        verify(service).excluirAluno(1L);
    }

    private DadosDetalhamentoAluno detalhamento() {
        return new DadosDetalhamentoAluno(
                1L,
                "Aluno Teste",
                "aluno@email.com.br",
                "(11) 98765-4321",
                "12345678901",
                new DadosEndereco("Rua Teste", "10", null, "Centro", "São Paulo", "SP", "01234-567"),
                true
        );
    }
}
