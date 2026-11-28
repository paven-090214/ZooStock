package ZooStock.zoostock_backend.global.exception;

import ZooStock.zoostock_backend.global.code.BaseErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
// public, protected의 성능적 관계는 없고, 관례임.
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class GeneralException extends RuntimeException{
    private final BaseErrorCode baseErrorCode;
}
