package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PaymentRepository {

    private final Map<String, Payment> paymentData = new LinkedHashMap<>();

    public Payment save(Payment payment) {
        paymentData.put(payment.getId(), payment);
        return payment;
    }

    public Payment findById(String id) {
        return paymentData.get(id);
    }

    public List<Payment> findAll() {
        return new ArrayList<>(paymentData.values());
    }
}
