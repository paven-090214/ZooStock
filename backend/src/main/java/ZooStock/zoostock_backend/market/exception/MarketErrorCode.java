package ZooStock.zoostock_backend.market.exception;

import ZooStock.zoostock_backend.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum MarketErrorCode implements BaseErrorCode {

    EMPTY_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "MARKET400_1",
            "검색어가 비어있습니다."),
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "MARKET400_2",
            "존재하지 않는 페이지 입니다."),
    INVALID_SORT(HttpStatus.BAD_REQUEST, "MARKET400_3",
            "존재하지 않는 정렬 기준입니다."),
    INVALID_PERIOD(HttpStatus.BAD_REQUEST, "MARKET400_4",
            "기간 설정이 잘못되었습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "MARKET404_1",
            "토큰을 찾을 수 없습니다."),
    ANIMAL_NOT_FOUND(HttpStatus.NOT_FOUND, "MARKET404_2",
            "존재하지 않는 동물입니다."),
    ANIMAL_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, "MARKET404_3",
            "동물의 이름이 없습니다."),
    ANIMAL_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MARKET404_4",
            "동물의 이미지가 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MARKET404_5",
            "회원을 찾을 수 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
