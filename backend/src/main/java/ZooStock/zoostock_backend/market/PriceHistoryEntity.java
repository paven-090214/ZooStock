package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.market.dto.PriceHistoryResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "price_history")
public class PriceHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "price")
    private Long price;

    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
    public PriceHistoryResponse toPriceHistoryResponse(
            Long historyId,
            Long price,
            Long tokenId,
            LocalDateTime recordedAt
    ) {
        return new PriceHistoryResponse(
                historyId,
                price,
                tokenId,
                recordedAt
        );
    }
}

