package com.example.hsa11_hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.example.hsa11_hw")
public class Hsa11HwApplication {

    public static void main(String[] args) {
        SpringApplication.run(Hsa11HwApplication.class, args);
    }

}
