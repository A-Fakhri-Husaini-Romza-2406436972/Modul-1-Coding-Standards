package id.ac.ui.cs.advprog.eshop.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {

    WAITING_PAYMENT("WAITING_PAYMENT"),
    FAILED("FAILED"),
    SUCCESS("SUCCESS"),
    CANCELLED("CANCELLED");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public static boolean contains(String param) {
        if (param == null) {
            return false;
        }

        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.getValue().equals(param)) {
                return true;
            }
        }
        return false;
    }
}
