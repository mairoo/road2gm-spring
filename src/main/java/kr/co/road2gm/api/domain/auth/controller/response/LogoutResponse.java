package kr.co.road2gm.api.domain.auth.controller.response;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class LogoutResponse {
    private final String message = "로그아웃했습니다.";
}
