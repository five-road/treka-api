package com.example.ieumapi.localplace.service;

import com.example.ieumapi.file.FileStorageClient;
import com.example.ieumapi.file.dto.UploadImageResDto;
import com.example.ieumapi.global.util.SecurityUtils;
import com.example.ieumapi.localplace.domain.LocalPlace;
import com.example.ieumapi.localplace.domain.LocalPlaceImage;
import com.example.ieumapi.localplace.dto.LocalPlaceCreateRequest;
import com.example.ieumapi.localplace.dto.LocalPlaceResponse;
import com.example.ieumapi.localplace.dto.LocalPlaceUpdateRequest;
import com.example.ieumapi.localplace.exception.LocalPlaceErrorCode;
import com.example.ieumapi.localplace.exception.LocalPlaceException;
import com.example.ieumapi.localplace.repository.LocalPlaceImageRepository;
import com.example.ieumapi.localplace.repository.LocalPlaceRepository;
import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.user.repository.UserRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LocalPlaceService {

    private final LocalPlaceRepository localPlaceRepository;
    private final LocalPlaceImageRepository localPlaceImageRepository;
    private final UserRepository userRepository;
    private final FileStorageClient fileStorageClient;

    public LocalPlaceResponse createPlace(LocalPlaceCreateRequest request, List<MultipartFile> images) {
        if (images != null && images.size() > 3) {
            throw new IllegalArgumentException("이미지는 최대 3개까지 업로드할 수 있습니다.");
        }

        User user = userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalPlace localPlace = LocalPlace.builder()
            .name(request.getName())
            .description(request.getDescription())
            .address(request.getAddress())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .userId(user.getUserId())
            .userNickName(user.getNickName())
            .build();

        localPlaceRepository.save(localPlace);

        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            List<UploadImageResDto> uploadImageResDtos = fileStorageClient.uploadMultipleImages(images);

            uploadImageResDtos.forEach(uploadImageResDto -> {
                LocalPlaceImage image = LocalPlaceImage.builder().localPlace(localPlace).imageUrl(uploadImageResDto.getUrl()).build();
                imageUrls.add(uploadImageResDto.getUrl());
                localPlaceImageRepository.save(image);
            });
        }

        return LocalPlaceResponse.from(localPlace, imageUrls ,user.getName());
    }

    @Transactional(readOnly = true)
    public LocalPlaceResponse getPlace(Long placeId) {
        LocalPlace localPlace = localPlaceRepository.findById(placeId)
            .orElseThrow(() -> new LocalPlaceException(LocalPlaceErrorCode.LOCAL_PLACE_NOT_FOUND));
        List<String> imageUrls = localPlace.getImages().stream().map(LocalPlaceImage::getImageUrl).collect(Collectors.toList());
        return LocalPlaceResponse.from(localPlace, imageUrls, localPlace.getUserNickName());
    }

    public LocalPlaceResponse updatePlace(Long placeId, LocalPlaceUpdateRequest request) {
        LocalPlace localPlace = localPlaceRepository.findById(placeId).orElseThrow(() -> new LocalPlaceException(LocalPlaceErrorCode.LOCAL_PLACE_NOT_FOUND));
        validateAuthor(localPlace);

        localPlace.update(request.getName(), request.getDescription(), request.getAddress());

        List<String> imageUrls = localPlace.getImages().stream().map(LocalPlaceImage::getImageUrl).collect(Collectors.toList());
        return LocalPlaceResponse.from(localPlace, imageUrls, localPlace.getUserNickName());
    }

    public void deletePlace(Long placeId) {
        LocalPlace localPlace = localPlaceRepository.findById(placeId).orElseThrow(() -> new LocalPlaceException(LocalPlaceErrorCode.LOCAL_PLACE_NOT_FOUND));
        validateAuthor(localPlace);

        localPlace.getImages().forEach(image -> fileStorageClient.deleteImage(image.getImageUrl()));
        localPlaceRepository.delete(localPlace);
    }

    @Transactional(readOnly = true)
    public List<LocalPlaceResponse> findNearbyPlaces(double latitude, double longitude) {
        List<LocalPlace> nearbyPlaces = localPlaceRepository.findNearbyPlaces(latitude, longitude, 1.0);

        if (nearbyPlaces.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> placeIds = nearbyPlaces.stream().map(LocalPlace::getPlaceId).collect(Collectors.toList());
        List<LocalPlaceImage> images = localPlaceImageRepository.findByLocalPlace_PlaceIdIn(placeIds);

        Map<Long, List<String>> imageUrlsMap = images.stream()
            .collect(Collectors.groupingBy(image -> image.getLocalPlace().getPlaceId(),
                Collectors.mapping(LocalPlaceImage::getImageUrl, Collectors.toList())));

        return nearbyPlaces.stream()
            .map(place -> LocalPlaceResponse.from(
                place,
                imageUrlsMap.getOrDefault(place.getPlaceId(), Collections.emptyList()),
                place.getUserNickName())
            )
            .collect(Collectors.toList());
    }

    private void validateAuthor(LocalPlace localPlace) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!localPlace.getUserId().equals(currentUserId)) {
            throw new LocalPlaceException(LocalPlaceErrorCode.FORBIDDEN);
        }
    }
}
