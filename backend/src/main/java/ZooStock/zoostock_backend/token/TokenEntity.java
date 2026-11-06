package ZooStock.zoostock_backend.token;

import jakarta.persistence.*;

@Entity
@Table(name = "token")
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "token_name")
    private String tokenName;

    @Column(name = "token_price")
    private Long tokenPrice;

    @Column(name = "img_path")
    private String imgPath;

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public Long getTokenPrice() {
        return tokenPrice;
    }

    public void setTokenPrice(Long tokenPrice) {
        this.tokenPrice = tokenPrice;
    }
}
