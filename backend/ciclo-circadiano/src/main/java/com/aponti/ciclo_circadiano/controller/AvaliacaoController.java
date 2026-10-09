package com.aponti.ciclo_circadiano.controller;

import com.aponti.ciclo_circadiano.dto.PerguntaResponse;
import com.aponti.ciclo_circadiano.service.PerguntaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AvaliacaoController {

    private final PerguntaService perguntaService;

    public AvaliacaoController(PerguntaService perguntaService) {
        this.perguntaService = perguntaService;
    }

    @GetMapping("/perguntas")
    public List<PerguntaResponse> listarPerguntas() {
        return perguntaService.listarPerguntas();
    }
}