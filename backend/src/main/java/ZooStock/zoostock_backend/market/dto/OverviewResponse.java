package ZooStock.zoostock_backend.market.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class OverviewResponse {
    private DailyRankingResponse dailyRankingResponse;
    private List<TokenDTO> randomTokens;
}
