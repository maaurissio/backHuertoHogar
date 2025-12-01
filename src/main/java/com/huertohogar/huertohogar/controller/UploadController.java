package com.huertohogar.huertohogar.controller;

import com.huertohogar.huertohogar.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/imagen")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("imagen") MultipartFile imagen,
            @RequestParam(value = "folder", defaultValue = "productos") String folder) throws IOException {
        
        Map<String, String> result = cloudinaryService.uploadImage(imagen, folder);
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "url", result.get("url"),
                "publicId", result.get("publicId")
        ));
    }
}
