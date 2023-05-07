package workshop.spring.gtd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.discovery.enabled=false",
        "spring.cloud.config.enabled=false"
})
class CommandServiceApplicationTest {

    @Test
    void contextLoads() {
    }

}
