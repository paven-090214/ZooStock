package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.market.dto.TokenDTO;
import ZooStock.zoostock_backend.market.exception.MarketErrorCode;
import ZooStock.zoostock_backend.market.exception.MarketException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            throw new MarketException(MarketErrorCode.EMPTY_SEARCH_KEYWORD);
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

        if (tokenId == null)
            throw new MarketException(MarketErrorCode.TOKEN_NOT_FOUND);

        return tokenRepository.findByTokenId(tokenId)
                .map(token -> token.toTokenDTO(
                        token.getTokenId(),
                        token.getTokenName(),
                        token.getTokenPrice(),
                        token.getImgPath()
                ))
                .orElseThrow(() ->
                        new MarketException(MarketErrorCode.TOKEN_NOT_FOUND)
                );
    }

    public Page<TokenDTO> findAllToken(String sortValue, int page) {

        if (page <= 0 || page > 5) {
            throw new MarketException(MarketErrorCode.INVALID_PAGE);
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "tokenName");

        switch (sortValue) {
            case "nameAsc":
                sort = Sort.by(Sort.Direction.ASC, "tokenName");
                break;
            case "nameDesc":
                sort = Sort.by(Sort.Direction.DESC, "tokenName");
                break;
            case "priceAsc":
                sort = Sort.by(Sort.Direction.ASC, "tokenPrice");
                break;
            case "priceDesc":
                sort = Sort.by(Sort.Direction.DESC, "tokenPrice");
                break;
            default:
                throw new MarketException(MarketErrorCode.INVALID_SORT);
        }


        Pageable pageable = PageRequest.of(
                page - 1,
                30,
                sort
        );

        return tokenRepository.findAll(pageable)
                .map(
                        token -> token.toTokenDTO(
                                token.getTokenId(),
                                token.getTokenName(),
                                token.getTokenPrice(),
                                token.getImgPath()
                        )
                );

    }
}
