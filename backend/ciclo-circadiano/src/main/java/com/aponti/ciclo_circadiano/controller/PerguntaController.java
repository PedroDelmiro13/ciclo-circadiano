package com.aponti.ciclo_circadiano.controller;

import com.aponti.ciclo_circadiano.dto.PerguntaResponse;
import com.aponti.ciclo_circadiano.service.PerguntaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/perguntas")
public class PerguntaController {

    private final PerguntaService service;

    public PerguntaController(PerguntaService service){
        this.service = service;
    }

    @GetMapping
    public List<PerguntaResponse> listar() {
        return service.listarPerguntas();
    }
}
