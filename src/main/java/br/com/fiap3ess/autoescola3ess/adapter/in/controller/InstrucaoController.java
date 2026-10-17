package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrucao.DadosDetalhamentoAgendamento;
import br.com.fiap3ess.autoescola3ess.application.service.AgendaDeInstrucoes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instrucoes")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Instruções", description = "Agendamento, consulta e cancelamento de instruções (aulas)")
public class InstrucaoController {
    @Autowired
    private AgendaDeInstrucoes agenda;

    @PostMapping
    @Operation(
            summary = "Agendar instrução",
            description = """
                    Regras: funcionamento de segunda a sábado, das 06:00 às 21:00; instruções de 1 hora \
                    (horas inteiras); antecedência mínima de 30 minutos; aluno e instrutor ativos; \
                    instrutor sem outra instrução no mesmo horário; no máximo 1 instrução por dia por aluno. \
                    Se o instrutor não for informado, a especialidade é obrigatória e um instrutor livre é escolhido.""")
    @ApiResponse(responseCode = "200", description = "Instrução agendada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada", content = @Content)
    @ApiResponse(responseCode = "404", description = "Aluno ou instrutor não encontrado", content = @Content)
    public ResponseEntity<DadosDetalhamentoAgendamento> agendarInstrucao(@RequestBody @Valid DadosAgendamento dados) {
        return ResponseEntity.ok(agenda.agendar(dados));
    }

    @GetMapping
    @Operation(summary = "Listar instruções agendadas", description = "Lista paginada das instruções não canceladas, ordenada por data/hora.")
    public ResponseEntity<Page<DadosDetalhamentoAgendamento>> listarInstrucoes(
            @ParameterObject @PageableDefault(size = 10, sort = "dataHora") Pageable paginacao) {
        return ResponseEntity.ok(agenda.listar(paginacao));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar instrução")
    @ApiResponse(responseCode = "200", description = "Instrução encontrada")
    @ApiResponse(responseCode = "404", description = "Instrução não encontrada", content = @Content)
    public ResponseEntity<DadosDetalhamentoAgendamento> detalharInstrucao(@PathVariable Long id) {
        return ResponseEntity.ok(agenda.detalhar(id));
    }

    @DeleteMapping
    @Operation(
            summary = "Cancelar instrução",
            description = "Exige antecedência mínima de 24 horas e o motivo do cancelamento (ALUNO_DESISTIU, INSTRUTOR_CANCELOU ou OUTROS).")
    @ApiResponse(responseCode = "204", description = "Instrução cancelada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada", content = @Content)
    @ApiResponse(responseCode = "404", description = "Instrução não encontrada", content = @Content)
    public ResponseEntity<Void> cancelarInstrucao(@RequestBody @Valid DadosCancelamento dados) {
        agenda.cancelar(dados);
        return ResponseEntity.noContent().build();
    }
}
