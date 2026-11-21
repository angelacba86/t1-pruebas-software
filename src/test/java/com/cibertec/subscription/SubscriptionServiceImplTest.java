package com.cibertec.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PaymentGatewayClient paymentGatewayClient;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Test
    @DisplayName("Prueba de Tarjeta Vencida (Fallo): Si la tarjeta expiró, debe suspender la cuenta, lanzar ExpiredCardException y no realizar cobro")
    void debeSuspenderCuentaYNoCobrarCuandoTarjetaEstaVencida() {
        Long subscriptionId = 100L;
        Subscription suscripcionExistente = new Subscription(subscriptionId, "ACTIVE", LocalDate.of(2026, 11, 20));

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(suscripcionExistente);
        when(paymentGatewayClient.isCardValid(subscriptionId)).thenReturn(false);

        ExpiredCardException exception = assertThrows(ExpiredCardException.class, () -> {
            subscriptionService.renewSubscription(subscriptionId);
        });

        assertEquals("La tarjeta asociada ha expirado", exception.getMessage());

        // Verificación clave 1: Validar que la cuenta cambie su estado a "SUSPENDED"
        verify(subscriptionRepository).updateStatus(subscriptionId, "SUSPENDED");

        // Verificación clave 2: Validar que el procesador de pagos externo tenga cero interacciones de cobro
        verify(paymentGatewayClient, never()).chargeMonthlyFee(anyLong());

        // Asegurar que no se actualice la fecha de facturación si la tarjeta falló
        verify(subscriptionRepository, never()).updateBillingDate(anyLong(), any());
    }

    @Test
    @DisplayName("Prueba de Renovación Exitosa (Éxito): Si la tarjeta es válida, cobra y extiende la membresía por exactamente un mes")
    void debeCobrarYExtenderMembresiaPorUnMesCuandoTarjetaEsValida() {
        Long subscriptionId = 200L;
        LocalDate fechaActual = LocalDate.of(2026, 11, 20);
        LocalDate fechaEsperada = LocalDate.of(2026, 12, 20);
        Subscription suscripcionExistente = new Subscription(subscriptionId, "ACTIVE", fechaActual);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(suscripcionExistente);
        when(paymentGatewayClient.isCardValid(subscriptionId)).thenReturn(true);

        Subscription suscripcionRenovada = subscriptionService.renewSubscription(subscriptionId);

        // Verificación del objeto retornado
        assertNotNull(suscripcionRenovada);
        assertEquals(subscriptionId, suscripcionRenovada.id());
        assertEquals("ACTIVE", suscripcionRenovada.status());
        assertEquals(fechaEsperada, suscripcionRenovada.nextBillingDate());

        // Verificación clave 1: Procesar el cobro
        verify(paymentGatewayClient).chargeMonthlyFee(subscriptionId);

        // Verificación clave 2: Comprobar mediante verify() que la nueva fecha guardada sea exactamente más un mes
        verify(subscriptionRepository).updateBillingDate(subscriptionId, fechaEsperada);

        // Verificar que nunca se suspenda si la tarjeta es válida
        verify(subscriptionRepository, never()).updateStatus(subscriptionId, "SUSPENDED");
    }
}
