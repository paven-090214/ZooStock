package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.market.dto.TokenDTO;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService (
            TokenRepository tokenRepository
    ) {
        this.tokenRepository = tokenRepository;
    }

    public List<TokenDTO> searchToken(String tokenName) {

        if (tokenName == null ||
        tokenName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "검색어를 입력하세요.");
        }

        return tokenRepository.findByTokenNameContaining(tokenName)
                .stream()
                .map(token -> token.toTokenDTO(
                        token.getTokenId(),
                        token.getTokenName(),
                        token.getTokenPrice(),
                        token.getImgPath()
                ))
                .toList();
    }

    public TokenDTO findToken(Long tokenId) {

        if (tokenId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "조회할 수 있는 토큰이 없습니다.");
        }

        return tokenRepository.findByTokenId(tokenId)
                .map(token -> token.toTokenDTO(
                        token.getTokenId(),
                        token.getTokenName(),
                        token.getTokenPrice(),
                        token.getImgPath()
                ))
                .orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "조회할 수 있는 토큰이 없습니다.")
        );
    }

    public List<TokenDTO> findAllToken(String sortValue) {
        List<TokenEntity> response;
        switch(sortValue){
            case "nameAsc":
                response = tokenRepository.findAll(Sort.by("tokenName").ascending());
                break;
            case "nameDesc":
                response = tokenRepository.findAll(Sort.by("tokenName").descending());
                break;
            case "priceAsc":
                response = tokenRepository.findAll(Sort.by("tokenPrice").ascending());
                break;
            case "priceDesc":
                response = tokenRepository.findAll(Sort.by("tokenPrice").descending());
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바른 정렬 값이 아닙니다.");
        }
        return response
                .stream()
                .map(
                        token -> token.toTokenDTO(
                                token.getTokenId(),
                                token.getTokenName(),
                                token.getTokenPrice(),
                                token.getImgPath()
                        )
                )
                .toList();
    }
}
