package ZooStock.zoostock_backend.market.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PriceHistoryResponse {
    private Long historyId;
    private Long price;
    private Long tokenId;
    private LocalDateTime recordedAt;
}
