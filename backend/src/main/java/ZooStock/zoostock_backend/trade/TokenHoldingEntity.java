package ZooStock.zoostock_backend.trade;

import ZooStock.zoostock_backend.trade.dto.TokenHoldingResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@Entity
@Table(
        name = "token_holding",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_holding_member_token",
                        columnNames = {"member_uid", "token_id"}
                )
        }
)
public class TokenHoldingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holding_id")
    private Long holdingId;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "buy_price_avg")
    private BigDecimal buyPriceAvg;

    @Column(name = "member_uid")
    private Long memberUid;

    @Column(name = "token_id")
    private Long tokenId;

    public TokenHoldingResponse toTokenHoldingResponse(
            Long holdingId,
            Long memberUid,
            Long tokenId,
            Long quantity,
            BigDecimal buyPriceAvg,
            BigDecimal profitRate
    ) {
        return new TokenHoldingResponse(
                holdingId,
                memberUid,
                tokenId,
                quantity,
                buyPriceAvg,
                profitRate
        );
    }

    // 가격 변동률 계산 메서드
    public BigDecimal calculateProfitRate(Long currentPrice) {
        return BigDecimal.valueOf(currentPrice)
                .subtract(buyPriceAvg)
                .multiply(BigDecimal.valueOf(100))
                .divide(buyPriceAvg, 2, RoundingMode.HALF_UP);
    }
}
