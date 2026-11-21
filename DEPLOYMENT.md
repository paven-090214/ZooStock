# ZooStock 개발과 홈서버 배포

## 구성

```text
https://zoostock.gleeze.com
    -> 공유기 TCP 80/443
    -> 103번 Debian LXC / Nginx Proxy Guard / 172.30.1.40:80,443
    -> 100번 Ubuntu / 프론트 Nginx / 172.30.1.90:8080
         ├─ /               React 정적 파일
         └─ /api/*          Spring 127.0.0.1:8079/*
                               └─ PostgreSQL 127.0.0.1:5432/zoostock
```

현재 프론트는 서버가 제공하는 데이터만 표시한다. 로그인한 회원의 이름·잔액은 `/mypage/overview`로 조회하여 공통 헤더에 표시한다. 동물 설명과 게시판은 아직 포함하지 않는다. 순위 API는 오늘 첫 가격 기록 대비 상위·하위 5개이며 양수·음수를 필터링하지 않는다. 따라서 전체 시장이 하락한 날에는 상위 목록에도 음수가 나올 수 있다. 화면은 실제 부호대로 표시한다.

## Windows 개발

1. 기존 환경변수 설정으로 IntelliJ에서 백엔드를 실행한다. DB_HOST 등은 개발용 기존 값을 사용한다. 루트의 배포용 `.env`는 Spring이 자동으로 읽는 파일이 아니다.
2. `frontend`에서 실행한다.

```powershell
cd C:\ZooStock\frontend
npm ci
npm run dev
```

터미널에 표시된 Vite 주소로 접속한다. `/api` 요청은 Vite가 `127.0.0.1:8079`로 전달한다. 브라우저의 `/token/1`은 React 화면이고, `/api/token/1?history=1h`는 차트 API다. 주소가 겹치지 않도록 분리했다.

```powershell
npm run lint
npm run build
```

로컬의 `vite preview`에는 개발 프록시가 적용되지 않으므로, 실제 API 연동은 `npm run dev` 또는 배포 Nginx에서 확인한다.

## 1. 100번 Ubuntu 준비

아래 명령은 **100번 Ubuntu**에서 실행한다. 프로젝트 최신 파일을 `/srv/zoostock`에 동기화한 뒤 진행한다. 저장소의 기존 작업을 강제로 초기화하지 않는다.

```bash
cd /srv/zoostock
docker version
docker compose version
sudo ss -lntp | grep -E ':(5432|8079|8080)\b'
sudo -u postgres psql -d zoostock -c "SELECT installed_rank, version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
sudo -u postgres pg_dump -Fc zoostock > "$HOME/zoostock-before-deploy.dump"
```

5432는 기존 PostgreSQL이 사용해야 한다. 8079·8080에 기존 프로세스가 있다면 어떤 서비스인지 확인하고, 이 프로젝트의 이전 실행만 정상 종료한 뒤 전환한다. 다른 서비스의 포트를 임의로 종료하지 않는다.

V1~V3 적용 이력과 기존 DB가 일치해야 한다. Flyway 오류가 나면 history를 삭제하거나 테이블을 드롭하지 말고 로그와 이력을 먼저 확인한다. 이 배포는 DB 컨테이너를 새로 만들지 않는다.

## 2. 서버 환경변수

```bash
cd /srv/zoostock
umask 077
# .env가 이미 있다면 덮어쓰지 않고 기존 파일을 편집한다.
test -f .env || cp .env.example .env
nano .env
chmod 600 .env
```

DB_USERNAME·DB_PASSWORD에는 현재 DB 계정 값을 입력한다. JWT_SECRET은 Base64로 인코딩된 최소 32바이트 키다. 새 키가 필요하면 서버에서 `openssl rand -base64 32`로 생성해 입력한다. 비밀번호나 키를 채팅·스크린샷·Git에 올리지 않는다. `$`, `#` 등이 포함된 Compose 환경변수는 `.env`에서 작은따옴표로 감싸 리터럴 값으로 지정한다.

이 구성은 Linux 호스트 네트워크를 사용하므로 컨테이너의 `127.0.0.1`이 100번 Ubuntu의 루프백이다. PostgreSQL listen_addresses나 pg_hba.conf를 외부 허용으로 바꿀 필요가 없다. 대신 컨테이너의 네트워크 격리는 줄어든다. Docker Desktop 개발용 구성은 아니다.

## 3. 앱 빌드와 내부 확인

```bash
cd /srv/zoostock
docker compose config --quiet
docker compose build
docker compose up -d --wait --wait-timeout 600
docker compose ps
docker compose logs --tail=100 backend frontend
curl -f http://127.0.0.1:8079/token/all
curl -f http://172.30.1.90:8080/
curl -f http://172.30.1.90:8080/api/token/all
```

최초 실행 때 동물 등록에는 API 대기 시간과 요청 간 지연이 있다. `healthy`는 DB 연결과 API 응답을 확인할 뿐 150종 전부 등록됐다는 의미가 아니다. 동물 등록 실패 로그와 실제 목록 수를 확인한다. API 실패 항목이 있으면 백엔드의 기존 중복 확인 로직을 이용해 다시 실행할 수 있다. API에서 응답하지 않은 동물은 프론트가 가짜로 채우지 않는다.

서버 시간은 Asia/Seoul로 통일한다. 차트의 LocalDateTime은 +09:00으로 해석한다. 기존 데이터가 다른 시간대를 사용했다면 데이터를 임의로 바꾸지 말고 시간 기준을 확인한다.

