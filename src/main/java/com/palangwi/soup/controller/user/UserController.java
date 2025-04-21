package com.palangwi.soup.controller.user;

import com.palangwi.soup.dto.user.*;
import com.palangwi.soup.security.JwtAuthentication;
import com.palangwi.soup.service.UserService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.palangwi.soup.utils.ApiUtils.success;

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
    public ApiResult<Boolean> checkNickname(@RequestParam String nickname) {
        return success(userService.isNicknameDuplicate(nickname));
    }

    @PostMapping("/api/v1/users/withdraw")
    public ApiResult<UserWithdrawResponseDto> withdraw(@AuthenticationPrincipal JwtAuthentication userInfo) {
        userService.withdrawUser(userInfo.id());
        return success(null);
    }
}