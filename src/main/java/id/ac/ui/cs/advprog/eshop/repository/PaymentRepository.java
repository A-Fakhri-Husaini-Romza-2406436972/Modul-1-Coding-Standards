package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentRepository {

    private final List<Payment> paymentData = new ArrayList<>();

    public Payment save(Payment payment) {
        int index = 0;
        for (Payment savedPayment : paymentData) {
            if (savedPayment.getId().equals(payment.getId())) {
                paymentData.remove(index);
                paymentData.add(index, payment);
                return payment;
            }
            index += 1;
        }

        paymentData.add(payment);
        return payment;
    }

    public Payment findById(String id) {
        for (Payment savedPayment : paymentData) {
            if (savedPayment.getId().equals(id)) {
                return savedPayment;
            }
        }
        return null;
    }

    public List<Payment> findAll() {
        return paymentData;
    }
}
