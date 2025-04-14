package com.product.app.service.interfacing;

import com.product.app.config.FileStorageProperties;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

public interface FileStorage {
    String storeFile(MultipartFile file) throws IOException;
}
