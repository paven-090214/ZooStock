package ZooStock.zoostock_backend.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GeneralSuccessCode implements BaseSuccessCode{

    REQUEST_SUCCESS(HttpStatus.OK, "COMMON200_1",
            "요청에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
