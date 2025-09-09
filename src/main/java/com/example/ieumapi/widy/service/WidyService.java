package com.example.ieumapi.widy.service;

import com.example.ieumapi.file.FileStorageClient;
import com.example.ieumapi.file.dto.UploadImageResDto;
import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.util.SecurityUtils;
import com.example.ieumapi.group.domain.Group;
import com.example.ieumapi.group.repository.GroupRepository;
import com.example.ieumapi.group.service.GroupService;
import com.example.ieumapi.plan.domain.Plan;
import com.example.ieumapi.plan.repository.PlanRepository;
import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.widy.domain.Widy;
import com.example.ieumapi.widy.domain.WidyImage;
import com.example.ieumapi.widy.domain.WidyScope;
import com.example.ieumapi.widy.dto.RecentWidyResponseDto;
import com.example.ieumapi.widy.dto.WidyCountResponseDto;
import com.example.ieumapi.widy.dto.WidyCreateRequestDto;
import com.example.ieumapi.widy.dto.WidyResponseDto;
import com.example.ieumapi.widy.dto.WidyUpdateRequestDto;
import com.example.ieumapi.widy.exception.WidyErrorCode;
import com.example.ieumapi.widy.exception.WidyException;
import com.example.ieumapi.widy.repository.WidyImageRepository;
import com.example.ieumapi.widy.repository.WidyRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class WidyService {

    private final WidyRepository widyRepository;
    private final WidyImageRepository widyImageRepository;
    private final GroupService groupService;
    private final FileStorageClient fileStorageClient;
    private final PlanRepository planRepository;
    private final GroupRepository groupRepository;

    public WidyResponseDto createWidy(WidyCreateRequestDto requestDto, List<MultipartFile> images) {
        // Scope validation
        if (requestDto.getScope() == WidyScope.GROUP && requestDto.getGroupId() == null) {
            throw new WidyException(WidyErrorCode.GROUP_ID_REQUIRED);
        }
        Long userId = SecurityUtils.getCurrentUserId();
        // 1. Widy 엔티티 생성 및 저장
        Widy widy = requestDto.toEntity(userId);
        Widy savedWidy = widyRepository.save(widy);
        Long widyId = savedWidy.getWidyId();

        // 2. 이미지 업로드 및 정보 저장
        List<UploadImageResDto> uploadedImages = fileStorageClient.uploadMultipleImages(images);

        List<WidyImage> widyImages = uploadedImages.stream()
            .map(imageDto -> WidyImage.builder()
                .widyId(widyId) // 저장된 Widy의 ID를 설정
                .id(imageDto.getId())
                .originalName(imageDto.getOriginalName())
                .storedName(imageDto.getStoredName())
                .url(imageDto.getUrl())
                .build())
            .collect(Collectors.toList());

        widyImageRepository.saveAll(widyImages);

        // 3. 응답 DTO 생성
        return WidyResponseDto.builder()
            .widyId(widyId)
            .title(savedWidy.getTitle())
            .content(savedWidy.getContent())
            .address(savedWidy.getAddress())
            .images(uploadedImages)
            .build();
    }

    public WidyResponseDto update(Long id, WidyUpdateRequestDto dto) {
        Widy widy = widyRepository.findById(id)
            .orElseThrow(() -> new WidyException(WidyErrorCode.WIDY_NOT_FOUND));

        User user = SecurityUtils.getCurrentUser();
        if (!widy.getUserId().equals(user.getUserId())) {
            throw new WidyException(WidyErrorCode.FORBIDDEN);
        }

        // Scope validation
        if (dto.getScope() == WidyScope.GROUP && dto.getGroupId() == null) {
            throw new WidyException(WidyErrorCode.GROUP_ID_REQUIRED);
        }

        widy.update(dto.getTitle(), dto.getContent(), dto.getAddress());
        // DB에서 이미지를 다시 조회하는 헬퍼 메소드 호출
        return mapToWidyResponseDto(widy, user.getEmail() );
    }

    public void delete(Long id) {
        Widy widy = widyRepository.findById(id)
            .orElseThrow(() -> new WidyException(WidyErrorCode.WIDY_NOT_FOUND));

        Long userId = SecurityUtils.getCurrentUserId();
        if (!widy.getUserId().equals(userId)) {
            throw new WidyException(WidyErrorCode.FORBIDDEN);
        }
        widyRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public WidyResponseDto getWidy(Long widyId) {
        Widy widy = widyRepository.findById(widyId)
            .orElseThrow(() -> new WidyException(WidyErrorCode.WIDY_NOT_FOUND));

        User currentUser = SecurityUtils.getCurrentUser();

        // Access control based on scope
        if (widy.getScope() == WidyScope.PRIVATE && !widy.getUserId().equals(currentUser.getUserId())) {
            throw new WidyException(WidyErrorCode.FORBIDDEN);
        }
        if (widy.getScope() == WidyScope.GROUP && !groupService.getMyGroups().contains(
            widy.getGroupId())) { // Assuming getGroupIdForUser is a method that retrieves the group ID for a user
            throw new WidyException(WidyErrorCode.FORBIDDEN);
        }

        return mapToWidyResponseDto(widy, currentUser.getEmail());
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<WidyResponseDto> getMyWidysCursor(int size, String cursor) {
        Long userId = SecurityUtils.getCurrentUserId();

        long cursorMillis = decodeCursor(cursor);
        LocalDateTime cursorTime = (cursorMillis <= 0)
            ? LocalDateTime.now()
            : LocalDateTime.ofInstant(Instant.ofEpochMilli(cursorMillis), ZoneOffset.UTC);

        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Widy> widys = widyRepository
            .findByUserIdAndScopeAndCreatedAtLessThanOrderByCreatedAtDesc(userId, WidyScope.PRIVATE,
                cursorTime, pageable);

        return getWidyResponseDtoCursorPageResponse(size, widys);
    }

    private CursorPageResponse<WidyResponseDto> getWidyResponseDtoCursorPageResponse(int size,
        List<Widy> widys) {
        boolean hasNext = widys.size() > size;
        if (hasNext) {
            widys = widys.subList(0, size);
        }

        List<Long> widyIds = widys.stream()
            .map(Widy::getWidyId)
            .collect(Collectors.toList());

        Map<Long, List<WidyImage>> imagesByWidyId = widyIds.isEmpty()
            ? Collections.emptyMap()
            : widyImageRepository.findByWidyIdIn(widyIds).stream()
                .collect(Collectors.groupingBy(WidyImage::getWidyId));

        List<WidyResponseDto> data = widys.stream()
            .map(widy -> {
                List<WidyImage> widyImages = imagesByWidyId.getOrDefault(widy.getWidyId(),
                    Collections.emptyList());
                return mapToWidyResponseDto(widy, widyImages, SecurityUtils.getCurrentUser().getEmail());
            })
            .collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext) {
            long lastMillis = widys.get(widys.size() - 1)
                .getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            nextCursor = Base64.getEncoder()
                .encodeToString(String.valueOf(lastMillis).getBytes(StandardCharsets.UTF_8));
        }

        return new CursorPageResponse<>(data, nextCursor, hasNext);
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<WidyResponseDto> getPublicWidysCursor(int size, String cursor) {
        // 1. 커서 디코딩 및 페이지 정보 설정
        long cursorMillis = decodeCursor(cursor); // 이제 이 메소드를 찾을 수 있습니다.
        LocalDateTime cursorTime = (cursorMillis <= 0)
            ? LocalDateTime.now()
            : LocalDateTime.ofInstant(Instant.ofEpochMilli(cursorMillis), ZoneOffset.UTC);

        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Widy> widys = widyRepository
            .findByScopeAndCreatedAtLessThanOrderByCreatedAtDesc(WidyScope.PUBLIC, cursorTime,
                pageable);

        return getWidyResponseDtoCursorPageResponse(size, widys);
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<WidyResponseDto> getGroupFeedCursor(int size, String cursor) {

        // 1. 사용자가 속한 그룹 ID 목록 가져오기
        List<Long> groupIds = groupService.getMyGroups();

        // 2. 만약 사용자가 속한 그룹이 하나도 없다면, 비어있는 페이지 반환
        if (groupIds.isEmpty()) {
            return new CursorPageResponse<>(Collections.emptyList(), null, false);
        }

        // 3. 커서 디코딩 및 페이지 정보 설정
        long cursorMillis = decodeCursor(cursor);
        LocalDateTime cursorTime = (cursorMillis <= 0)
            ? LocalDateTime.now()
            : LocalDateTime.ofInstant(Instant.ofEpochMilli(cursorMillis), ZoneOffset.UTC);

        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Widy> widys = widyRepository
            .findByScopeAndGroupIdInAndCreatedAtLessThanOrderByCreatedAtDesc(WidyScope.GROUP,
                groupIds, cursorTime, pageable);

        return getWidyResponseDtoCursorPageResponse(size, widys);
    }

    @Transactional(readOnly = true)
    public WidyCountResponseDto getVisibleWidyCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> groupIds = Optional.ofNullable(groupService.getMyGroups())
            .orElse(Collections.emptyList());
        long count = widyRepository.countVisibleWidysForUser(userId, groupIds);
        return new WidyCountResponseDto(count);
    }

    @Transactional(readOnly = true)
    public List<RecentWidyResponseDto> getRecentVisibleWidys() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> groupIds = Optional.ofNullable(groupService.getMyGroups())
            .orElse(Collections.emptyList());

        LocalDateTime startDateTime = LocalDate.now().minusMonths(1).atStartOfDay();
        Pageable pageable = PageRequest.of(0, 7);

        List<Widy> widys = widyRepository.findVisibleWidysForUser(userId, groupIds, startDateTime,
            pageable);

        if (widys.isEmpty()) {
            return Collections.emptyList();
        }

        // N+1 방지를 위해 필요한 plan, group 정보를 한번에 조회
        List<Long> planIds = widys.stream()
            .map(Widy::getPlanId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        Map<Long, String> planIdToNameMap = planIds.isEmpty() ? Collections.emptyMap() :
            planRepository.findAllById(planIds).stream()
                .collect(Collectors.toMap(Plan::getPlanId, Plan::getTitle));

        List<Long> groupIdsForName = widys.stream()
            .map(Widy::getGroupId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        Map<Long, String> groupIdToNameMap = groupIdsForName.isEmpty() ? Collections.emptyMap() :
            groupRepository.findAllById(groupIdsForName).stream()
                .collect(Collectors.toMap(Group::getGroupId, Group::getName));

        return widys.stream()
            .map(widy -> {
                String planName = Optional.ofNullable(widy.getPlanId())
                    .map(planIdToNameMap::get)
                    .orElse("연결된 일정 없음");
                String groupName = Optional.ofNullable(widy.getGroupId())
                    .map(groupIdToNameMap::get)
                    .orElse("연결된 그룹 없음");

                return RecentWidyResponseDto.builder()
                    .widyId(widy.getWidyId())
                    .title(widy.getTitle())
                    .planName(planName)
                    .groupName(groupName)
                    .build();
            })
            .collect(Collectors.toList());
    }

    // ==================== 추가된 헬퍼 메소드들 ====================

    /**
     * Base64로 인코딩된 커서 문자열을 디코딩하여 long 타입의 시간(ms)으로 변환합니다.
     */
    private long decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return 0L;
        }
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            return Long.parseLong(decoded);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cursor 형식이 잘못되었습니다.");
        }
    }

    /**
     * Widy 엔티티를 받아 이미지를 조회한 후 DTO로 변환하는 헬퍼 메소드. 단일 Widy를 조회하는 경우에 사용됩니다.
     */
    private WidyResponseDto mapToWidyResponseDto(Widy widy, String email) {
        List<WidyImage> images = widyImageRepository.findByWidyId(widy.getWidyId());
        return mapToWidyResponseDto(widy, images, email);
    }

    /**
     * Widy 엔티티와 이미지 목록을 받아 DTO로 변환하는 헬퍼 메소드. DB 조회를 하지 않으므로 N+1 문제 해결에 사용됩니다.
     */
    private WidyResponseDto mapToWidyResponseDto(Widy widy, List<WidyImage> images, String email) {
        List<UploadImageResDto> imageDtos = images.stream()
            .map(image -> UploadImageResDto.builder()
                .id(image.getId())
                .originalName(image.getOriginalName())
                .storedName(image.getStoredName())
                .url(image.getUrl())
                .build())
            .collect(Collectors.toList());

        return WidyResponseDto.builder()
            .email(email)
            .widyId(widy.getWidyId())
            .title(widy.getTitle())
            .content(widy.getContent())
            .address(widy.getAddress())
            .images(imageDtos)
            .build();
    }
}
