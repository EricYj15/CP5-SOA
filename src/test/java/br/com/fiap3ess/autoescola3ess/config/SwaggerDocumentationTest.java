package br.com.fiap3ess.autoescola3ess.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SwaggerDocumentationTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Expectativa: Gerar o documento OpenAPI sem exigir autenticação, com todos os endpoints e o esquema JWT.")
    void apiDocsCenario1() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Auto Escola 3ESS"))
                .andExpect(jsonPath("$.components.securitySchemes.bearer-key.scheme").value("bearer"))
                .andExpect(jsonPath("$.paths['/login'].post").exists())
                .andExpect(jsonPath("$.paths['/alunos'].post").exists())
                .andExpect(jsonPath("$.paths['/instrutores'].get").exists())
                .andExpect(jsonPath("$.paths['/usuarios'].put").exists())
                .andExpect(jsonPath("$.paths['/instrucoes'].delete").exists())
                .andExpect(jsonPath("$.paths['/cep/{cep}'].get").exists());
    }

    @Test
    @DisplayName("Expectativa: Disponibilizar a documentação também em YAML.")
    void apiDocsCenario2() throws Exception {
        mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("title: Auto Escola 3ESS")));
    }

    @Test
    @DisplayName("Expectativa: Disponibilizar a interface do Swagger UI sem exigir autenticação.")
    void swaggerUiCenario1() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
