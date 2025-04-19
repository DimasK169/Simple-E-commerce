package com.product.app.service.interfacing;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorage {
    String storeFile(MultipartFile file) throws IOException;
}
