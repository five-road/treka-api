package com.example.ieumapi.localplace.service;

import com.example.ieumapi.file.FileStorageClient;
import com.example.ieumapi.file.dto.UploadImageResDto;
import com.example.ieumapi.global.util.SecurityUtils;
import com.example.ieumapi.localplace.domain.LocalPlace;
import com.example.ieumapi.localplace.domain.LocalPlaceImage;
import com.example.ieumapi.localplace.domain.PlaceCategory;
import com.example.ieumapi.localplace.domain.Source;
import com.example.ieumapi.localplace.dto.LocalPlaceCreateRequest;
import com.example.ieumapi.localplace.dto.LocalPlaceResponse;
import com.example.ieumapi.localplace.dto.LocalPlaceSearchResponse;
import com.example.ieumapi.localplace.dto.LocalPlaceUpdateRequest;
import com.example.ieumapi.localplace.exception.LocalPlaceErrorCode;
import com.example.ieumapi.localplace.exception.LocalPlaceException;
import com.example.ieumapi.localplace.feign.KtoFeignClient;
import com.example.ieumapi.localplace.repository.LocalPlaceImageRepository;
import com.example.ieumapi.localplace.repository.LocalPlaceRepository;
import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional
public class LocalPlaceService {

    private final LocalPlaceRepository localPlaceRepository;
    private final LocalPlaceImageRepository localPlaceImageRepository;
    private final UserRepository userRepository;
    private final FileStorageClient fileStorageClient;
    private final KtoFeignClient ktoFeignClient;
    private final ObjectMapper objectMapper;

    @Value("${api.kto.service-key}")
    private String ktoServiceKey;

