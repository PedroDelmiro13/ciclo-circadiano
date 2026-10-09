package com.aponti.ciclo_circadiano.controller;

import com.aponti.ciclo_circadiano.dto.PerguntaResponse;
import com.aponti.ciclo_circadiano.service.AvaliacaoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping("/perguntas")
    public List<PerguntaResponse> listarPerguntas() {
        return avaliacaoService.listarPerguntas();
    }
}