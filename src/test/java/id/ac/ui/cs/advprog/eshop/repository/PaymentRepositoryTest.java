package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentRepositoryTest {

    private PaymentRepository paymentRepository;
    private Payment firstPayment;
    private Payment secondPayment;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();

        Map<String, String> voucherData = new HashMap<>();
        voucherData.put("voucherCode", "ESHOP1234ABC5678");
        firstPayment = new Payment("payment-1", "VOUCHER_CODE", "SUCCESS", voucherData);

        Map<String, String> transferData = new HashMap<>();
        transferData.put("bankName", "BCA");
        transferData.put("referenceCode", "REF-001");
        secondPayment = new Payment("payment-2", "BANK_TRANSFER", "SUCCESS", transferData);
    }

    @Test
    void testSaveCreatePayment() {
        Payment savedPayment = paymentRepository.save(firstPayment);

        assertEquals("payment-1", savedPayment.getId());
        assertEquals("payment-1", paymentRepository.findById("payment-1").getId());
        assertEquals("SUCCESS", paymentRepository.findById("payment-1").getStatus());
    }

    @Test
    void testSaveUpdatePayment() {
        paymentRepository.save(firstPayment);

        Payment updatedPayment = new Payment(
                "payment-1",
                "VOUCHER_CODE",
                "REJECTED",
                firstPayment.getPaymentData()
        );
        paymentRepository.save(updatedPayment);

        assertEquals("REJECTED", paymentRepository.findById("payment-1").getStatus());
        assertEquals(1, paymentRepository.findAll().size());
    }

    @Test
    void testFindByIdNotFound() {
        paymentRepository.save(firstPayment);

        assertNull(paymentRepository.findById("missing-id"));
    }

    @Test
    void testFindAllPayments() {
        paymentRepository.save(firstPayment);
        paymentRepository.save(secondPayment);

        List<Payment> payments = paymentRepository.findAll();
        assertEquals(2, payments.size());
        assertEquals("payment-1", payments.get(0).getId());
        assertEquals("payment-2", payments.get(1).getId());
    }
}
