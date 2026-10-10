package com.aponti.ciclo_circadiano.dto;

public record AuthResponse(
        String token,
        String tipo,
        long expiraEm
) {
}
