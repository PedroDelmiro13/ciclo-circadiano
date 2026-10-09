package com.aponti.ciclo_circadiano.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Usuarios")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", updatable = false)
    private UUID id;

    @Column(name = "Nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "Email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "Senha", nullable = false)
    private String senha; 

    @CreationTimestamp
    @Column(name = "Data_de_Criacao", nullable = false, updatable = false)
    private LocalDateTime dataDeCriacao;
}