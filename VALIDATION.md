# 구현 검증 결과

2026-11-21, Windows 개발 환경에서 확인했다.

| 검사 | 결과 |
|---|---|
| React `npm run lint` | 통과 |
| React `npm run build` | 통과 |
| Spring `gradlew.bat bootJar --offline` | 통과 |
| Compose YAML 구조와 호스트 네트워크·루프백 설정 | 정적 검사 통과 |
| 동물 YAML ID 수·중복 | 150개, 고유 ID 150개 |
| Edge headless PC·모바일, 320px 가로 넘침 | 통과 |
| 브라우저 런타임 오류 | 없음 |

브라우저 검증은 **현재 DTO와 컨트롤러 형태에 맞춘 테스트 응답**으로 실행했다. 테스트 데이터가 프로덕션 화면에 포함되지는 않으며, 테스트는 실제 서버에 매매 요청을 보내지 않는다.

확인한 동작:

- 시장 목록·정렬값 전달·검색 결과 없음·존재하지 않는 토큰
- 30초 자동 갱신·랜덤 토큰 ID 유지·갱신 오류 후 마지막 데이터 유지
- 차트 기간 변경·빈 이력·한 점짜리 이력·모바일 차트
- 회원가입 비밀번호 확인·로그인 후 상세 화면 복귀·탭 내 새로고침 로그인 유지
- Bearer 헤더 전달·401 세션 제거·타이머 만료 후 보호 화면 이동
- 주문 확인·처리 중 버튼 비활성화·성공 응답·409 안내와 자동 재주문 방지
- 주문 확인 중 차트 기간을 바꿔도 주문 확인 상태 유지
- 보유 목록과 거래 기록·토큰 ID를 이름으로 연결

## 재실행

`frontend/tests/browser-check.cjs`에 브라우저 검증을 보관했다. Playwright는 앱 의존성에 추가하지 않았다. 별도 도구 디렉터리에 설치해서 실행한다. Vite는 다른 터미널에서 실행해 두어야 한다.

Windows 예시:

```powershell
$qaTools = Join-Path $env:TEMP 'zoostock-qa-tools'
npm install --prefix $qaTools --no-save --package-lock=false playwright
$env:ZOOSTOCK_PLAYWRIGHT_PATH = Join-Path $qaTools 'node_modules\playwright'
cd C:\ZooStock\frontend
npm run test:browser
```

Windows에서는 설치된 Microsoft Edge를 사용한다. Linux에서는 Playwright용 Chromium을 별도로 설치하고 실행한다. 결과 화면은 시스템 임시 디렉터리의 `zoostock-browser-qa`에 저장한다. `ZOOSTOCK_QA_URL`, `ZOOSTOCK_QA_OUTPUT`, `ZOOSTOCK_BROWSER_CHANNEL`로 대상 개발 서버·출력 경로·브라우저를 변경할 수 있다. **이 검사는 개발 서버 화면용이며 실제 서비스의 거래 검증을 대체하지 않는다.**

## 홈서버에서 남은 검증

이 개발 컴퓨터에는 Docker가 없고, 검증 당시 localhost:8079 백엔드가 실행 중이지 않았다. 따라서 다음은 아직 확인하지 못했다.

- 실제 API·DB를 이용한 로그인·매수·매도·낙관적 락 동시 거래
- Docker 이미지 빌드·컨테이너 실행·기존 PostgreSQL 인증
- NPG upstream 연결·공유기 최종 포워딩·현재 DNS·외부 HTTP 도달
- Let's Encrypt 발급·갱신·외부 HTTPS·직접 URL 새로고침

적용 절차는 `DEPLOYMENT.md`를 따른다. 서버 적용과 공개 배포는 사용자가 수행한 결과를 함께 확인하는 단계로 남아 있다.
