package com.aiboomi.edupath.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI edupathOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("EduPath API")
                        .description("APIs to import and retrieve student academic and extracurricular data. Use the import endpoints to upload Excel files and the student endpoints to retrieve normalized data.")
                        .version("v1.0.0")
                        .contact(new Contact().name("EduPath Team").email("dev@edupath.local"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .servers(List.of(new Server().url("http://localhost:8080").description("Local server")))
                .addTagsItem(new Tag().name("Students").description("Operations to manage and fetch student related data"))
                .addTagsItem(new Tag().name("Imports").description("Endpoints to import data from Excel files"))
                .externalDocs(new ExternalDocumentation().description("Project README").url("https://example.com/edupath/README"));
    }
}
