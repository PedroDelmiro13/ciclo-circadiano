package com.aponti.ciclo_circadiano.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AvaliacaoRequest (
        @NotNull @Valid List<RespostaRequest> respostas) {}
