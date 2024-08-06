package com.augefarma.controle_feira.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer{

    /**
     * Configures CORS settings for the application.
     *
     * @return a WebMvcConfigurer object with customized CORS settings
     */
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {

            /**
             * Adds CORS mappings to allow cross-origin requests from specified origins.
             *
             * @param registry the CorsRegistry object to configure CORS settings
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Configures CORS to allow requests from all paths
                registry.addMapping("/**")
                        // Permits requests from the specified origin (Angular app running on localhost:4200)
                        .allowedOrigins("http://localhost:4200")
                        // Allows all HTTP methods (GET, POST, PUT, DELETE, etc.)
                        .allowedMethods("*")
                        // Allows credentials (cookies, authorization headers, etc.) to be included in requests
                        .allowCredentials(true);
            }
        };
    }
}
