# ZooStock Frontend

React + Vite + React Router로 구성한 모의 투자 화면이다. 새 런타임 의존성 없이 브라우저 fetch, Context, SVG 차트를 사용한다.

## 실행

```bash
npm ci
npm run dev
npm run lint
npm run build
```

백엔드는 `127.0.0.1:8079`에서 별도로 실행한다. Vite가 `/api` 접두사를 제거하여 요청을 전달한다. 배포에서는 같은 역할을 Nginx가 수행한다.

## 구조

- `src/pages`: 홈, 마켓, 목록·검색, 토큰 상세·주문, 인증, 보유·거래 기록
- `src/components`: 레이아웃·표·상태 표시·SVG 차트
- `src/auth`: sessionStorage 기반 로그인과 만료 처리
- `src/lib`: HTTP 요청·오류 처리와 숫자·시간 표시
- `src/hooks`: 화면 이탈 시 요청 취소와 주기적 데이터 갱신

JWT는 현재 탭에서만 유지한다. 로그아웃은 클라이언트 토큰을 제거하며 이미 복사된 서버 토큰을 강제 폐기하지 않는다. 409 응답 시 재주문하지 않는다. 개인정보 데이터는 화면을 나가거나 로그아웃하면 유지하지 않는다.

전체 서버 적용 순서는 저장소 루트의 `DEPLOYMENT.md`를 참고한다. 닉네임·잔액 조회, 동물 설명, 게시판은 이번 구현 범위에서 제외했다.
