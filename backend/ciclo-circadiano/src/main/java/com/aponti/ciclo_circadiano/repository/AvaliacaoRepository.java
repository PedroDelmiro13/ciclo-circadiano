package com.aponti.ciclo_circadiano.repository;

import com.aponti.ciclo_circadiano.model.AvaliacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AvaliacaoRepository extends JpaRepository<AvaliacaoModel, UUID> {
}
