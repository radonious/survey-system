package edu.forms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FormsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormsApplication.class, args);
    }
}
