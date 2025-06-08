package com.palangwi.soup.controller.user;

import static com.palangwi.soup.utils.ApiUtils.success;

import com.palangwi.soup.dto.keyword.MyKeywordListResponseDto;
import com.palangwi.soup.dto.user.UserDeleteRequestDto;
import com.palangwi.soup.dto.user.UserAdditionalInfoRequestDto;
import com.palangwi.soup.dto.user.UserInitSettingResponseDto;
import com.palangwi.soup.dto.user.UserResponseDto;
import com.palangwi.soup.dto.user.UserUpdateRequestDto;
import com.palangwi.soup.security.JwtAuthentication;
import com.palangwi.soup.service.keyword.KeywordService;
import com.palangwi.soup.service.user.UserService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        userService.deleteAccount(userInfo.id(), request);
        return success();
    }

    @GetMapping("/me/keywords")
    public ApiResult<MyKeywordListResponseDto> getMyKeywords (
            @AuthenticationPrincipal JwtAuthentication userDetails,
            @PageableDefault(size = 20, sort = "createdDate", direction = Direction.DESC) Pageable pageable) {
        return success(keywordService.getMyKeywords(userDetails.id(), pageable));
    }
}