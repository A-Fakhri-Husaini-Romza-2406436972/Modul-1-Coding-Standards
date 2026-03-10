package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    private static final String ORDER_ID = "13652556-012a-4c07-b546-54eb1396d79b";
    private static final String AUTHOR = "Safira Sudrajati";

    private List<Product> products;

    @BeforeEach
    void setUp() {
        this.products = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product1.setProductName("Sampo Cap Bambang");
        product1.setProductQuantity(2);
        Product product2 = new Product();
        product2.setProductId("a2c62328-4a37-4664-83c7-f32db8620155");
        product2.setProductName("Sabun Cap Usep");
        product2.setProductQuantity(1);
        this.products.add(product1);
        this.products.add(product2);
    }

    @Test
    void testCreateOrderEmptyProduct() {
        this.products.clear();

        assertThrows(IllegalArgumentException.class, () -> {
            new Order(
                    ORDER_ID,
                    this.products,
                    1708560000L,
                    AUTHOR
            );
        });
    }

    @Test
    void testCreateOrderNullProduct() {
        assertThrows(IllegalArgumentException.class, () ->
                new Order(
                        ORDER_ID,
                        null,
                        1708560000L,
                        AUTHOR
                )
        );
    }

    @Test
    void testCreateOrderDefaultStatus() {
        Order order = new Order(
                ORDER_ID,
                this.products,
                1708560000L,
                AUTHOR
        );

        assertSame(this.products, order.getProducts());
        assertEquals(2, order.getProducts().size());
        assertEquals("Sampo Cap Bambang", order.getProducts().get(0).getProductName());
        assertEquals("Sabun Cap Usep", order.getProducts().get(1).getProductName());

        assertEquals(ORDER_ID, order.getId());
        assertEquals(1708560000L, order.getOrderTime());
        assertEquals(AUTHOR, order.getAuthor());
        assertEquals("WAITING_PAYMENT", order.getStatus());
    }

    @Test
    void testCreateOrderSuccessStatus() {
        Order order = new Order(
                ORDER_ID,
                this.products,
                1708560000L,
                AUTHOR,
                "SUCCESS"
        );

        assertEquals("SUCCESS", order.getStatus());
    }

    @Test
    void testCreateOrderInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order(
                    ORDER_ID,
                    this.products,
                    1708560000L,
                    AUTHOR,
                    "MEOW"
            );
        });
    }

    @Test
    void testSetStatusToCancelled() {
        Order order = new Order(
                ORDER_ID,
                this.products,
                1708560000L,
                AUTHOR
        );

        order.setStatus("CANCELLED");

        assertEquals("CANCELLED", order.getStatus());
    }

    @Test
    void testSetStatusToInvalidStatus() {
        Order order = new Order(
                ORDER_ID,
                this.products,
                1708560000L,
                AUTHOR
        );

        assertThrows(IllegalArgumentException.class, () -> order.setStatus("MEOW"));
    }
}
