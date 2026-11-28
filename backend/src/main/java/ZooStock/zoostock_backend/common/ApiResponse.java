package ZooStock.zoostock_backend.common;

import ZooStock.zoostock_backend.global.code.BaseErrorCode;
import ZooStock.zoostock_backend.global.code.BaseSuccessCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public record ApiResponse<T>(
        @JsonProperty("isSuccess") boolean isSuccess,
        @JsonProperty("code") String code,
        @JsonProperty("message") String message,
        @JsonProperty("result") T result
) {
    public static ApiResponse<Void> onFailure(BaseErrorCode baseErrorCode) {
        return new ApiResponse<>(
                false,
                baseErrorCode.getCode(),
                baseErrorCode.getMessage(),
                null);
    }

    public static <T> ApiResponse<T> onSuccess(BaseSuccessCode baseSuccessCode, T result) {
        return new ApiResponse<>(
                true,
                baseSuccessCode.getCode(),
                baseSuccessCode.getMessage(),
                result
        );
    }
}
