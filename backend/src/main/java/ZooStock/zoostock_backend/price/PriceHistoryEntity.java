package ZooStock.zoostock_backend.price;

import jakarta.persistence.*;

import java.util.Date;

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
    private Date recordedAt;

    public Long getHistoryId() {
        return historyId;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public Date getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Date recordedAt) {
        this.recordedAt = recordedAt;
    }
}

