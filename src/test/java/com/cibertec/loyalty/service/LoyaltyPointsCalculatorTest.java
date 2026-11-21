package com.cibertec.loyalty.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoyaltyPointsCalculatorTest {

    private LoyaltyPointsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new LoyaltyPointsCalculator();
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el monto de compra es nulo")
    void debeLanzarExcepcionCuandoMontoEsNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateAccumulatedPoints(null, "REGULAR");
        });
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el monto de compra es igual a cero")
    void debeLanzarExcepcionCuandoMontoEsCero() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateAccumulatedPoints(BigDecimal.ZERO, "REGULAR");
        });
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el monto de compra es negativo")
    void debeLanzarExcepcionCuandoMontoEsNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateAccumulatedPoints(new BigDecimal("-50.00"), "VIP");
        });
    }

    @Test
    @DisplayName("Debe calcular equivalencia simple de 1 punto por unidad entera para cliente regular")
    void debeCalcularPuntosParaClienteRegular() {
        BigDecimal monto = new BigDecimal("150.75");
        int puntosEsperados = 150;

        int puntosObtenidos = calculator.calculateAccumulatedPoints(monto, "REGULAR");

        assertEquals(puntosEsperados, puntosObtenidos);
    }

    @Test
    @DisplayName("Debe calcular el doble de puntos por unidad invertida para cliente con estatus VIP")
    void debeCalcularDobleDePuntosParaClienteVip() {
        BigDecimal monto = new BigDecimal("150.75");
        // 150.75 * 2 = 301.50 -> intValue() = 301
        int puntosEsperados = 301;

        int puntosObtenidos = calculator.calculateAccumulatedPoints(monto, "VIP");

        assertEquals(puntosEsperados, puntosObtenidos);
    }

    @Test
    @DisplayName("Debe ignorar mayúsculas y minúsculas en el estatus VIP (vip en minúscula)")
    void debeCalcularDobleDePuntosParaClienteVipEnMinusculas() {
        BigDecimal monto = new BigDecimal("100.00");
        int puntosEsperados = 200;

        int puntosObtenidos = calculator.calculateAccumulatedPoints(monto, "vip");

        assertEquals(puntosEsperados, puntosObtenidos);
    }
}
