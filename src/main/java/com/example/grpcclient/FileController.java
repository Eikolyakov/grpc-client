package com.example.grpcclient;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileUploadService fileUploadService;

    @PostMapping("/")
    public void uploadNewFile(){
        try {
            fileUploadService.uploadFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
