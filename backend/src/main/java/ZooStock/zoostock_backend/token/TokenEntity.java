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

    public Long getTokenPrice() {
        return tokenPrice;
    }
}
