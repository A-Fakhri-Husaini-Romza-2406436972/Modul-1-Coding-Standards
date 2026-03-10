package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
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
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    private static final String HISTORY_TESTER = "History Tester";
    private static final String METHOD_VOUCHER_CODE = "VOUCHER_CODE";

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
        Order firstOrder = createOrder("order-1", HISTORY_TESTER);
        Order secondOrder = createOrder("order-2", HISTORY_TESTER);
        when(orderService.findAllByAuthor(HISTORY_TESTER)).thenReturn(List.of(firstOrder, secondOrder));

        mockMvc.perform(post("/order/history")
                        .param("author", HISTORY_TESTER))
                .andExpect(status().isOk())
                .andExpect(view().name("OrderList"))
                .andExpect(model().attribute("author", HISTORY_TESTER))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("orders", hasSize(2)));
    }

    @Test
    void orderPayPageShouldReturnPayOrderViewWithOrder() throws Exception {
        Order order = createOrder("order-3", "Pay Tester");
        when(orderService.findById("order-3")).thenReturn(order);

        mockMvc.perform(get("/order/pay/order-3"))
                .andExpect(status().isOk())
                .andExpect(view().name("PayOrder"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("order", samePropertyValuesAs(order)));
    }

    @Test
    void orderPayPostShouldCreatePaymentAndReturnPaymentResultView() throws Exception {
        Order order = createOrder("order-4", "Pay Tester");
        when(orderService.findById("order-4")).thenReturn(order);

        Map<String, String> paymentData = Map.of(
                "method", METHOD_VOUCHER_CODE,
                "voucherCode", "ESHOP1234ABC5678"
        );
        Payment payment = new Payment("payment-1", METHOD_VOUCHER_CODE, "SUCCESS", paymentData);
        when(paymentService.addPayment(eq(order), eq(METHOD_VOUCHER_CODE), any()))
                .thenReturn(payment);

        mockMvc.perform(post("/order/pay/order-4")
                        .param("method", METHOD_VOUCHER_CODE)
                        .param("voucherCode", "ESHOP1234ABC5678"))
                .andExpect(status().isOk())
                .andExpect(view().name("PayOrderResult"))
                .andExpect(model().attributeExists("payment"))
                .andExpect(model().attribute("payment", samePropertyValuesAs(payment)));
    }

    private Order createOrder(String orderId, String author) {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);

        return new Order(orderId, List.of(product), 1708560000L, author);
    }
}
