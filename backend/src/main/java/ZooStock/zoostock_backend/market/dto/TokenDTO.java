package ZooStock.zoostock_backend.market.dto;

public class TokenDTO {
    private Long tokenId;
    private String tokenName;
    private Long tokenPrice;
    private String imgPath;

    public TokenDTO() {
    }

    public TokenDTO(Long tokenId, String tokenName, Long tokenPrice, String imgPath) {
        this.tokenId = tokenId;
        this.tokenName = tokenName;
        this.tokenPrice = tokenPrice;
        this.imgPath = imgPath;
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
