package id.ac.ui.cs.advprog.eshop;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@SpringBootTest
class EshopApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainShouldCallSpringApplicationRun() {
        String[] args = new String[]{"--spring.main.web-application-type=none"};

        try (MockedStatic<SpringApplication> mockedSpringApplication = org.mockito.Mockito.mockStatic(SpringApplication.class)) {
            mockedSpringApplication.when(() -> SpringApplication.run(EshopApplication.class, args))
                    .thenReturn(mock(org.springframework.context.ConfigurableApplicationContext.class));

            EshopApplication.main(args);

            mockedSpringApplication.verify(() -> SpringApplication.run(EshopApplication.class, args), times(1));
        }
    }

}
