package ZooStock.zoostock_backend.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenHoldingResponse {
    private Long holdingId;
    private Long memberUid;
    private Long tokenId;
    private Long quantity;
    private BigDecimal buyPriceAvg;
    private BigDecimal profitRate;
}
