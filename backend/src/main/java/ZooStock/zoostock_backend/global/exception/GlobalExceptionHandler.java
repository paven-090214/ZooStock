package ZooStock.zoostock_backend.global.exception;

import ZooStock.zoostock_backend.common.ApiResponse;
import ZooStock.zoostock_backend.global.code.BaseErrorCode;
import ZooStock.zoostock_backend.global.code.GeneralErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLock(
            OptimisticLockingFailureException e
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("다른 거래가 먼저 처리되었습니다. 다시 시도해주세요.");
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(
            GeneralException exception
    ) {
        BaseErrorCode baseErrorCode = exception.getBaseErrorCode();
        return ResponseEntity.status(baseErrorCode.getStatus())
                .body(ApiResponse.onFailure(baseErrorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        log.error("예상하지 못한 서버 오류", exception);

        BaseErrorCode baseErrorCode = GeneralErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(baseErrorCode.getStatus())
                .body(ApiResponse.onFailure(baseErrorCode));
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,   // 파라미터 없음
            MethodArgumentTypeMismatchException.class,       // 타입 불일치
            HttpMessageNotReadableException.class            // JSON 못 읽음
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception exception) {
        log.warn("잘못된 요청: {}", exception.getMessage());

        BaseErrorCode baseErrorCode = GeneralErrorCode.BAD_REQUEST;

        return ResponseEntity.status(baseErrorCode.getStatus())
                .body(ApiResponse.onFailure(baseErrorCode));
    }
}