package ZooStock.zoostock_backend.trade.dto;

import java.util.Date;

public record TradeResponse(
    Long tokenId,
    String tokenName,
    Long price,
    Long quantity,
    Date tradedAt
) {
}