    public LocalPlaceResponse createPlace(LocalPlaceCreateRequest request,
        List<MultipartFile> images) {
        if (images != null && images.size() > 3) {
            throw new IllegalArgumentException("이미지는 최대 3개까지 업로드할 수 있습니다.");
        }

        User user = userRepository.findById(SecurityUtils.getCurrentUserId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalPlace localPlace = LocalPlace.builder()
            .name(request.getName())
            .description(request.getDescription())
            .address(request.getAddress())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .userId(user.getUserId())
            .userNickName(user.getNickName())
            .source(Source.USER)
            .ktoContentId(null)
            .category(request.getCategory())
            .build();

        localPlaceRepository.save(localPlace);

        List<String> imageUrls = uploadAndSaveImages(localPlace, images);

        return LocalPlaceResponse.from(localPlace, imageUrls, user.getNickName());
    }

    public List<LocalPlaceSearchResponse> searchLocalPlaces(String keyword) {
        // 1. KTO API 호출 및 데이터 처리
        List<LocalPlaceSearchResponse> ktoPlaces = getPlacesFromKto(keyword);

        // 2. 사용자 등록 장소 검색
        List<LocalPlace> userPlaces = localPlaceRepository.findByNameContainingIgnoreCaseAndSource(
            keyword, Source.USER);

        List<LocalPlaceSearchResponse> userPlaceResponses = userPlaces.stream()
            .map(this::convertPlaceToSearchResponse)
            .toList();

        // 3. 결과 병합 및 중복 제거
        Set<LocalPlaceSearchResponse> combinedPlaces = new HashSet<>(ktoPlaces);
        combinedPlaces.addAll(userPlaceResponses);

        return new ArrayList<>(combinedPlaces);
    }

    private List<LocalPlaceSearchResponse> getPlacesFromKto(String keyword) {
        String responseJson = ktoFeignClient.searchByKeyword(ktoServiceKey, keyword, "json", "ETC",
            "treka", "39");

        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isMissingNode() || !items.isArray()) {
                return Collections.emptyList();
            }

            return StreamSupport.stream(items.spliterator(), false)
                .map(this::createOrUpdatePlaceFromKtoNode)
                .collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse KTO API response", e);
        }
    }

    private LocalPlaceSearchResponse createOrUpdatePlaceFromKtoNode(JsonNode node) {
        String ktoContentId = node.path("contentid").asText();
        long contentTypeId = node.path("contenttypeid").asLong();

        LocalPlace localPlace = localPlaceRepository.findByKtoContentId(ktoContentId)
            .orElseGet(() -> {

                LocalPlace newPlace = LocalPlace.builder()
                    .name(node.path("title").asText())
                    .address(node.path("addr1").asText() + node.path("addr2").asText())
                    .latitude(node.path("mapy").asDouble())
                    .longitude(node.path("mapx").asDouble())
                    .contentTypeId(contentTypeId)
                    .userId(0L) // 시스템(KTO)에서 등록한 경우 userId는 0 또는 특정 값으로 지정
                    .userNickName("한국관광공사")
                    .source(Source.KTO)
                    .ktoContentId(ktoContentId)
                    .category(PlaceCategory.fromCode(contentTypeId))
                    .build();

                LocalPlace savedPlace = localPlaceRepository.save(newPlace);

                String imageUrl = node.path("firstimage").asText();
                String sumNailUrl = node.path("firstimage2").asText();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    LocalPlaceImage image = LocalPlaceImage.builder()
                        .localPlace(newPlace)
                        .sumNailUrl(sumNailUrl)
                        .imageUrl(imageUrl)
                        .build();
                    localPlaceImageRepository.save(image);
                }
                return savedPlace;
            });

        return convertPlaceToSearchResponse(localPlace);
    }


    @Transactional(readOnly = true)
    public LocalPlaceResponse getPlace(Long placeId, String ktoContentId, PlaceCategory contentTypeId) {

        LocalPlace localPlace = localPlaceRepository.findById(placeId)
            .orElseThrow(() -> new LocalPlaceException(LocalPlaceErrorCode.LOCAL_PLACE_NOT_FOUND));

        if(ktoContentId != null && contentTypeId != null) {
            String reponseDetailCommonJson = ktoFeignClient.getLocalPlaceDetailCommon(
                ktoServiceKey,
                "json",
                "ETC",
                "treka",
                ktoContentId
            );

            String reponseDetailIntroJson = ktoFeignClient.getLocalPlaceDetailIntro(
                ktoServiceKey,
                "json",
                "ETC",
                "treka",
                ktoContentId,
                String.valueOf(contentTypeId.getCode())
            );

            String overview = "";
            try {
                JsonNode commonRoot = objectMapper.readTree(reponseDetailCommonJson);
                JsonNode commonItems = commonRoot.path("response").path("body").path("items").path("item");

                JsonNode introRoot = objectMapper.readTree(reponseDetailIntroJson);
                JsonNode introItems = introRoot.path("response").path("body").path("items").path("item");

                if (commonItems.isArray() && commonItems.size() > 0) {
                    overview = commonItems.get(0).path("overview").asText("");

                    String businessHour = "";
                    String parking = "";
                    switch (contentTypeId.getCode()){
                        case 39: // 음식점
                            businessHour = introItems.get(0).path("opentimefood").asText("");
                            parking = introItems.get(0).path("parkingfood").asText("");
                            break;
                        case 12: // 관광지
                            businessHour = introItems.get(0).path("opendate").asText("");
                            parking = introItems.get(0).path("parking").asText("");
                            break;
                        case 38: //쇼핑
                            businessHour = introItems.get(0).path("opentime").asText("");
                            parking = introItems.get(0).path("parkingshopping").asText("");
                            break;
                        case 32: //숙박
                            parking = introItems.get(0).path("parkinglodging")
                                .asText("");
                            String checkintime = introItems.get(0).path("checkintime").asText("");
                            String checkouttime = introItems.get(0).path("checkouttime").asText("");
                            businessHour = "checkInTime : " + checkintime + "checkOutTime : " + checkouttime;
                    }

                    localPlace.updateBusinessHours(businessHour);
                    localPlace.updateDescription(overview);
                    localPlace.updateParking(parking);
                }

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        List<String> imageUrls = localPlaceImageRepository.findByLocalPlace_PlaceId(placeId)
            .stream()
            .map(LocalPlaceImage::getImageUrl)
            .toList();


        return LocalPlaceResponse.from(localPlace, imageUrls, localPlace.getUserNickName());
    }

    public LocalPlaceResponse updatePlace(Long placeId, LocalPlaceUpdateRequest request,
        List<MultipartFile> images) {
        if (images != null && images.size() > 3) {
            throw new IllegalArgumentException("이미지는 최대 3개까지 업로드할 수 있습니다.");
        }

        LocalPlace localPlace = localPlaceRepository.findById(placeId)
            .orElseThrow(() -> new LocalPlaceException(LocalPlaceErrorCode.LOCAL_PLACE_NOT_FOUND));
        validateAuthor(localPlace);

        localPlace.update(request.getName(), request.getDescription(), request.getAddress(),
            request.getCategory());

        List<String> imageUrls;

        if (images != null && !images.isEmpty()) {
            List<LocalPlaceImage> existingImages = localPlaceImageRepository.findByLocalPlace_PlaceId(
                placeId);
            if (existingImages != null && !existingImages.isEmpty()) {
                existingImages.forEach(image -> fileStorageClient.deleteImage(image.getImageUrl()));
                localPlaceImageRepository.deleteAll(existingImages);
            }
            imageUrls = uploadAndSaveImages(localPlace, images);
        } else {
            imageUrls = localPlaceImageRepository.findByLocalPlace_PlaceId(placeId).stream()
                .map(LocalPlaceImage::getImageUrl)
                .toList();
        }

        return LocalPlaceResponse.from(localPlace, imageUrls, localPlace.getUserNickName());
    }

    public void deletePlace(Long placeId) {
        LocalPlace localPlace = localPlaceRepository.findById(placeId)
            .orElseThrow(() -> new LocalPlaceException(LocalPlaceErrorCode.LOCAL_PLACE_NOT_FOUND));
        validateAuthor(localPlace);

        List<LocalPlaceImage> images = localPlaceImageRepository.findByLocalPlace_PlaceId(placeId);
        images.forEach(image -> fileStorageClient.deleteImage(image.getImageUrl()));
        localPlaceImageRepository.deleteAll(images);

        localPlaceRepository.delete(localPlace);
    }

    @Transactional
    public List<LocalPlaceSearchResponse> findNearbyPlaces(double latitude, double longitude) {
        // 1. KTO API를 통해 근처 장소 가져오기 (반경 1km)
        String responseJson = ktoFeignClient.searchByLocation(ktoServiceKey, longitude, latitude,
            1000, "json", "ETC", "treka");
        List<LocalPlaceSearchResponse> ktoNearbyPlaces = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isArray()) {
                for (JsonNode item : items) {
                    ktoNearbyPlaces.add(createOrUpdatePlaceFromKtoNode(item));
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse KTO API response", e);
        }

        // 2. DB에서 1km 반경 내의 모든 장소 가져오기
        List<LocalPlace> dbNearbyPlaces = localPlaceRepository.findNearbyPlaces(latitude, longitude,
            1.0);

        List<LocalPlaceSearchResponse> dbNearbyPlaceResponses = dbNearbyPlaces.stream()
            .map(this::convertPlaceToSearchResponse)
            .toList();

        // 3. 두 리스트를 합치고 중복 제거
        Set<LocalPlaceSearchResponse> combinedPlaces = new HashSet<>(ktoNearbyPlaces);
        combinedPlaces.addAll(dbNearbyPlaceResponses);

        return new ArrayList<>(combinedPlaces);
    }

    private LocalPlaceSearchResponse convertPlaceToSearchResponse(LocalPlace place) {
        List<LocalPlaceImage> images = localPlaceImageRepository.findByLocalPlace_PlaceId(
            place.getPlaceId());
        String imageUrl = images.isEmpty() ? null : images.get(0).getImageUrl();
        String sumNailUrl = images.isEmpty() ? null : images.get(0).getSumNailUrl();

        return new LocalPlaceSearchResponse(
            String.valueOf(place.getPlaceId()),
            place.getName(),
            place.getAddress(),
            place.getLatitude(),
            place.getLongitude(),
            place.getUserId(),
            place.getUserNickName(),
            place.getSource(),
            place.getKtoContentId(),
            place.getContentTypeId(),
            place.getCategory(),
            imageUrl,
            sumNailUrl
        );
    }

    private List<LocalPlaceResponse> getPlaceDetails(List<LocalPlace> places) {
        if (places.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> placeIds = places.stream().map(LocalPlace::getPlaceId)
            .toList();
        List<LocalPlaceImage> images = localPlaceImageRepository.findByLocalPlace_PlaceIdIn(
            placeIds);

        Map<Long, List<String>> imageUrlsMap = images.stream()
            .collect(Collectors.groupingBy(image -> image.getLocalPlace().getPlaceId(),
                Collectors.mapping(LocalPlaceImage::getImageUrl, Collectors.toList())));

        return places.stream()
            .map(place -> LocalPlaceResponse.from(
                place,
                imageUrlsMap.getOrDefault(place.getPlaceId(), Collections.emptyList()),
                place.getUserNickName())
            )
            .toList();
    }

    private void validateAuthor(LocalPlace localPlace) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!localPlace.getUserId().equals(currentUserId)) {
            throw new LocalPlaceException(LocalPlaceErrorCode.FORBIDDEN);
        }
    }

    private List<String> uploadAndSaveImages(LocalPlace localPlace, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }

        List<UploadImageResDto> uploadImageResDtos = fileStorageClient.uploadMultipleImages(images);
        List<LocalPlaceImage> newImages = uploadImageResDtos.stream()
            .map(uploadImageResDto -> LocalPlaceImage.builder()
                .localPlace(localPlace)
                .imageUrl(uploadImageResDto.getUrl())
                .build())
            .toList();
        localPlaceImageRepository.saveAll(newImages);
        return newImages.stream().map(LocalPlaceImage::getImageUrl).toList();
    }
}
