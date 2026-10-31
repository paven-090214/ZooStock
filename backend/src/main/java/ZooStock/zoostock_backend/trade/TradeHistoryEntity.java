package ZooStock.zoostock_backend.trade;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(
        name = "trade_history",
        check = {
                @CheckConstraint(
                        name = "check_trade_type",
                        constraint = "trade_type IN ('BUY', 'SELL')"
                )
        }
)
public class TradeHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long tradeId;

    @Column(name = "member_uid")
    private Long memberUid;

    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "trade_price")
    private Long tradePrice;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "traded_at")
    private Date tradedAt;

    @Column(name = "trade_type")
    private String tradeType;

    public void setMemberUid(Long memberUid) {
        this.memberUid = memberUid;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public void setTradePrice(Long tradePrice) {
        this.tradePrice = tradePrice;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setTradedAt(Date tradedAt) {
        this.tradedAt = tradedAt;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
}
