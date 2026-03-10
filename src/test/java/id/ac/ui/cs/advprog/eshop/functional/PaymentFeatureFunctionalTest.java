package id.ac.ui.cs.advprog.eshop.functional;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class PaymentFeatureFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    @Autowired
    private PaymentService paymentService;

    private String baseUrl;
    private String paymentId;

    @BeforeEach
    void setUpTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);

        Product product = new Product();
        product.setProductId("product-" + UUID.randomUUID());
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);

        Order order = new Order(
                "order-" + UUID.randomUUID(),
                List.of(product),
                System.currentTimeMillis(),
                "Payment Tester"
        );

        Payment payment = paymentService.addPayment(
                order,
                "VOUCHER_CODE",
                Map.of("voucherCode", "ESHOP1234ABC5678")
        );
        paymentId = payment.getId();
    }

    @Test
    void paymentDetailFormShouldBeAccessible(ChromeDriver driver) {
        driver.get(baseUrl + "/payment/detail");

        WebElement paymentIdInput = driver.findElement(By.id("paymentIdInput"));
        assertTrue(paymentIdInput.isDisplayed());
    }

    @Test
    void paymentDetailByIdShouldShowPaymentInformation(ChromeDriver driver) {
        driver.get(baseUrl + "/payment/detail/" + paymentId);

        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(paymentId));
        assertTrue(pageSource.contains("VOUCHER_CODE"));
        assertTrue(pageSource.contains("SUCCESS"));
    }

    @Test
    void paymentAdminListShouldShowPayments(ChromeDriver driver) {
        driver.get(baseUrl + "/payment/admin/list");

        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(paymentId));
    }

    @Test
    void paymentAdminShouldAllowStatusUpdate(ChromeDriver driver) {
        driver.get(baseUrl + "/payment/admin/detail/" + paymentId);

        WebElement statusInput = driver.findElement(By.id("statusInput"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        statusInput.clear();
        statusInput.sendKeys("REJECTED");
        submitButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/payment/admin/detail/"));
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("REJECTED"));
    }
}
