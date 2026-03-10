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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentServiceImplTest {

    private static final String METHOD_VOUCHER_CODE = "VOUCHER_CODE";
    private static final String METHOD_BANK_TRANSFER = "BANK_TRANSFER";
    private static final String METHOD_CASH_ON_DELIVERY = "CASH_ON_DELIVERY";
    private static final String METHOD_UNKNOWN = "E_WALLET";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_WAITING_PAYMENT = "WAITING_PAYMENT";
    private static final String VOUCHER_CODE_KEY = "voucherCode";
    private static final String BANK_NAME_KEY = "bankName";
    private static final String REFERENCE_CODE_KEY = "referenceCode";
    private static final String ADDRESS_KEY = "address";
    private static final String DELIVERY_FEE_KEY = "deliveryFee";

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
    void testAddPaymentWithInvalidVoucherNumericCount() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put(VOUCHER_CODE_KEY, "ESHOPABCD1234EFG");

        Payment payment = paymentService.addPayment(order, METHOD_VOUCHER_CODE, paymentData);

        assertEquals(STATUS_REJECTED, payment.getStatus());
        assertEquals(STATUS_FAILED, order.getStatus());
    }

    @Test
    void testAddPaymentWithInvalidVoucherPrefix() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put(VOUCHER_CODE_KEY, "ABCDE1234ABC5678");

        Payment payment = paymentService.addPayment(order, METHOD_VOUCHER_CODE, paymentData);

        assertEquals(STATUS_REJECTED, payment.getStatus());
        assertEquals(STATUS_FAILED, order.getStatus());
    }

    @Test
    void testAddPaymentWithNullVoucherData() {
        Payment payment = paymentService.addPayment(order, METHOD_VOUCHER_CODE, null);

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
    void testAddPaymentWithMissingBankName() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put(REFERENCE_CODE_KEY, "INV-12345");

        Payment payment = paymentService.addPayment(order, METHOD_BANK_TRANSFER, paymentData);

        assertEquals(STATUS_REJECTED, payment.getStatus());
        assertEquals(STATUS_FAILED, order.getStatus());
    }

    @Test
    void testAddPaymentWithValidCashOnDeliveryData() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put(ADDRESS_KEY, "Jl. Margonda Raya");
        paymentData.put(DELIVERY_FEE_KEY, "10000");

        Payment payment = paymentService.addPayment(order, METHOD_CASH_ON_DELIVERY, paymentData);

        assertEquals(STATUS_SUCCESS, payment.getStatus());
        assertEquals(STATUS_SUCCESS, order.getStatus());
    }

    @Test
    void testAddPaymentWithInvalidCashOnDeliveryData() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put(ADDRESS_KEY, "");
        paymentData.put(DELIVERY_FEE_KEY, "10000");

        Payment payment = paymentService.addPayment(order, METHOD_CASH_ON_DELIVERY, paymentData);

        assertEquals(STATUS_REJECTED, payment.getStatus());
        assertEquals(STATUS_FAILED, order.getStatus());
    }

    @Test
    void testAddPaymentWithMissingDeliveryFee() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put(ADDRESS_KEY, "Jl. Margonda Raya");
        paymentData.put(DELIVERY_FEE_KEY, "");

        Payment payment = paymentService.addPayment(order, METHOD_CASH_ON_DELIVERY, paymentData);

        assertEquals(STATUS_REJECTED, payment.getStatus());
        assertEquals(STATUS_FAILED, order.getStatus());
    }

    @Test
    void testAddPaymentWithNullCashOnDeliveryData() {
        Payment payment = paymentService.addPayment(order, METHOD_CASH_ON_DELIVERY, null);

        assertEquals(STATUS_REJECTED, payment.getStatus());
        assertEquals(STATUS_FAILED, order.getStatus());
    }

    @Test
    void testAddPaymentWithUnknownMethodShouldReject() {
        Payment payment = paymentService.addPayment(order, METHOD_UNKNOWN, Map.of());

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
    void testSetStatusOtherValueShouldNotChangeOrderStatus() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put(BANK_NAME_KEY, "BCA");
        paymentData.put(REFERENCE_CODE_KEY, "INV-12345");
        Payment payment = paymentService.addPayment(order, METHOD_BANK_TRANSFER, paymentData);

        paymentService.setStatus(payment, STATUS_PENDING);

        assertEquals(STATUS_PENDING, payment.getStatus());
        assertEquals(STATUS_SUCCESS, order.getStatus());
    }

    @Test
    void testSetStatusForPaymentWithoutRelatedOrder() {
        Payment externalPayment = new Payment(
                "external-payment",
                METHOD_VOUCHER_CODE,
                STATUS_PENDING,
                Map.of(VOUCHER_CODE_KEY, "ESHOP1234ABC5678")
        );

        assertDoesNotThrow(() -> paymentService.setStatus(externalPayment, STATUS_SUCCESS));
        assertEquals(STATUS_SUCCESS, externalPayment.getStatus());
        assertEquals(STATUS_WAITING_PAYMENT, order.getStatus());
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
