package ZooStock.zoostock_backend.market.exception;

import ZooStock.zoostock_backend.global.exception.GeneralException;

public class MarketException extends GeneralException {
    public MarketException(MarketErrorCode marketErrorCode) {
        super(marketErrorCode);
    }
}
