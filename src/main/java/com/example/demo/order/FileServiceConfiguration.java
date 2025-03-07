package com.example.demo.order;

import com.example.demo.order.domain.file.FileService;
import com.example.demo.order.domain.file.FileServiceImpl;
import com.example.demo.order.domain.file.FileServiceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FileServiceConfiguration {

    @Bean
    FileService fileService() {
        return new FileServiceProxy(new FileServiceImpl());
    }
}
