package ZooStock.zoostock_backend.trade.exception;

import ZooStock.zoostock_backend.global.exception.GeneralException;

public class TradeException extends GeneralException {
    public TradeException (TradeErrorCode tradeErrorCode) {
        super(tradeErrorCode);
    }
}
