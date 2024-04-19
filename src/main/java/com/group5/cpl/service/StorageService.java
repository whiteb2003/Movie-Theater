package com.group5.cpl.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void init();
    void store (MultipartFile file);
}
