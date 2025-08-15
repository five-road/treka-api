package com.example.ieumapi.file;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileStorageClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String fileStorageUrl = "http://localhost:8081/image";

    // 단일 이미지 업로드
    public String uploadImage(MultipartFile image) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(fileStorageUrl, HttpMethod.POST, requestEntity, String.class);
        return response.getBody();
    }

    // 이미지 수정
    public String updateImage(MultipartFile image, String deleteUrl) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });
        body.add("deleteUrl", "{\"url\":\"" + deleteUrl + "\"}");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(fileStorageUrl, HttpMethod.PUT, requestEntity, String.class);
        return response.getBody();
    }

    // 이미지 삭제
    public void deleteImage(String url) {
        String deleteUrl = fileStorageUrl + "?url=" + url;
        restTemplate.delete(deleteUrl);
    }

    // 다중 이미지 업로드
    public List<String> uploadMultipleImages(List<MultipartFile> images) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        images.forEach(image -> {
            try {
                body.add("images", new ByteArrayResource(image.getBytes()) {
                    @Override
                    public String getFilename() {
                        return image.getOriginalFilename();
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<List<String>> response = restTemplate.exchange(fileStorageUrl + "/multiple", HttpMethod.POST, requestEntity, (Class<List<String>>) (Class<?>) List.class);
        return response.getBody();
    }
}