package com.aponti.ciclo_circadiano.dto;

import java.util.List;

public record PerguntaResponse(int id, String texto, String ajuda, List<OpcaoResponse> opcoes) {
}
