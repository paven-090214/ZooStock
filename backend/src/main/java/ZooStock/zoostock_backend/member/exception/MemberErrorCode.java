package ZooStock.zoostock_backend.member.exception;

import ZooStock.zoostock_backend.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    DUPLICATE_ID(HttpStatus.BAD_REQUEST, "MEMBER400_1",
            "이미 존재하는 아이디입니다."),
    DUPLICATE_NAME(HttpStatus.BAD_REQUEST, "MEMBER400_2",
            "이미 존재하는 이름입니다."),
    EMPTY_INPUT(HttpStatus.BAD_REQUEST, "MEMBER400_3",
            "비어있는 값이 있으면 안됩니다."),
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "MEMBER400_4",
            "랭킹 페이지 번호가 잘못되었습니다."),
    ID_NOT_FOUND(HttpStatus.UNAUTHORIZED, "MEMBER401_1",
            "아이디가 잘못되었습니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER401_2",
            "비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1",
            "회원 정보를 조회할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
