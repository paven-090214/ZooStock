package ZooStock.zoostock_backend.trade.dto;

import ZooStock.zoostock_backend.trade.TradeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TradeHistoryResponse {
    private Long tradeId;
    private Long memberUid;
    private Long tokenId;
    private Long tradePrice;
    private Long quantity;
    private Date tradedAt;
    private TradeType tradeType;
}
