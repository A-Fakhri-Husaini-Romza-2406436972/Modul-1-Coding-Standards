package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_REJECTED = "REJECTED";

    private static final String METHOD_VOUCHER_CODE = "VOUCHER_CODE";
    private static final String METHOD_BANK_TRANSFER = "BANK_TRANSFER";
    private static final String METHOD_CASH_ON_DELIVERY = "CASH_ON_DELIVERY";

    private final PaymentRepository paymentRepository;
    private final Map<String, Order> orderByPaymentId = new HashMap<>();

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                method,
                STATUS_PENDING,
                paymentData
        );

        paymentRepository.save(payment);
        orderByPaymentId.put(payment.getId(), order);

        String evaluatedStatus = evaluateInitialStatus(method, paymentData);
        return setStatus(payment, evaluatedStatus);
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);
        paymentRepository.save(payment);

        Order relatedOrder = orderByPaymentId.get(payment.getId());
        if (relatedOrder != null) {
            if (STATUS_SUCCESS.equals(status)) {
                relatedOrder.setStatus(OrderStatus.SUCCESS.getValue());
            } else if (STATUS_REJECTED.equals(status)) {
                relatedOrder.setStatus(OrderStatus.FAILED.getValue());
            }
        }

        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    private String evaluateInitialStatus(String method, Map<String, String> paymentData) {
        if (METHOD_VOUCHER_CODE.equals(method)) {
            return isValidVoucherCode(paymentData) ? STATUS_SUCCESS : STATUS_REJECTED;
        }

        if (METHOD_BANK_TRANSFER.equals(method)) {
            return hasValue(paymentData, "bankName") && hasValue(paymentData, "referenceCode")
                    ? STATUS_SUCCESS
                    : STATUS_REJECTED;
        }

        if (METHOD_CASH_ON_DELIVERY.equals(method)) {
            return hasValue(paymentData, "address") && hasValue(paymentData, "deliveryFee")
                    ? STATUS_SUCCESS
                    : STATUS_REJECTED;
        }

        return STATUS_REJECTED;
    }

    private boolean isValidVoucherCode(Map<String, String> paymentData) {
        String voucherCode = paymentData == null ? null : paymentData.get("voucherCode");
        if (voucherCode == null) {
            return false;
        }

        if (voucherCode.length() != 16 || !voucherCode.startsWith("ESHOP")) {
            return false;
        }

        long numericCount = voucherCode.chars().filter(Character::isDigit).count();
        return numericCount == 8;
    }

    private boolean hasValue(Map<String, String> paymentData, String key) {
        if (paymentData == null) {
            return false;
        }

        String value = paymentData.get(key);
        return value != null && !value.isEmpty();
    }
}
