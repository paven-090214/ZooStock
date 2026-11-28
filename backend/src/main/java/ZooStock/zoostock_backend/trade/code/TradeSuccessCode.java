package ZooStock.zoostock_backend.trade.code;

import ZooStock.zoostock_backend.global.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TradeSuccessCode implements BaseSuccessCode {

    BUY_SUCCESS(HttpStatus.OK, "TRADE200_1",
            "매수에 성공했습니다."),
    SELL_SUCCESS(HttpStatus.OK, "TRADE200_2",
            "매도에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
