package br.com.fiap3ess.autoescola3ess.application.core.domain;

import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.MotivoCancelamento;

import java.time.LocalDateTime;

public class Instrucao {
    private Long id;
    private Aluno aluno;
    private Instrutor instrutor;
    private LocalDateTime dataHora;
    private MotivoCancelamento motivoCancelamento;

    public Instrucao() {
    }

    public Instrucao(
            Long id,
            Aluno aluno,
            Instrutor instrutor,
            LocalDateTime dataHora) {
        this(id, aluno, instrutor, dataHora, null);
    }

    public Instrucao(
            Long id,
            Aluno aluno,
            Instrutor instrutor,
            LocalDateTime dataHora,
            MotivoCancelamento motivoCancelamento) {
        this.id = id;
        this.aluno = aluno;
        this.instrutor = instrutor;
        this.dataHora = dataHora;
        this.motivoCancelamento = motivoCancelamento;
    }

    public Long getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Instrutor getInstrutor() {
        return instrutor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public MotivoCancelamento getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public boolean isCancelada() {
        return motivoCancelamento != null;
    }

    public void cancelar(MotivoCancelamento motivo) {
        this.motivoCancelamento = motivo;
    }
}
