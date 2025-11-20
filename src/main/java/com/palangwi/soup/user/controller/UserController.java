package com.palangwi.soup.user.controller;

import com.palangwi.soup.common.security.JwtAuthentication;
import com.palangwi.soup.common.utils.ApiUtils.ApiResult;
import com.palangwi.soup.keyword.dto.MyKeywordListResponseDto;
import com.palangwi.soup.keyword.service.KeywordService;
import com.palangwi.soup.user.dto.*;
import com.palangwi.soup.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.palangwi.soup.common.utils.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final KeywordService keywordService;

    @PostMapping("/init")
    public ApiResult<UserInitSettingResponseDto> initAdditionalInfo(@AuthenticationPrincipal JwtAuthentication userInfo,
                                                                    @Valid @RequestBody UserAdditionalInfoRequestDto request) {
        return success(userService.initAdditionalUserInfo(userInfo.id(), request));
    }

    @PatchMapping
    public ApiResult<UserResponseDto> updateUser(@AuthenticationPrincipal JwtAuthentication userInfo,
                                                 @Valid @RequestBody UserUpdateRequestDto request) {
        return success(userService.updateUserInfo(userInfo.id(), request));
    }

    @GetMapping
    public ApiResult<UserResponseDto> getUserInfo(@AuthenticationPrincipal JwtAuthentication userInfo) {
        return success(userService.getUserInfo(userInfo.id()));
    }

    @GetMapping("/check-nickname")
    public ApiResult<Boolean> validateNickname(@RequestParam(name = "nickname") String nickname) {
        return success(userService.isAvailableNickname(nickname)); // 길이, 형식만 체크
    }

    @PostMapping("/delete")
    public ApiResult<Void> deleteAccount(@AuthenticationPrincipal JwtAuthentication userInfo, @RequestBody UserDeleteRequestDto request) {
        userService.deleteAccount(userInfo.id());
        return success();
    }

    @GetMapping("/me/keywords")
    public ApiResult<MyKeywordListResponseDto> getMyKeywords (
            @AuthenticationPrincipal JwtAuthentication userDetails,
            @PageableDefault(size = 20, sort = "createdDate", direction = Direction.DESC) Pageable pageable) {
        return success(keywordService.getMyKeywords(userDetails.id(), pageable));
    }
}