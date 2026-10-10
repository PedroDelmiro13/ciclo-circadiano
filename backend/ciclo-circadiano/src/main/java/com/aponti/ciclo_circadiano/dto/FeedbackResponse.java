package com.aponti.ciclo_circadiano.dto;

public record FeedbackResponse (
        int perguntaId,
        String pergunta,
        String suaResposta,
        int pontos,
        boolean prioridade,
        String feedback) {}


