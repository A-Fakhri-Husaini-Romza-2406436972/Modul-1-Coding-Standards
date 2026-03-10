package id.ac.ui.cs.advprog.eshop.service.payment;

import java.util.Map;

public class VoucherCodePaymentValidator implements PaymentDataValidator {
    private static final String VOUCHER_CODE_KEY = "voucherCode";

    @Override
    public boolean isValid(Map<String, String> paymentData) {
        String voucherCode = paymentData == null ? null : paymentData.get(VOUCHER_CODE_KEY);
        if (voucherCode == null) {
            return false;
        }

        if (voucherCode.length() != 16 || !voucherCode.startsWith("ESHOP")) {
            return false;
        }

        long numericCount = voucherCode.chars().filter(Character::isDigit).count();
        return numericCount == 8;
    }
}
