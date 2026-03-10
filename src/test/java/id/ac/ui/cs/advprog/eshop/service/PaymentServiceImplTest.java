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
        paymentData.put("voucherCode", "ESHOP1234ABC5678");

        Payment payment = paymentService.addPayment(order, "VOUCHER_CODE", paymentData);

        assertNotNull(payment.getId());
        assertEquals("VOUCHER_CODE", payment.getMethod());
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals("SUCCESS", order.getStatus());
        assertEquals(payment.getId(), paymentService.getPayment(payment.getId()).getId());
    }

    @Test
    void testAddPaymentWithInvalidVoucherCode() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "INVALID-CODE");

        Payment payment = paymentService.addPayment(order, "VOUCHER_CODE", paymentData);

        assertEquals("REJECTED", payment.getStatus());
        assertEquals("FAILED", order.getStatus());
    }

    @Test
    void testAddPaymentWithValidBankTransferData() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("bankName", "BCA");
        paymentData.put("referenceCode", "INV-12345");

        Payment payment = paymentService.addPayment(order, "BANK_TRANSFER", paymentData);

        assertEquals("SUCCESS", payment.getStatus());
        assertEquals("SUCCESS", order.getStatus());
    }

    @Test
    void testAddPaymentWithIncompleteBankTransferData() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("bankName", "BCA");
        paymentData.put("referenceCode", "");

        Payment payment = paymentService.addPayment(order, "BANK_TRANSFER", paymentData);

        assertEquals("REJECTED", payment.getStatus());
        assertEquals("FAILED", order.getStatus());
    }

    @Test
    void testSetStatusToRejectedShouldSetOrderToFailed() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("bankName", "BCA");
        paymentData.put("referenceCode", "INV-12345");
        Payment payment = paymentService.addPayment(order, "BANK_TRANSFER", paymentData);

        paymentService.setStatus(payment, "REJECTED");

        assertEquals("REJECTED", payment.getStatus());
        assertEquals("FAILED", order.getStatus());
    }

    @Test
    void testGetAllPayments() {
        Map<String, String> voucherData = new HashMap<>();
        voucherData.put("voucherCode", "ESHOP1234ABC5678");
        paymentService.addPayment(order, "VOUCHER_CODE", voucherData);

        Order secondOrder = createOrder("order-2");
        Map<String, String> transferData = new HashMap<>();
        transferData.put("bankName", "Mandiri");
        transferData.put("referenceCode", "INV-99999");
        paymentService.addPayment(secondOrder, "BANK_TRANSFER", transferData);

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
