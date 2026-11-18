package ZooStock.zoostock_backend.integration;

import org.springframework.stereotype.Service;

@Service
public class AnimalImgImportService {

    private final AnimalApiClient animalApiClient;

    public AnimalImgImportService (
            AnimalApiClient animalApiClient
    ) {
        this.animalApiClient = animalApiClient;
    }

    // 역할: 외부 사진 URL로 이미지를 다운로드하고, 지정한 외부 폴더에 저장한다.
    // DB 저장은 AnimalImportService의 역할이다. 여기서는 파일 저장만 담당한다.
    // 아직 아래 주석은 구현 계획이며, 현재 이 클래스는 다운로드를 실행하지 않는다.

    // 사진은 API 기본 주소와 다른 서버에 있으므로, 요청할 때 사진의 전체 URL을 사용한다.

    // TODO 3. 실제 파일을 저장할 폴더를 Path 타입으로 준비한다.
    // Path는 파일 또는 폴더의 위치를 표현하는 Java 타입이다.
    // 이 폴더는 src/main/resources/static 대신 프로젝트 밖의 저장 폴더로 정한다.
    // 예: Windows의 C:/ZooStock-data/images, 홈서버의 /data/zoostock/images
    // 위 주소는 예시다. 실제 경로는 아직 정하지 않았으며 설정값으로 받을 수 있다.
    // Docker에서는 컨테이너 경로와 홈서버 폴더를 볼륨으로 연결해야 파일이 유지된다.

    // TODO 4. 이미지 하나를 저장하는 public 메서드를 작성한다.
    // 이름 예: saveImage
    // 매개변수: Long taxonId, String imageUrl
    // 반환 타입: String
    // 반환할 값은 실제 디스크 절대 경로가 아니라 브라우저용 경로다.
    // 예: 실제 파일 /data/zoostock/images/41964.jpg -> 반환값 /images/41964.jpg

    // TODO 5. 메서드 안에서 taxonId와 imageUrl을 검사한다.
    // taxonId가 null이거나 유효하지 않은 값인지 확인한다.
    // imageUrl이 null이거나 공백인지 확인한다.
    // 사용자 임의 입력 URL을 받는 API로 노출하지 않고, 동물 API가 제공한 사진 URL을 사용한다.

    // TODO 6. RestClient로 imageUrl에 GET 요청을 보낸다.
    // AnimalApiClient와 같은 get -> uri -> retrieve 흐름을 활용한다.
    // 이번 응답은 JSON이 아니라 사진 파일이므로 String 대신 byte[]로 받는다.
    // byte[]는 파일을 구성하는 바이트들의 배열이다.
    // 확장자 판단을 위해 본문과 함께 Content-Type 응답 헤더도 읽는다.
    // 응답 헤더와 본문을 함께 받는 메서드로 toEntity(byte[].class)를 살펴본다.
    // 응답 본문이 null이거나 길이가 0이면 저장하지 않고 실패 처리한다.

    // TODO 7. 실제 이미지 형식에 맞는 확장자로 파일명을 만든다.
    // 예: image/jpeg -> .jpg, image/png -> .png, image/webp -> .webp
    // taxonId가 41964이고 JPEG라면 41964.jpg처럼 만든다.
    // 모든 사진을 무조건 .jpg로 이름만 바꾸면 실제 파일 형식과 달라질 수 있다.
    // 허용하지 않은 Content-Type이나 알 수 없는 형식은 저장하지 않고 실패 처리한다.

    // TODO 8. 저장 폴더를 만들고 사진 바이트를 파일에 기록한다.
    // Files.createDirectories: 폴더가 없으면 생성한다.
    // Path.resolve: 저장 폴더와 파일명을 연결해 대상 파일의 위치를 만든다.
    // Files.write: byte[] 내용을 파일로 저장한다.
    // IOException은 디스크 기록 실패 등에서 발생한다. 호출자에게 실패를 전달한다.
    // 실패를 숨기고 성공한 경로를 반환하지 않는다.
    // 같은 파일이 이미 있으면 덮어쓸지 건너뛸지 명확히 정한다.

    // TODO 9. 저장이 성공하면 /images/파일명 형태의 경로를 반환한다.
    // URL 경로에는 운영체제 경로 구분자가 아니라 /를 사용한다.
    // 경로 반환만으로 브라우저 접근이 되지는 않는다.
    // 별도 Spring MVC 설정에서 /images/**를 TODO 3의 폴더에 연결해야 한다.

    // TODO 10. 이 클래스 구현 후 AnimalImportService에서 호출하도록 연결한다.
    // AnimalImportService가 이 서비스를 생성자로 주입받는다.
    // 동물 정보 검사 -> saveImage(taxonId, 사진 URL) -> 반환 경로를 TokenEntity에 설정 -> DB 저장
    // DB 저장에 실패했을 때 다운로드된 파일을 정리할지도 결정한다.
    // 기존 토큰은 중복 검사에서 건너뛰므로, 기존 이미지 URL을 바꾸는 작업은 별도로 필요하다.
    // 사진 출처와 라이선스 정보는 다운로드 후에도 보존하고 이용 조건에 맞게 표시한다.

    // 처음에는 한 종만 다운로드해 파일이 생기는지 확인하고, 그다음 DB 저장에 연결한다.
}
