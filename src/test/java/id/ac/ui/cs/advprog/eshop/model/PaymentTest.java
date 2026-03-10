package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentTest {

    private Map<String, String> paymentData;

    @BeforeEach
    void setUp() {
        paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
    }

    @Test
    void testCreatePayment() {
        Payment payment = new Payment(
                "payment-1",
                "VOUCHER_CODE",
                "SUCCESS",
                paymentData
        );

        assertEquals("payment-1", payment.getId());
        assertEquals("VOUCHER_CODE", payment.getMethod());
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals(paymentData, payment.getPaymentData());
    }

    @Test
    void testSetPaymentStatus() {
        Payment payment = new Payment(
                "payment-2",
                "BANK_TRANSFER",
                "SUCCESS",
                paymentData
        );

        payment.setStatus("REJECTED");

        assertEquals("REJECTED", payment.getStatus());
    }
}
