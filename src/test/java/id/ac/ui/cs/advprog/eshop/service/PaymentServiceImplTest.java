package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentServiceImplTest {

    private static final String METHOD_VOUCHER_CODE = "VOUCHER_CODE";
    private static final String METHOD_BANK_TRANSFER = "BANK_TRANSFER";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_FAILED = "FAILED";
    private static final String VOUCHER_CODE_KEY = "voucherCode";
    private static final String BANK_NAME_KEY = "bankName";
    private static final String REFERENCE_CODE_KEY = "referenceCode";

    private PaymentService paymentService;
    private Order order;

    @BeforeEach
    void setUp() {
        PaymentRepository paymentRepository = new PaymentRepository();
        paymentService = new PaymentServiceImpl(paymentRepository);
        order = createOrder("order-1");
    }

    @Test
    void testAddPaymentWithValidVoucherCode() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put(VOUCHER_CODE_KEY, "ESHOP1234ABC5678");

        Payment payment = paymentService.addPayment(order, METHOD_VOUCHER_CODE, paymentData);

        assertNotNull(payment.getId());
        assertEquals(METHOD_VOUCHER_CODE, payment.getMethod());
        assertEquals(STATUS_SUCCESS, payment.getStatus());
        assertEquals(STATUS_SUCCESS, order.getStatus());
        assertEquals(payment.getId(), paymentService.getPayment(payment.getId()).getId());
    }

    @Test
    void testAddPaymentWithInvalidVoucherCode() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put(VOUCHER_CODE_KEY, "INVALID-CODE");

        Payment payment = paymentService.addPayment(order, METHOD_VOUCHER_CODE, paymentData);

        assertEquals(STATUS_REJECTED, payment.getStatus());
        assertEquals(STATUS_FAILED, order.getStatus());
    }

    @Test
    void testAddPaymentWithValidBankTransferData() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put(BANK_NAME_KEY, "BCA");
        paymentData.put(REFERENCE_CODE_KEY, "INV-12345");

        Payment payment = paymentService.addPayment(order, METHOD_BANK_TRANSFER, paymentData);

        assertEquals(STATUS_SUCCESS, payment.getStatus());
        assertEquals(STATUS_SUCCESS, order.getStatus());
    }

    @Test
    void testAddPaymentWithIncompleteBankTransferData() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put(BANK_NAME_KEY, "BCA");
        paymentData.put(REFERENCE_CODE_KEY, "");

        Payment payment = paymentService.addPayment(order, METHOD_BANK_TRANSFER, paymentData);

        assertEquals(STATUS_REJECTED, payment.getStatus());
        assertEquals(STATUS_FAILED, order.getStatus());
    }

    @Test
    void testSetStatusToRejectedShouldSetOrderToFailed() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put(BANK_NAME_KEY, "BCA");
        paymentData.put(REFERENCE_CODE_KEY, "INV-12345");
        Payment payment = paymentService.addPayment(order, METHOD_BANK_TRANSFER, paymentData);

        paymentService.setStatus(payment, STATUS_REJECTED);

        assertEquals(STATUS_REJECTED, payment.getStatus());
        assertEquals(STATUS_FAILED, order.getStatus());
    }

    @Test
    void testGetAllPayments() {
        Map<String, String> voucherData = new HashMap<>();
        voucherData.put(VOUCHER_CODE_KEY, "ESHOP1234ABC5678");
        paymentService.addPayment(order, METHOD_VOUCHER_CODE, voucherData);

        Order secondOrder = createOrder("order-2");
        Map<String, String> transferData = new HashMap<>();
        transferData.put(BANK_NAME_KEY, "Mandiri");
        transferData.put(REFERENCE_CODE_KEY, "INV-99999");
        paymentService.addPayment(secondOrder, METHOD_BANK_TRANSFER, transferData);

        List<Payment> allPayments = paymentService.getAllPayments();
        assertEquals(2, allPayments.size());
    }

    private Order createOrder(String orderId) {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);
        products.add(product);

        return new Order(orderId, products, 1708560000L, "Safira Sudrajat");
    }
}
