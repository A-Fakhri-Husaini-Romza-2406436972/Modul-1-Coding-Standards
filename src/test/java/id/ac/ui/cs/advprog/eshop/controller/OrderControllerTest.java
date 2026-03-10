package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void createOrderPageShouldReturnCreateOrderView() throws Exception {
        mockMvc.perform(get("/order/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("CreateOrder"));
    }

    @Test
    void createOrderPostShouldCreateOrderAndRedirect() throws Exception {
        mockMvc.perform(post("/order/create")
                        .param("author", "Order Tester")
                        .param("productName", "Sampo Cap Bambang")
                        .param("productQuantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/order/history"));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderService).createOrder(orderCaptor.capture());
        Order createdOrder = orderCaptor.getValue();
        assertEquals("Order Tester", createdOrder.getAuthor());
        assertEquals("Sampo Cap Bambang", createdOrder.getProducts().get(0).getProductName());
        assertEquals(2, createdOrder.getProducts().get(0).getProductQuantity());
    }

    @Test
    void orderHistoryFormShouldReturnHistoryView() throws Exception {
        mockMvc.perform(get("/order/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("OrderHistoryForm"));
    }

    @Test
    void orderHistoryPostShouldReturnOrderListByAuthor() throws Exception {
        Order firstOrder = createOrder("order-1", "History Tester");
        Order secondOrder = createOrder("order-2", "History Tester");
        when(orderService.findAllByAuthor("History Tester")).thenReturn(List.of(firstOrder, secondOrder));

        mockMvc.perform(post("/order/history")
                        .param("author", "History Tester"))
                .andExpect(status().isOk())
                .andExpect(view().name("OrderList"))
                .andExpect(model().attribute("author", "History Tester"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("orders", hasSize(2)));
    }

    private Order createOrder(String orderId, String author) {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);

        return new Order(orderId, List.of(product), 1708560000L, author);
    }
}
