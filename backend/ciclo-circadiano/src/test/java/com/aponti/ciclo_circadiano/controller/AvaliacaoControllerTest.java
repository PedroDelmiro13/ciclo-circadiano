package com.aponti.ciclo_circadiano.controller;

import com.aponti.ciclo_circadiano.config.JwtService;
import com.aponti.ciclo_circadiano.model.UserModel;
import com.aponti.ciclo_circadiano.questionario.BancoDePerguntas;
import com.aponti.ciclo_circadiano.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.isIn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AvaliacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BancoDePerguntas bancoDePerguntas;

    private String token;

    @BeforeEach
    void prepararUsuario() {
        userRepository.save(new UserModel(
                null,
                "Teste",
                "teste@email.com",
                passwordEncoder.encode("senha12345"),
                null));
        token = "Bearer " + jwtService.generateToken("teste@email.com");
    }

    @Test
    @DisplayName("GET /perguntas sem token é negado")
    void listaPerguntasSemToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/perguntas"))
                .andExpect(status().is(isIn(List.of(401, 403))));
    }

    @Test
    @DisplayName("Retorna 204 quando não há avaliação")
    void retorna204SemAvaliacoes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/avaliacoes/ultima")
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Cria avaliação com respostas para todas as perguntas")
    void criaAvaliacaoCompleta() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/avaliacoes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoComRespostas(true)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Rejeita avaliação com respostas incompletas")
    void rejeitaAvaliacaoIncompleta() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/avaliacoes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoComRespostas(false)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Retorna 200 para a última avaliação existente")
    void retornaUltimaAvaliacaoCriada() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/avaliacoes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoComRespostas(true)))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.get("/avaliacoes/ultima")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    private String corpoComRespostas(boolean completa) {
        var perguntas = bancoDePerguntas.listar();
        var limite = completa ? perguntas.size() : perguntas.size() - 1;
        var respostas = perguntas.subList(0, limite).stream()
                .map(pergunta -> """
                        {"perguntaId":%d,"opcaoId":%d}
                        """.formatted(pergunta.id(), pergunta.opcoes().getFirst().id()))
                .collect(Collectors.joining(","));

        return """
                {"respostas":[%s]}
                """.formatted(respostas);
    }
}
