package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class OrderFeatureFunctionalTest {

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

        WebElement authorInput = driver.findElement(By.id("authorInput"));
        WebElement productNameInput = driver.findElement(By.id("productNameInput"));
        WebElement productQuantityInput = driver.findElement(By.id("productQuantityInput"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        authorInput.clear();
        authorInput.sendKeys("Order Tester");
        productNameInput.clear();
        productNameInput.sendKeys("Sampo Cap Bambang");
        productQuantityInput.clear();
        productQuantityInput.sendKeys("2");
        submitButton.click();

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/order/history"));
    }

    @Test
    void historyShouldShowOrdersByAuthor(ChromeDriver driver) {
        createOrder(driver, "History Tester", "Sabun Cap Usep", "3");
        createOrder(driver, "Another Tester", "Pasta Gigi", "1");

        driver.get(baseUrl + "/order/history");
        WebElement authorInput = driver.findElement(By.id("authorInput"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        authorInput.clear();
        authorInput.sendKeys("History Tester");
        submitButton.click();

        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("Author: History Tester"));
        assertTrue(pageSource.contains("WAITING_PAYMENT"));
        assertTrue(pageSource.contains("Pay"));
    }

    private void createOrder(ChromeDriver driver, String author, String productName, String quantity) {
        driver.get(baseUrl + "/order/create");
        driver.findElement(By.id("authorInput")).sendKeys(author);
        driver.findElement(By.id("productNameInput")).sendKeys(productName);
        driver.findElement(By.id("productQuantityInput")).sendKeys(quantity);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }
}
