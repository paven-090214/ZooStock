package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.market.dto.TokenDTO;
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

    public Page<TokenDTO> findAllToken(String sortValue, int page) {

        if (page <= 0 || page > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바르지 않은 페이지");
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
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "올바른 정렬 값이 아닙니다."
                );
        }


        Pageable pageable = PageRequest.of(
                page - 1,
                50,
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
