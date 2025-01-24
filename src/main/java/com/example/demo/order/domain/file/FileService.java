package com.example.demo.order.domain.file;

import com.example.demo.order.domain.OrderInput;

import java.io.IOException;

public interface FileService {
    void writeFile(OrderInput orderInput);
}
