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

    private static final String PAYMENT_ID_1 = "payment-1";
    private static final String PAYMENT_ID_2 = "payment-2";
    private static final String METHOD_VOUCHER_CODE = "VOUCHER_CODE";
    private static final String METHOD_BANK_TRANSFER = "BANK_TRANSFER";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String VOUCHER_CODE_KEY = "voucherCode";
    private static final String BANK_NAME_KEY = "bankName";
    private static final String REFERENCE_CODE_KEY = "referenceCode";

    private PaymentRepository paymentRepository;
    private Payment firstPayment;
    private Payment secondPayment;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();

        Map<String, String> voucherData = new HashMap<>();
        voucherData.put(VOUCHER_CODE_KEY, "ESHOP1234ABC5678");
        firstPayment = new Payment(PAYMENT_ID_1, METHOD_VOUCHER_CODE, STATUS_SUCCESS, voucherData);

        Map<String, String> transferData = new HashMap<>();
        transferData.put(BANK_NAME_KEY, "BCA");
        transferData.put(REFERENCE_CODE_KEY, "REF-001");
        secondPayment = new Payment(PAYMENT_ID_2, METHOD_BANK_TRANSFER, STATUS_SUCCESS, transferData);
    }

    @Test
    void testSaveCreatePayment() {
        Payment savedPayment = paymentRepository.save(firstPayment);

        assertEquals(PAYMENT_ID_1, savedPayment.getId());
        assertEquals(PAYMENT_ID_1, paymentRepository.findById(PAYMENT_ID_1).getId());
        assertEquals(STATUS_SUCCESS, paymentRepository.findById(PAYMENT_ID_1).getStatus());
    }

    @Test
    void testSaveUpdatePayment() {
        paymentRepository.save(firstPayment);

        Payment updatedPayment = new Payment(
                PAYMENT_ID_1,
                METHOD_VOUCHER_CODE,
                STATUS_REJECTED,
                firstPayment.getPaymentData()
        );
        paymentRepository.save(updatedPayment);

        assertEquals(STATUS_REJECTED, paymentRepository.findById(PAYMENT_ID_1).getStatus());
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
        assertEquals(PAYMENT_ID_1, payments.get(0).getId());
        assertEquals(PAYMENT_ID_2, payments.get(1).getId());
    }
}
