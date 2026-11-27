package ZooStock.zoostock_backend.admin.exception;

import ZooStock.zoostock_backend.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminErrorCode implements BaseErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "ADMIN404_1",
            "수정할 회원이 없습니다."),
    NEGATIVE_POINT(HttpStatus.BAD_REQUEST, "ADMIN400_1",
            "포인트는 음수가 될 수 없습니다."),
    DUPLICATE_NAME(HttpStatus.BAD_REQUEST, "ADMIN400_2",
            "이름은 중복될 수 없습니다."),
    EMPTY_INPUT(HttpStatus.BAD_REQUEST, "ADMIN400_3",
            "비어있는 값이 존재합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
