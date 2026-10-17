package br.com.fiap3ess.autoescola3ess.config.documentation;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(
                                "bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtido em POST /login")
                        )
                )
                .info(new Info()
                        .title("Auto Escola 3ESS")
                        .version("1.0.0")
                        .description("""
                                API REST para sistema de gerenciamento de instruções da Auto Escola 3ESS.

                                Para testar, faça o login em POST /login (usuário admin e senha admin), \
                                copie o tokenJWT e cole no botão Authorize.

                                Erros de regra de negócio retornam {"mensagem": "..."} e erros de validação \
                                retornam [{"campo": "...", "mensagem": "..."}].""")
                        .contact(new Contact()
                                .name("Auto Escola 3ESS")
                                .email("autoescola3ess@email.com.br")
                        )
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("API externa consumida: ViaCEP")
                        .url("https://viacep.com.br")
                );
    }
}
