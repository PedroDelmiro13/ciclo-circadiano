package com.aponti.ciclo_circadiano.service;

import com.aponti.ciclo_circadiano.dto.OpcaoResponse;
import com.aponti.ciclo_circadiano.dto.PerguntaResponse;
import com.aponti.ciclo_circadiano.questionario.BancoDePerguntas;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerguntaService {

    private final BancoDePerguntas banco;

    public PerguntaService(BancoDePerguntas banco){
        this.banco = banco;
    }

    public List<PerguntaResponse> listarPerguntas(){
        return banco.listar().stream()
                .map(p -> new PerguntaResponse(
                        p.id(),
                        p.texto(),
                        p.ajuda(),
                        p.opcoes().stream()
                                .map(o -> new OpcaoResponse(o.id(), o.texto()))
                                .toList()

                ))
                .toList();
    }

}
