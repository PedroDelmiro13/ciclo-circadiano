package com.aponti.ciclo_circadiano.dto;

import jakarta.validation.constraints.NotNull;

public record RespostaRequest(
        @NotNull Integer perguntaId,
        @NotNull Integer opcaoId) {}