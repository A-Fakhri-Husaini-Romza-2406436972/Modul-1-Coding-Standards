package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void paymentDetailFormShouldReturnPaymentDetailFormView() throws Exception {
        mockMvc.perform(get("/payment/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("PaymentDetailForm"));
    }

    @Test
    void paymentDetailFormWithPaymentIdShouldRedirectToDetailPath() throws Exception {
        mockMvc.perform(get("/payment/detail")
                        .param("paymentId", "payment-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/payment/detail/payment-1"));
    }

    @Test
    void paymentDetailByIdShouldReturnPaymentDetailView() throws Exception {
        Payment payment = createPayment("payment-2", "VOUCHER_CODE", "SUCCESS");
        when(paymentService.getPayment("payment-2")).thenReturn(payment);

        mockMvc.perform(get("/payment/detail/payment-2"))
                .andExpect(status().isOk())
                .andExpect(view().name("PaymentDetail"))
                .andExpect(model().attributeExists("payment"))
                .andExpect(model().attribute("payment", samePropertyValuesAs(payment)));
    }

    @Test
    void paymentAdminListShouldReturnAllPayments() throws Exception {
        Payment firstPayment = createPayment("payment-3", "VOUCHER_CODE", "SUCCESS");
        Payment secondPayment = createPayment("payment-4", "BANK_TRANSFER", "REJECTED");
        when(paymentService.getAllPayments()).thenReturn(List.of(firstPayment, secondPayment));

        mockMvc.perform(get("/payment/admin/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("PaymentAdminList"))
                .andExpect(model().attributeExists("payments"))
                .andExpect(model().attribute("payments", hasSize(2)));
    }

    @Test
    void paymentAdminDetailShouldReturnPaymentAdminDetailView() throws Exception {
        Payment payment = createPayment("payment-5", "BANK_TRANSFER", "SUCCESS");
        when(paymentService.getPayment("payment-5")).thenReturn(payment);

        mockMvc.perform(get("/payment/admin/detail/payment-5"))
                .andExpect(status().isOk())
                .andExpect(view().name("PaymentAdminDetail"))
                .andExpect(model().attributeExists("payment"))
                .andExpect(model().attribute("payment", samePropertyValuesAs(payment)));
    }

    @Test
    void paymentSetStatusShouldCallServiceAndRedirectToAdminDetail() throws Exception {
        Payment payment = createPayment("payment-6", "VOUCHER_CODE", "SUCCESS");
        when(paymentService.getPayment("payment-6")).thenReturn(payment);
        when(paymentService.setStatus(payment, "REJECTED")).thenReturn(payment);

        mockMvc.perform(post("/payment/admin/set-status/payment-6")
                        .param("status", "REJECTED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/payment/admin/detail/payment-6"));

        verify(paymentService).getPayment("payment-6");
        verify(paymentService).setStatus(eq(payment), eq("REJECTED"));
    }

    private Payment createPayment(String paymentId, String method, String status) {
        return new Payment(paymentId, method, status, Map.of("voucherCode", "ESHOP1234ABC5678"));
    }
}
