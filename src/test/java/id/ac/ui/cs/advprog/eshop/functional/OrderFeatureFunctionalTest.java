package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class OrderFeatureFunctionalTest {
    private static final String AUTHOR_INPUT_ID = "authorInput";
    private static final String ORDER_HISTORY_PATH = "/order/history";
    private static final String SUBMIT_BUTTON_SELECTOR = "button[type='submit']";

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    @BeforeEach
    void setUpTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    @Test
    void createOrderShouldRedirectToHistoryPage(ChromeDriver driver) {
        driver.get(baseUrl + "/order/create");

        WebElement authorInput = driver.findElement(By.id(AUTHOR_INPUT_ID));
        WebElement productNameInput = driver.findElement(By.id("productNameInput"));
        WebElement productQuantityInput = driver.findElement(By.id("productQuantityInput"));
        WebElement submitButton = driver.findElement(By.cssSelector(SUBMIT_BUTTON_SELECTOR));

        authorInput.clear();
        authorInput.sendKeys("Order Tester");
        productNameInput.clear();
        productNameInput.sendKeys("Sampo Cap Bambang");
        productQuantityInput.clear();
        productQuantityInput.sendKeys("2");
        submitButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains(ORDER_HISTORY_PATH));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(ORDER_HISTORY_PATH));
    }

    @Test
    void historyShouldShowOrdersByAuthor(ChromeDriver driver) {
        createOrder(driver, "History Tester", "Sabun Cap Usep", "3");
        createOrder(driver, "Another Tester", "Pasta Gigi", "1");

        driver.get(baseUrl + ORDER_HISTORY_PATH);
        WebElement authorInput = driver.findElement(By.id(AUTHOR_INPUT_ID));
        WebElement submitButton = driver.findElement(By.cssSelector(SUBMIT_BUTTON_SELECTOR));
        authorInput.clear();
        authorInput.sendKeys("History Tester");
        submitButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".order-id")));
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("History Tester"));
        assertTrue(pageSource.contains("WAITING_PAYMENT"));
        assertTrue(pageSource.contains("Pay"));
    }

    @Test
    void orderPayShouldReturnPaymentIdPage(ChromeDriver driver) {
        createOrder(driver, "Payment Flow Tester", "Sikat Gigi", "2");

        driver.get(baseUrl + ORDER_HISTORY_PATH);
        driver.findElement(By.id(AUTHOR_INPUT_ID)).sendKeys("Payment Flow Tester");
        driver.findElement(By.cssSelector(SUBMIT_BUTTON_SELECTOR)).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".order-id")));
        String orderId = driver.findElement(By.cssSelector(".order-id")).getText();
        driver.get(baseUrl + "/order/pay/" + orderId);

        WebElement methodInput = driver.findElement(By.id("methodInput"));
        WebElement voucherCodeInput = driver.findElement(By.id("voucherCodeInput"));
        Select methodSelect = new Select(methodInput);
        methodSelect.selectByValue("VOUCHER_CODE");
        voucherCodeInput.sendKeys("ESHOP1234ABC5678");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("paymentIdText")));
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("Payment ID"));
        assertTrue(pageSource.contains("SUCCESS"));
    }

    private void createOrder(ChromeDriver driver, String author, String productName, String quantity) {
        driver.get(baseUrl + "/order/create");
        driver.findElement(By.id("authorInput")).sendKeys(author);
        driver.findElement(By.id("productNameInput")).sendKeys(productName);
        driver.findElement(By.id("productQuantityInput")).sendKeys(quantity);
        driver.findElement(By.cssSelector(SUBMIT_BUTTON_SELECTOR)).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains(ORDER_HISTORY_PATH));
    }
}
