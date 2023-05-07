package workshop.spring.gtd;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CommandServiceConfiguration {

    @Value("${api.common.version:TBD}")
    String apiVersion;

    @Value("${api.common.title:TBD}")
    String apiTitle;

    @Value("${api.common.description:TBD}")
    String apiDescription;

    @Value("${api.common.license:TBD}")
    String apiLicense;

    @Value("${api.common.licenseUrl:TBD}")
    String apiLicenseUrl;

    @Value("${api.common.contact.name:TBD}")
    String apiContactName;

    @Value("${api.common.contact.url:TBD}")
    String apiContactUrl;

    @Value("${api.common.contact.email:TBD}")
    String apiContactEmail;

    @Bean
    public OpenAPI openApiDocumentation() {
        return new OpenAPI()
                .info(new Info().title(apiTitle)
                        .description(apiDescription)
                        .version(apiVersion)
                        .contact(new Contact()
                                .name(apiContactName)
                                .url(apiContactUrl)
                                .email(apiContactEmail))
                        .license(new License()
                                .name(apiLicense)
                                .url(apiLicenseUrl)));
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
