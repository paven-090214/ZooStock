package ZooStock.zoostock_backend.exception;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}