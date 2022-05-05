package com.francin.linux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LinuxApplication {
    public static void main(String[] args) {
        SpringApplication.run(LinuxApplication.class, args);
    }
}
