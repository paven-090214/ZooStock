package ZooStock.zoostock_backend.token;

public class TokenDTO {
    private Long tokenId;
    private String tokenName;
    private Long tokenPrice;
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

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
