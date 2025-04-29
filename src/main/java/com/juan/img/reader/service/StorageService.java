package com.juan.img.reader.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {
    void store(MultipartFile file);
    Path load(String filename);
}