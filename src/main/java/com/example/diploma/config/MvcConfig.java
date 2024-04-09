package com.example.diploma.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Bean
    public RestTemplate getRestTemplate()
    {
        return new RestTemplate();
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCachePeriod(31556926)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
        registry.addResourceHandler("/static/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(31556926)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
}
