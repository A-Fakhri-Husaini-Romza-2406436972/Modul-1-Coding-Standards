package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping("/create")
    public String createOrderPage() {
        return "CreateOrder";
    }

    @PostMapping("/create")
    public String createOrderPost(
            @RequestParam("author") String author,
            @RequestParam("productName") String productName,
            @RequestParam("productQuantity") int productQuantity) {
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName(productName);
        product.setProductQuantity(productQuantity);

        Order order = new Order(
                UUID.randomUUID().toString(),
                List.of(product),
                System.currentTimeMillis(),
                author
        );
        orderService.createOrder(order);

        return "redirect:/order/history";
    }

    @GetMapping("/history")
    public String orderHistoryFormPage() {
        return "OrderHistoryForm";
    }

    @PostMapping("/history")
    public String orderHistoryPost(@RequestParam("author") String author, Model model) {
        List<Order> orders = orderService.findAllByAuthor(author);
        model.addAttribute("author", author);
        model.addAttribute("orders", orders);
        return "OrderList";
    }

    @GetMapping("/pay/{orderId}")
    public String orderPayPage(@PathVariable String orderId, Model model) {
        Order order = orderService.findById(orderId);
        model.addAttribute("order", order);
        return "PayOrder";
    }

    @PostMapping("/pay/{orderId}")
    public String orderPayPost(
            @PathVariable String orderId,
            @RequestParam("method") String method,
            @RequestParam Map<String, String> requestData,
            Model model) {
        Order order = orderService.findById(orderId);
        Payment payment = paymentService.addPayment(order, method, requestData);
        model.addAttribute("payment", payment);
        return "PayOrderResult";
    }
}
