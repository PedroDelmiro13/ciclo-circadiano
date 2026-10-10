package com.aponti.ciclo_circadiano.controller;

import com.aponti.ciclo_circadiano.dto.AvaliacaoRequest;
import com.aponti.ciclo_circadiano.dto.AvaliacaoResponse;
import com.aponti.ciclo_circadiano.dto.PerguntaResponse;
import com.aponti.ciclo_circadiano.service.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @PostMapping("/avaliacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public AvaliacaoResponse criarAvaliacao(@Valid @RequestBody AvaliacaoRequest request,
                                            Authentication authentication) {
        return avaliacaoService.criarAvaliacao(authentication.getName(), request);
    }
}