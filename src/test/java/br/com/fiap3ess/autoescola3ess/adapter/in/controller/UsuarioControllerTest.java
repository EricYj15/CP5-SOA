package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.usuario.DadosCadastroUsuario;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.usuario.DadosDetalhamentoUsuario;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.usuario.DadosListagemUsuario;
import br.com.fiap3ess.autoescola3ess.application.service.UsuarioService;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Role;
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
class UsuarioControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UsuarioService service;

    private static final String JSON_CADASTRO = """
            {"login": "secretaria", "senha": "senha123", "perfil": "USER"}
            """;

    @Test
    @DisplayName("Expectativa: Retornar código 201 sem expor a senha ao cadastrar usuário.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario1() throws Exception {
        //arrange
        when(service.cadastrarUsuario(any(DadosCadastroUsuario.class)))
                .thenReturn(new DadosDetalhamentoUsuario(2L, "secretaria", Role.USER, true));

        //act + assert
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/usuarios/2"))
                .andExpect(jsonPath("$.login").value("secretaria"))
                .andExpect(jsonPath("$.perfil").value("USER"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 quando a senha tiver menos de 6 caracteres.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario2() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO.replace("senha123", "123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("senha"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 com mensagem quando o login já existir.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario3() throws Exception {
        //arrange
        when(service.cadastrarUsuario(any())).thenThrow(new ValidacaoException("Login já cadastrado!"));

        //act + assert
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CADASTRO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Login já cadastrado!"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 403 quando um usuário com perfil USER acessar o CRUD de usuários.")
    @WithMockUser(roles = "USER")
    void listarCenario1() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 200 e a página de usuários para o perfil ADMIN.")
    @WithMockUser(roles = "ADMIN")
    void listarCenario2() throws Exception {
        //arrange
        var usuario = new DadosListagemUsuario(1L, "admin", Role.ADMIN);
        when(service.listarUsuarios(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(usuario)));

        //act + assert
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].login").value("admin"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 200 ao atualizar o perfil do usuário.")
    @WithMockUser(roles = "ADMIN")
    void atualizarCenario1() throws Exception {
        //arrange
        when(service.atualizarUsuario(any())).thenReturn(new DadosDetalhamentoUsuario(2L, "secretaria", Role.ADMIN, true));

        //act + assert
        mockMvc.perform(put("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": 2, "perfil": "ADMIN"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil").value("ADMIN"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 204 ao excluir usuário.")
    @WithMockUser(roles = "ADMIN")
    void excluirCenario1() throws Exception {
        mockMvc.perform(delete("/usuarios/2"))
                .andExpect(status().isNoContent());

        verify(service).excluirUsuario(2L);
    }
}
