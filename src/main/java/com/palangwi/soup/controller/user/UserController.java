package com.palangwi.soup.controller.user;

import static com.palangwi.soup.utils.ApiUtils.success;

import com.palangwi.soup.dto.user.*;
import com.palangwi.soup.security.JwtAuthentication;
import com.palangwi.soup.service.user.UserService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/v1/users/init")
    public ApiResult<UserInitSettingResponseDto> initAdditionalInfo(@AuthenticationPrincipal JwtAuthentication userInfo,
                                                                    @Valid @RequestBody UserAdditionalInfoRequestDto request) {

        return success(userService.initAdditionalUserInfo(userInfo.id(), request));
    }

    @PatchMapping("/api/v1/users")
    public ApiResult<UserResponseDto> updateUser(@AuthenticationPrincipal JwtAuthentication userInfo,
                                                 @Valid @RequestBody UserUpdateRequestDto request) {

        return success(userService.updateUserInfo(userInfo.id(), request));
    }

    @GetMapping("/api/v1/users")
    public ApiResult<UserResponseDto> getUserInfo(@AuthenticationPrincipal JwtAuthentication userInfo) {
        return success(userService.getUserInfo(userInfo.id()));
    }

    @GetMapping("/api/v1/users/check-nickname")
    public ApiResult<Boolean> validateNickname(@RequestParam(name = "nickname") String nickname) {
        return success(userService.isAvailableNickname(nickname)); // 길이, 형식만 체크
    }

    @PostMapping("/api/v1/users/delete")
    public ApiResult<Void> deleteAccount(@AuthenticationPrincipal JwtAuthentication userInfo, @RequestBody UserDeleteRequestDto request) {
        userService.deleteAccount(userInfo.id(), request);
        return success();
    }
}