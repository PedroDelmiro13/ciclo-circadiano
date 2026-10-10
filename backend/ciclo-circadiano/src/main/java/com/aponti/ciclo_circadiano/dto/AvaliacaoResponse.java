package com.aponti.ciclo_circadiano.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AvaliacaoResponse (
        UUID id,
        LocalDateTime dataAvaliacao,
        int pontuacaoTotal,
        int pontuacaoMaxima,
        String classificacao,
        List<DominioResponse> dominios,
        List<FeedbackResponse> feedback
) {}

