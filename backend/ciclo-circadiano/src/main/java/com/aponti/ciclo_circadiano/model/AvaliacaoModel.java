package com.aponti.ciclo_circadiano.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Avaliacoes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Usuario_Id", nullable = false, updatable = false)
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "Data_avaliacao", nullable = false, updatable = false)
    private LocalDateTime dataAvaliacao;

    @Column(name = "Pontuacao_total", nullable = false)
    private int pontuacaoTotal;

    @Column(name = "Nota_regularidade", nullable = false)
    private int notaRegularidade;

    @Column(name = "Nota_saude_sono", nullable = false)
    private int notaSaudeSono;

    @Column(name = "Nota_luz_estimulante", nullable = false)
    private int notaLuzEstimulante;

    @Column(name = "Classificacao", nullable = false, length = 20)
    private String classificacao;
}