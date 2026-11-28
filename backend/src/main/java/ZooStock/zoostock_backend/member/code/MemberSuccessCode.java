package ZooStock.zoostock_backend.member.code;

import ZooStock.zoostock_backend.global.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberSuccessCode implements BaseSuccessCode {

    LOGIN_SUCCESS(HttpStatus.OK, "MEMBER200_1",
            "로그인에 성공했습니다."),
    SIGN_UP_SUCCESS(HttpStatus.OK, "MEMBER200_2",
            "회원가입에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
