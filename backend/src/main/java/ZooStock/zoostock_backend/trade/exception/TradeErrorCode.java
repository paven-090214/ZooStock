package ZooStock.zoostock_backend.trade.exception;

import ZooStock.zoostock_backend.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TradeErrorCode implements BaseErrorCode {

    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "TRADE400_1",
            "매수할 포인트가 부족합니다."),
    NOT_ENOUGH_TOKEN(HttpStatus.BAD_REQUEST, "TRADE400_2",
            "매도할 토큰이 부족합니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "TRADE400_3",
            "수량이 잘못 입력되었습니다."),
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "TRADE400_4",
            "존재하지 않는 페이지 입니다."),
    HOLDING_NOT_FOUND(HttpStatus.NOT_FOUND, "TRADE404_1",
            "토큰을 보유하고 있지 않습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TRADE404_2",
            "토큰의 정보를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "TRADE404_3",
            "회원의 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
