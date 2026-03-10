package id.ac.ui.cs.advprog.eshop.service.payment;

import java.util.Map;

public class BankTransferPaymentValidator implements PaymentDataValidator {
    private static final String BANK_NAME_KEY = "bankName";
    private static final String REFERENCE_CODE_KEY = "referenceCode";

    @Override
    public boolean isValid(Map<String, String> paymentData) {
        return hasValue(paymentData, BANK_NAME_KEY) && hasValue(paymentData, REFERENCE_CODE_KEY);
    }

    private boolean hasValue(Map<String, String> paymentData, String key) {
        if (paymentData == null) {
            return false;
        }

        String value = paymentData.get(key);
        return value != null && !value.trim().isEmpty();
    }
}
