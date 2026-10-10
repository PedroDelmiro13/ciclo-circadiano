package com.aponti.ciclo_circadiano.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "avaliacoes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", updatable = false, length = 36, columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Usuario_Id", nullable = false, updatable = false)
    private UserModel usuario;

    @Column(name = "Data_avaliacao", insertable = false, updatable = false)
    private LocalDateTime dataAvaliacao;

    @Column(name = "Pontuacao_Total", insertable = false, updatable = false)
    private Integer pontuacaoTotal;

    @Column(name = "Nota_regularidade", nullable = false)
    private int notaRegularidade;

    @Column(name = "Nota_saude_sono", nullable = false)
    private int notaSaudeSono;

    @Column(name = "Nota_luz_estimulante", nullable = false)
    private int notaLuzEstimulante;

    @Column(name = "Classificacao", nullable = false, length = 20)
    private String classificacao;
}