## 4. NPG와 공유기

먼저 **103번에서** `curl -I http://172.30.1.90:8080/`가 성공하는지 확인한다. 실패하면 100번 리스닝 주소와 Proxmox·Ubuntu 방화벽을 확인한다. 필요한 허용 범위는 **103번 .40에서 100번 .90의 TCP 8080**이다. Spring 8079와 DB 5432는 공유기에서 공개하지 않는다.

NPG 관리 화면 `https://172.30.1.40:81`에서 다음 프록시 호스트를 구성한다. 화면 명칭은 설치 버전에 따라 다를 수 있다.

| 항목 | 값 |
|---|---|
| 도메인 | `zoostock.gleeze.com` |
| upstream scheme | `http` |
| upstream IP | `172.30.1.90` |
| upstream port | `8080` |
| 인증서 | 이 도메인용 Let's Encrypt 인증서 |

NPG는 원래 Host와 X-Forwarded-Proto를 전달해야 한다. API는 앱 Nginx가 처리하므로 NPG에서 `/api`를 별도 경로로 덮어쓰지 않는다.

공유기 설정은 다음과 같다.

| 외부 | 내부 대상 |
|---|---|
| TCP 80 | `172.30.1.40:80` |
| TCP 443 | `172.30.1.40:443` |

기존 `172.30.1.78` 규칙은 102번 VM으로 향하므로 이 사이트 경로로 사용하지 않는다. NPG 관리 포트 81과 내부 API 9080을 외부 사이트용 포트로 포워딩하지 않는다.

## 5. DNS와 인증서

- Dynu의 `zoostock.gleeze.com` A 레코드가 현재 집 공인 IPv4인지 확인한다. AAAA가 있으면 실제로 103번으로 도달하는 IPv6인지 확인한다. 제공된 오류 이미지의 과거 IP를 현재 IP라고 가정하지 않는다.
- 휴대폰 Wi-Fi를 끄고 `http://zoostock.gleeze.com` 접속을 확인한다. 집 내부 접속만으로 외부 도달을 판단하지 않는다.
- 외부 80번 요청이 NPG에 도착하는 상태에서 HTTP-01 인증서를 발급한다. `/.well-known/acme-challenge/`는 NPG가 처리해야 한다.
- 발급 완료 후 도메인에 인증서를 연결하고 HTTPS 강제를 켠다. NPG의 인증서 자동 갱신 기능과 로그도 확인한다. HTTP-01 갱신을 위해 80번 전달을 유지한다.
- 여전히 실패하면 DNS → 공유기 외부/WAN IP → 이중 NAT/상위 공유기 → 포워딩 → Proxmox/게스트 방화벽 → NPG 검증 경로 순으로 확인한다. 공인 IP·DNS가 실제로 맞는지 검증하기 전에 인증서 발급을 반복하지 않는다.

## 6. 기능 검증

- PC와 휴대폰에서 홈·마켓·전체 목록·검색·상세·회원 화면을 확인한다.
- 네 가지 정렬과 빈 검색 결과, 없는 토큰, 이미지 실패, 차트 1시간·5시간·1일을 확인한다.
- 마켓에 30초 이상 머무르며 순위·가격 갱신과 랜덤 토큰 ID 유지 여부를 확인한다.
- 테스트 계정으로 회원가입·로그인·새로고침·로그아웃·로그인 만료를 확인한다. 개발자 도구에서 보호된 API의 Authorization 헤더를 확인하되 토큰을 공유하지 않는다.
- 테스트 포인트로 매수·매도, 수량 오류·잔액 부족, 거래 기록 갱신을 확인한다. 주문 중 중복 클릭은 막히고, 409에는 자동 재주문이 없어야 한다. 네트워크 오류는 체결 실패를 보장하지 않으므로 기록을 확인한 뒤 재주문한다.
- 동일 회원 동시 거래의 실제 409/롤백은 별도 동시성 검증이다. 프론트에 409 안내가 뜨는 것만으로 DB 동시성 검증을 완료했다고 판단하지 않는다.
- `https://zoostock.gleeze.com/token/1` 같은 상세 URL을 직접 열고 새로고침해도 정상 표시되어야 한다. `/api`의 401·404·409가 index.html로 대체되면 안 된다.

## 7. 재배포·로그·복구

배포 전 `.env`의 `ZOOSTOCK_RELEASE`를 기록하고, 새 배포에는 다른 태그를 지정한다. 예: `release-001` → `release-002`. 이전 이미지를 삭제하지 않는다.

```bash
cd /srv/zoostock
docker compose build
docker compose up -d --wait --wait-timeout 600
docker compose logs --tail=100 backend frontend
```

실패 시 `.env`의 `ZOOSTOCK_RELEASE`를 이전 태그로 되돌린 뒤 빌드 없이 실행한다.

```bash
docker compose up -d --no-build --wait --wait-timeout 600
```

이미지 복구는 DB 마이그레이션을 되돌리지 않는다. 이 작업은 기존 V1~V3를 변경하지 않지만 향후 스키마 변경 배포에는 별도 호환성을 검토해야 한다. 장애 원인을 확인하기 위해 컨테이너 로그, NPG 요청/오류 로그, Flyway 이력을 사용한다.

사이트 검증은 외부 HTTPS에서 실제 API와 테스트 주문까지 확인한 뒤 완료한다. 이 안내 파일을 준비한 것만으로 서버 적용·인증서 발급이 완료되는 것은 아니다.
