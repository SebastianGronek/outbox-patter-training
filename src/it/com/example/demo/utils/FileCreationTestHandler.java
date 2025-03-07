package com.example.demo.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class FileCreationTestHandler {

    public static void verifyFileCreation(String path) {
        Path filePath = Paths.get(path);
        assertThat(Files.exists(filePath)).isTrue();
        BasicFileAttributes basicFileAttributes;
        try {
            basicFileAttributes = Files.readAttributes(filePath, BasicFileAttributes.class);
        } catch (Exception e) {
            throw new RuntimeException("Error reading file attributes in verifyFileCreation() method", e);
        }
        assertThat(basicFileAttributes.creationTime()).isNotNull();

    }

    public static void deleteTestFiles(String filePath) {
        listAllFilesInDirectory(filePath).forEach(file -> {
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                throw new RuntimeException("Error deleting file: " + file, e);
            }
        });
    }

    private static List<Path> listAllFilesInDirectory(String filePath) {
        try {
            return Files.list(Paths.get(filePath))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error listing files in directory: " + filePath, e);
        }
    }
}
