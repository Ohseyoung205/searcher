package com.saltlux.khnp.searcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class KHNPSearcherApplication {
    public static void main(String[] args) {
        SpringApplication.run(KHNPSearcherApplication.class, args);
    }
}
