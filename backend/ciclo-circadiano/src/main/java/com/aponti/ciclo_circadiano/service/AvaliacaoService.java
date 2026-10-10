package com.aponti.ciclo_circadiano.service;

import com.aponti.ciclo_circadiano.dto.AvaliacaoRequest;
import com.aponti.ciclo_circadiano.dto.AvaliacaoResponse;
import com.aponti.ciclo_circadiano.dto.DominioResponse;
import com.aponti.ciclo_circadiano.dto.FeedbackResponse;
import com.aponti.ciclo_circadiano.dto.OpcaoResponse;
import com.aponti.ciclo_circadiano.dto.PerguntaResponse;
import com.aponti.ciclo_circadiano.dto.RespostaRequest;
import com.aponti.ciclo_circadiano.model.AvaliacaoModel;
import com.aponti.ciclo_circadiano.model.UserModel;
import com.aponti.ciclo_circadiano.questionario.BancoDeFeedback;
import com.aponti.ciclo_circadiano.questionario.BancoDePerguntas;
import com.aponti.ciclo_circadiano.questionario.Dominio;
import com.aponti.ciclo_circadiano.questionario.Opcao;
import com.aponti.ciclo_circadiano.questionario.Pergunta;
import com.aponti.ciclo_circadiano.repository.AvaliacaoRepository;
import com.aponti.ciclo_circadiano.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AvaliacaoService {

    private final BancoDePerguntas bancoDePerguntas;
    private final BancoDeFeedback bancoDeFeedback;
    private final UserRepository userRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public AvaliacaoService(BancoDePerguntas bancoDePerguntas,
                            BancoDeFeedback bancoDeFeedback,
                            UserRepository userRepository,
                            AvaliacaoRepository avaliacaoRepository) {
        this.bancoDePerguntas = bancoDePerguntas;
        this.bancoDeFeedback = bancoDeFeedback;
        this.userRepository = userRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    public List<PerguntaResponse> listarPerguntas() {
        return bancoDePerguntas.listar().stream()
                .map(p -> new PerguntaResponse(
                        p.id(),
                        p.texto(),
                        p.ajuda(),
                        p.opcoes().stream()
                                .map(o -> new OpcaoResponse(o.id(), o.texto()))
                                .toList()))
                .toList();
    }

    @Transactional
    public AvaliacaoResponse criarAvaliacao(String email, AvaliacaoRequest request) {
        UserModel usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado."));

        List<Pergunta> perguntas = bancoDePerguntas.listar();
        validarRespostas(request.respostas(), perguntas);

        Map<Integer, Integer> opcaoPorPergunta = request.respostas().stream()
                .collect(Collectors.toMap(RespostaRequest::perguntaId, RespostaRequest::opcaoId));

        int total = 0;
        Map<Dominio, Integer> notas = new EnumMap<>(Dominio.class);
        Map<Dominio, Integer> maximos = new EnumMap<>(Dominio.class);
        List<ItemCorrigido> itens = new ArrayList<>();

        for (Pergunta pergunta : perguntas) {
            int opcaoId = opcaoPorPergunta.get(pergunta.id());
            Opcao escolhida = pergunta.opcoes().stream()
                    .filter(o -> o.id() == opcaoId)
                    .findFirst()
                    .orElseThrow();

            total += escolhida.pontos();
            notas.merge(pergunta.dominio(), escolhida.pontos(), Integer::sum);
            maximos.merge(pergunta.dominio(), 2, Integer::sum);
            itens.add(new ItemCorrigido(pergunta, escolhida));
        }

        String classificacao = classificar(total);

        AvaliacaoModel avaliacao = new AvaliacaoModel(
                null,
                usuario,
                null,
                total,
                notas.get(Dominio.REGULARIDADE_TIMING),
                notas.get(Dominio.SAUDE_DO_SONO),
                notas.get(Dominio.LUZ_E_ESTIMULANTES),
                classificacao);
        avaliacao = avaliacaoRepository.save(avaliacao);

        return montarResposta(avaliacao, notas, maximos, itens);
    }

    private void validarRespostas(List<RespostaRequest> respostas, List<Pergunta> perguntas) {
        if (respostas.size() != perguntas.size()) {
            throw requisicaoInvalida("É preciso responder todas as " + perguntas.size() + " perguntas.");
        }

        Set<Integer> vistas = new HashSet<>();
        for (RespostaRequest resposta : respostas) {
            if (!vistas.add(resposta.perguntaId())) {
                throw requisicaoInvalida("Pergunta repetida: " + resposta.perguntaId());
            }

            Pergunta pergunta = perguntas.stream()
                    .filter(p -> p.id() == resposta.perguntaId())
                    .findFirst()
                    .orElseThrow(() -> requisicaoInvalida("Pergunta inexistente: " + resposta.perguntaId()));

            boolean opcaoValida = pergunta.opcoes().stream()
                    .anyMatch(o -> o.id() == resposta.opcaoId());
            if (!opcaoValida) {
                throw requisicaoInvalida("Opção " + resposta.opcaoId()
                        + " inválida para a pergunta " + resposta.perguntaId());
            }
        }
    }

    private String classificar(int total) {
        if (total <= 7) {
            return "Ruim";
        }
        if (total <= 14) {
            return "Médio";
        }
        return "Ótimo";
    }

    private AvaliacaoResponse montarResposta(AvaliacaoModel avaliacao,
                                             Map<Dominio, Integer> notas,
                                             Map<Dominio, Integer> maximos,
                                             List<ItemCorrigido> itens) {
        List<DominioResponse> dominios = List.of(
                new DominioResponse("Regularidade e timing",
                        notas.get(Dominio.REGULARIDADE_TIMING), maximos.get(Dominio.REGULARIDADE_TIMING)),
                new DominioResponse("Saúde do sono",
                        notas.get(Dominio.SAUDE_DO_SONO), maximos.get(Dominio.SAUDE_DO_SONO)),
                new DominioResponse("Luz e estimulantes",
                        notas.get(Dominio.LUZ_E_ESTIMULANTES), maximos.get(Dominio.LUZ_E_ESTIMULANTES)));

        List<ItemCorrigido> ordenados = itens.stream()
                .sorted(Comparator.comparingInt((ItemCorrigido i) -> i.escolhida().pontos())
                        .thenComparingInt(i -> i.pergunta().id()))
                .toList();

        List<FeedbackResponse> feedback = new ArrayList<>();
        for (int i = 0; i < ordenados.size(); i++) {
            ItemCorrigido item = ordenados.get(i);
            int pontos = item.escolhida().pontos();
            boolean prioridade = i < 3 && pontos < 2;
            feedback.add(new FeedbackResponse(
                    item.pergunta().id(),
                    item.pergunta().texto(),
                    item.escolhida().texto(),
                    pontos,
                    prioridade,
                    bancoDeFeedback.textoPara(item.pergunta().id(), pontos)));
        }

        int pontuacaoMaxima = maximos.values().stream().mapToInt(Integer::intValue).sum();

        return new AvaliacaoResponse(
                avaliacao.getId(),
                avaliacao.getDataAvaliacao(),
                avaliacao.getPontuacaoTotal(),
                pontuacaoMaxima,
                avaliacao.getClassificacao(),
                dominios,
                feedback);
    }

    private ResponseStatusException requisicaoInvalida(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }

    private record ItemCorrigido(Pergunta pergunta, Opcao escolhida) {}
}