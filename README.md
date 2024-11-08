# road2gm-spring

## 인증 처리

## 액세스 토큰

- 이메일과 비밀번호 인증
- JWT 형식 / 디비 저장 안함
- JSON 응답에 ApiResponse 래퍼 사용
- 프론트엔드: Redux 상태 변수 저장

액세스 토큰 JSON 응답에 ApiResponse 래퍼 사용 시

- 장점: 추가 메타 데이터 확장성과 일관된 응답 구조 유지
- 단점: 크기 증가 및 클라이언트 data 내부 접근 코드 필요

- 단, 외부 API나 표준을 따라야 하는 경우(OAuth2 등)에는 감싸지 않는 편이 좋다.

## 리프레시 토큰

- 랜덤문자열 형식 / 디비 또는 Redis 저장
- HttpOnly, Secure, SameSite=Strict 쿠키 전송 (JSON 응답 없음)
- 프론트엔드: 별도 저장 없음

리프레시 토큰 서버 저장하는 이유

- 토큰 유효성 검증
- 토큰 재사용 감지
- 강제 로그아웃 기능 구현
- 사용자별 토큰 관리
- 보안 감사 및 모니터링

리프레시 토큰 HTTP only 쿠키 전송 보안 체크리스트

- 토큰 재사용 감지 및 대응
- 적절한 만료 시간 설정
- HTTPS 강제 사용
- Rate Limiting 구현
- 클라이언트 식별 정보 저장
- 보안 이벤트 모니터링
- 동시 세션 제한
- 토큰 순환(rotation) 구현
- 적절한 에러 처리
- 보안 헤더 설정

## OAuth2 state

결론:

- 백엔드: 랜덤문자열 state 코드를 1분 유효기간, 경로, 도메인 제한된 HTTP only 쿠키 전달
- 프론트엔드: 별도 state 저장은 없고 액세스 토큰 요청 로그인 절차 수행

- 랜덤문자열 형식 / 디비 또는 Redis 저장
- HttpOnly, Secure, SameSite=Strict 쿠키 전송 (JSON 응답 없음)
- 프론트엔드: 별도 저장 없음
- state 백엔드에 저장하는 이유는 리프레시 토큰을 서버에 저장하는 이유와 동일

근거:
OAuth2 인증 완료 후 응답 데이터 형식

- JWT 액세스 토큰
- 랜덤문자열 state 코드
  백엔드 응답을 프론트엔드에 전달하는 방법
- 새 창 또는 팝업: 백엔드에서 자바스크립트 하드코딩
- 리다이렉트
    - HTTP only 쿠키 전달
    - query param 전달 (보안상 권장 안 함)
