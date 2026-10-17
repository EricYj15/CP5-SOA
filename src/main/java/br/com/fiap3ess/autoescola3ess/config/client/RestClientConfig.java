package br.com.fiap3ess.autoescola3ess.config.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient viaCepRestClient(
            @Value("${api.externa.viacep.url}") String baseUrl,
            @Value("${api.externa.timeout-segundos:5}") long timeoutSegundos) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(timeoutSegundos));
        requestFactory.setReadTimeout(Duration.ofSeconds(timeoutSegundos));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
