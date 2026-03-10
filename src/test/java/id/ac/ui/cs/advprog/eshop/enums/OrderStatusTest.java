package id.ac.ui.cs.advprog.eshop.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderStatusTest {

    @Test
    void containsShouldReturnFalseWhenParamIsNull() {
        assertFalse(OrderStatus.contains(null));
    }

    @Test
    void containsShouldReturnTrueWhenStatusExists() {
        assertTrue(OrderStatus.contains(OrderStatus.SUCCESS.getValue()));
    }

    @Test
    void containsShouldReturnFalseWhenStatusDoesNotExist() {
        assertFalse(OrderStatus.contains("NOT_A_STATUS"));
    }
}
