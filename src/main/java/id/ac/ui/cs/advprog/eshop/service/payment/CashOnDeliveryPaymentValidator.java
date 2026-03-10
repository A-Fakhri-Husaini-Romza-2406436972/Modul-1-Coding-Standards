package id.ac.ui.cs.advprog.eshop.service.payment;

import java.util.Map;

public class CashOnDeliveryPaymentValidator implements PaymentDataValidator {
    private static final String ADDRESS_KEY = "address";
    private static final String DELIVERY_FEE_KEY = "deliveryFee";

    @Override
    public boolean isValid(Map<String, String> paymentData) {
        return hasValue(paymentData, ADDRESS_KEY) && hasValue(paymentData, DELIVERY_FEE_KEY);
    }

    private boolean hasValue(Map<String, String> paymentData, String key) {
        if (paymentData == null) {
            return false;
        }

        String value = paymentData.get(key);
        return value != null && !value.trim().isEmpty();
    }
}
