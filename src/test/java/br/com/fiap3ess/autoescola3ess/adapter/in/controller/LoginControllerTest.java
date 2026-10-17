package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Usa o usuário admin criado pela migration V11
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Expectativa: Retornar código 200 e o token JWT para credenciais válidas.")
    void efetuarLoginCenario1() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "admin", "senha": "admin"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenJWT").isNotEmpty());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 401 para senha incorreta.")
    void efetuarLoginCenario2() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "admin", "senha": "errada"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 quando login ou senha não forem informados.")
    void efetuarLoginCenario3() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 401 ao usar um token JWT inválido.")
    void tokenInvalidoCenario1() throws Exception {
        mockMvc.perform(get("/instrutores")
                        .header("Authorization", "Bearer token.invalido.123"))
                .andExpect(status().isUnauthorized());
    }
}
