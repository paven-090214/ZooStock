package ZooStock.zoostock_backend.market.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DailyRankingResponse {
    private List<TokenChangeProjection> gainers;
    private List<TokenChangeProjection> losers;
}
