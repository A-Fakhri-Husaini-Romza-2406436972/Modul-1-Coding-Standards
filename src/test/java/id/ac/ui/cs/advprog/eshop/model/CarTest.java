package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarTest {

    private Car car;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setCarId("car-1");
        car.setCarName("Toyota Avanza");
        car.setCarColor("Black");
        car.setCarQuantity(3);
    }

    @Test
    void testCarFields() {
        assertEquals("car-1", car.getCarId());
        assertEquals("Toyota Avanza", car.getCarName());
        assertEquals("Black", car.getCarColor());
        assertEquals(3, car.getCarQuantity());
    }
}
