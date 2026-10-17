package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import br.com.fiap3ess.autoescola3ess.application.port.out.ConsultaCepPort;
import br.com.fiap3ess.autoescola3ess.exception.type.CepNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.ServicoExternoException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CepControllerTest {
    @Autowired
    MockMvc mockMvc;

    //Mock só da consulta ao ViaCEP
    @MockitoBean
    ConsultaCepPort consultaCepPort;

    @Test
    @DisplayName("Expectativa: Retornar código 200 com o endereço no mesmo formato do cadastro.")
    @WithMockUser
    void consultarCepCenario1() throws Exception {
        //arrange
        when(consultaCepPort.buscarEnderecoPorCep("01310100")).thenReturn(new Endereco(
                "Avenida Paulista", null, "de 612 a 1510 - lado par", "Bela Vista", "São Paulo", "SP", "01310-100"));

        //act + assert
        mockMvc.perform(get("/cep/01310-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Avenida Paulista"))
                .andExpect(jsonPath("$.cidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"))
                .andExpect(jsonPath("$.cep").value("01310-100"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 sem chamar a API externa quando o CEP for inválido.")
    @WithMockUser
    void consultarCepCenario2() throws Exception {
        mockMvc.perform(get("/cep/123"))
                .andExpect(status().isBadRequest());

        verify(consultaCepPort, never()).buscarEnderecoPorCep(anyString());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 404 quando o CEP não existir.")
    @WithMockUser
    void consultarCepCenario3() throws Exception {
        //arrange
        when(consultaCepPort.buscarEnderecoPorCep("99999999"))
                .thenThrow(new CepNotFoundException("CEP 99999999 não encontrado!"));

        //act + assert
        mockMvc.perform(get("/cep/99999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("CEP 99999999 não encontrado!"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 503 quando o ViaCEP estiver indisponível.")
    @WithMockUser
    void consultarCepCenario4() throws Exception {
        //arrange
        when(consultaCepPort.buscarEnderecoPorCep(anyString()))
                .thenThrow(new ServicoExternoException("Serviço de consulta de CEP (ViaCEP) indisponível no momento!"));

        //act + assert
        mockMvc.perform(get("/cep/01310100"))
                .andExpect(status().isServiceUnavailable());
    }
}
