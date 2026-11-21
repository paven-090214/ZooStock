package ZooStock.zoostock_backend.trade;

import ZooStock.zoostock_backend.trade.dto.TokenHoldingResponse;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
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

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getBuyPriceAvg() {
        return buyPriceAvg;
    }

    public void setBuyPriceAvg(BigDecimal buyPriceAvg) {
        this.buyPriceAvg = buyPriceAvg;
    }

    public Long getMemberUid() {
        return memberUid;
    }

    public void setMemberUid(Long memberUid) {
        this.memberUid = memberUid;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public TokenHoldingResponse toTradeHistoryResponse(
            Long holdingId,
            Long memberUid,
            Long tokenId,
            Long quantity,
            BigDecimal buyPriceAvg
    ) {
        return new TokenHoldingResponse(
                holdingId,
                memberUid,
                tokenId,
                quantity,
                buyPriceAvg
        );
    }
}
