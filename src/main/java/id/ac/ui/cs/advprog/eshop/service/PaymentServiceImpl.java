package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import id.ac.ui.cs.advprog.eshop.service.payment.BankTransferPaymentValidator;
import id.ac.ui.cs.advprog.eshop.service.payment.CashOnDeliveryPaymentValidator;
import id.ac.ui.cs.advprog.eshop.service.payment.PaymentDataValidator;
import id.ac.ui.cs.advprog.eshop.service.payment.VoucherCodePaymentValidator;
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
    private final Map<String, PaymentDataValidator> validators = Map.of(
            METHOD_VOUCHER_CODE, new VoucherCodePaymentValidator(),
            METHOD_BANK_TRANSFER, new BankTransferPaymentValidator(),
            METHOD_CASH_ON_DELIVERY, new CashOnDeliveryPaymentValidator()
    );

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
        synchronizeOrderStatus(payment.getId(), status);

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
        PaymentDataValidator validator = validators.get(method);
        if (validator == null) {
            return STATUS_REJECTED;
        }

        return validator.isValid(paymentData) ? STATUS_SUCCESS : STATUS_REJECTED;
    }

    private void synchronizeOrderStatus(String paymentId, String paymentStatus) {
        Order relatedOrder = orderByPaymentId.get(paymentId);
        if (relatedOrder == null) {
            return;
        }

        if (STATUS_SUCCESS.equals(paymentStatus)) {
            relatedOrder.setStatus(OrderStatus.SUCCESS.getValue());
        } else if (STATUS_REJECTED.equals(paymentStatus)) {
            relatedOrder.setStatus(OrderStatus.FAILED.getValue());
        }
    }
}
