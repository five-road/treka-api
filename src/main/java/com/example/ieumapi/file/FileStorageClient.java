package com.example.ieumapi.file;

import static com.example.ieumapi.file.FileStorageException.FileStorageError.UPLOAD_FAILED;

import com.example.ieumapi.file.dto.UploadImageResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class FileStorageClient {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${file.storage.url}")
    private String fileStorageUrl;

    // 단일 이미지 업로드
    public List<UploadImageResDto> uploadImage(MultipartFile image) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });

        try {
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<UploadImageResDto> response = restTemplate.exchange(fileStorageUrl, HttpMethod.POST, requestEntity, UploadImageResDto.class);
            return Collections.singletonList(response.getBody());
        } catch (Exception e) {
            throw new FileStorageException(UPLOAD_FAILED);
        }
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

        try {
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(fileStorageUrl, HttpMethod.PUT, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            throw new FileStorageException(UPLOAD_FAILED);
        }
    }

    // 이미지 삭제
    public void deleteImage(String url) {
        String deleteUrl = fileStorageUrl + "?url=" + url;
        restTemplate.delete(deleteUrl);
    }

    // 다중 이미지 업로드
    public List<UploadImageResDto> uploadMultipleImages(List<MultipartFile> images) {
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
                throw new FileStorageException(UPLOAD_FAILED);
            }
        });

        try {
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<List<UploadImageResDto>> response = restTemplate.exchange(fileStorageUrl + "/multiple",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<UploadImageResDto>>() {}
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("오류 원인", e);
            throw new FileStorageException(UPLOAD_FAILED);
        }
    }
}
