package com.cibertec.rental.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PenaltyCalculatorTest {

    private PenaltyCalculator penaltyCalculator;

    @BeforeEach
    void setUp() {
        penaltyCalculator = new PenaltyCalculator();
    }

    @Test
    @DisplayName("Escenario 1: Debe lanzar IllegalStateException cuando los días de retraso son negativos")
    void debeLanzarExcepcionCuandoDiasDeRetrasoSonNegativos() {
        int diasNegativos = -5;

        assertThrows(IllegalStateException.class, () -> {
            penaltyCalculator.calculateLatePenalty(diasNegativos);
        });
    }

    @Test
    @DisplayName("Escenario 2: Debe retornar cero de penalización cuando la devolución es a tiempo (0 días de retraso)")
    void debeRetornarCeroCuandoDevolucionEsATiempo() {
        int diasRetraso = 0;
        int penalidadEsperada = 0;

        int penalidadObtenida = penaltyCalculator.calculateLatePenalty(diasRetraso);

        assertEquals(penalidadEsperada, penalidadObtenida);
    }

    @Test
    @DisplayName("Escenario 3: Debe calcular penalización base de $15 por día para retraso leve menor o igual a 7 días")
    void debeCalcularPenalidadBaseParaRetrasoMenorOIgualASieteDias() {
        int diasRetraso = 5;
        // 5 días * $15 = $75
        int penalidadEsperada = 75;

        int penalidadObtenida = penaltyCalculator.calculateLatePenalty(diasRetraso);

        assertEquals(penalidadEsperada, penalidadObtenida);
    }

    @Test
    @DisplayName("Escenario 3 (caso límite): Debe calcular penalización exacta en el límite de 7 días de retraso")
    void debeCalcularPenalidadExactaParaSieteDiasDeRetraso() {
        int diasRetraso = 7;
        // 7 días * $15 = $105
        int penalidadEsperada = 105;

        int penalidadObtenida = penaltyCalculator.calculateLatePenalty(diasRetraso);

        assertEquals(penalidadEsperada, penalidadObtenida);
    }

    @Test
    @DisplayName("Escenario 4: Debe aplicar el tope máximo comercial de $300 ante un retraso prolongado")
    void debeAplicarTopeMaximoDeTresCientosParaRetrasoProlongado() {
        int diasRetraso = 30;
        // 7 * 15 + (30-7)*10 = 105 + 230 = 335 -> Limitado a MAX_PENALTY = 300
        int penalidadEsperada = 300;

        int penalidadObtenida = penaltyCalculator.calculateLatePenalty(diasRetraso);

        assertEquals(penalidadEsperada, penalidadObtenida);
    }
}
