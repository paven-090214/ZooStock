package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.market.dto.TokenDTO;
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


}
