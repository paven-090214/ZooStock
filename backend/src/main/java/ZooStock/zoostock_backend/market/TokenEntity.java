package ZooStock.zoostock_backend.market;

import jakarta.persistence.*;

@Entity
@Table(
        name = "token",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_taxonId",
                        columnNames = "taxon_id"
                )
        }
)
public class TokenEntity {

    public TokenEntity(){};

    public TokenEntity(Long taxonId, String tokenName, Long tokenPrice, String imgPath) {
        this.taxonId = taxonId;
        this.tokenName = tokenName;
        this.tokenPrice = tokenPrice;
        this.imgPath = imgPath;
    }

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

    @Column(name = "taxon_id")
    private Long taxonId;

    public Long getTaxonId() {
        return taxonId;
    }

    public void setTaxonId(Long taxonId) {
        this.taxonId = taxonId;
    }

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

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
