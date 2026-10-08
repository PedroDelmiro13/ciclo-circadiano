package com.aponti.ciclo_circadiano.questionario;

import java.util.List;

public record Pergunta(int id, String texto, String ajuda, Dominio dominio, List<Opcao> opcoes) {
}
