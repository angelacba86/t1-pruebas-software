package com.cibertec.loyalty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoyaltyServiceImplTest {

    @Mock
    private LoyaltyRepository loyaltyRepository;

    @InjectMocks
    private LoyaltyServiceImpl loyaltyService;

    @Test
    @DisplayName("Prueba de Restricción Comercial (Fallo): Debe lanzar IllegalArgumentException y nunca registrar puntos cuando monto es menor o igual a cero")
    void debeLanzarExcepcionYNuncaRegistrarPuntosCuandoMontoEsInvalido() {
        Long userId = 1L;
        BigDecimal montoInvalido = new BigDecimal("-10.00");

        assertThrows(IllegalArgumentException.class, () -> {
            loyaltyService.calculateAndAssignPoints(userId, montoInvalido);
        });

        // Verificación clave: Garantizar que el repositorio de fidelización nunca registre puntos
        verify(loyaltyRepository, never()).addPoints(anyLong(), anyInt());
        verify(loyaltyRepository, never()).isVipUser(anyLong());
    }

    @Test
    @DisplayName("Prueba de Restricción Comercial (Fallo): Debe lanzar IllegalArgumentException y nunca registrar puntos cuando monto es nulo")
    void debeLanzarExcepcionYNuncaRegistrarPuntosCuandoMontoEsNulo() {
        Long userId = 1L;

        assertThrows(IllegalArgumentException.class, () -> {
            loyaltyService.calculateAndAssignPoints(userId, null);
        });

        // Verificación clave: Garantizar que el repositorio de fidelización nunca registre puntos
        verify(loyaltyRepository, never()).addPoints(anyLong(), anyInt());
        verify(loyaltyRepository, never()).isVipUser(anyLong());
    }

    @Test
    @DisplayName("Prueba de Multiplicador Especial (Éxito): Cliente VIP debe recibir puntaje duplicado exacto e invocar el repositorio")
    void debeCalcularPuntajeDuplicadoYGuardarEnRepositorioParaClienteVip() {
        Long userId = 2L;
        BigDecimal montoCompra = new BigDecimal("150.85");
        // basePoints = 150 -> finalPoints = 300
        int puntosEsperados = 300;

        when(loyaltyRepository.isVipUser(userId)).thenReturn(true);

        LoyaltyResponse response = loyaltyService.calculateAndAssignPoints(userId, montoCompra);

        // Verificación de la respuesta
        assertNotNull(response);
        assertEquals(userId, response.userId());
        assertEquals(puntosEsperados, response.pointsEarned());
        assertTrue(response.isVip());

        // Verificación clave: Validar con verify() que se invoque la actualización del repositorio con el puntaje duplicado exacto
        verify(loyaltyRepository).addPoints(userId, puntosEsperados);
    }

    @Test
    @DisplayName("Cliente Regular: Debe recibir 1 punto por cada unidad entera sin duplicar e invocar el repositorio")
    void debeCalcularPuntajeSimpleYGuardarEnRepositorioParaClienteRegular() {
        Long userId = 3L;
        BigDecimal montoCompra = new BigDecimal("150.85");
        int puntosEsperados = 150;

        when(loyaltyRepository.isVipUser(userId)).thenReturn(false);

        LoyaltyResponse response = loyaltyService.calculateAndAssignPoints(userId, montoCompra);

        // Verificación de la respuesta
        assertNotNull(response);
        assertEquals(userId, response.userId());
        assertEquals(puntosEsperados, response.pointsEarned());
        assertFalse(response.isVip());

        // Verificación de la invocación en el repositorio
        verify(loyaltyRepository).addPoints(userId, puntosEsperados);
    }
}
