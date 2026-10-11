package com.aponti.ciclo_circadiano.controller;

import com.aponti.ciclo_circadiano.model.UserModel;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void prepararUsuario() {
        userRepository.save(new UserModel(
                null,
                "Teste",
                "teste@email.com",
                passwordEncoder.encode("senha12345"),
                null));
    }

    @Test
    @DisplayName("Registra usuário com sucesso")
    void registraUsuarioComSucesso() throws Exception {
        String corpo = """
                {
                  "nome": "Novo usuário",
                  "email": "novo@email.com",
                  "senha": "senha12345"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Bearer")));
    }

    @Test
    @DisplayName("Não registra e-mail já cadastrado")
    void rejeitaEmailDuplicado() throws Exception {
        String corpo = """
                {
                  "nome": "Teste",
                  "email": "teste@email.com",
                  "senha": "senha12345"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Realiza login com sucesso")
    void realizaLoginComSucesso() throws Exception {
        String corpo = """
                {
                  "email": "teste@email.com",
                  "senha": "senha12345"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bearer")));
    }

    @Test
    @DisplayName("Rejeita login com senha incorreta")
    void rejeitaSenhaIncorreta() throws Exception {
        String corpo = """
                {
                  "email": "teste@email.com",
                  "senha": "senha-incorreta"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Rejeita login com corpo vazio")
    void rejeitaLoginComCorpoVazio() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
