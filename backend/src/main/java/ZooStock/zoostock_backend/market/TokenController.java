package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.market.dto.TokenDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController (
            TokenService tokenService
    ) {
        this.tokenService = tokenService;
    }

    @GetMapping("/token/search")
    public ResponseEntity<List<TokenDTO>> searchToken(
            @RequestParam("name") String tokenName
    ) {
        List<TokenDTO> response = tokenService.searchToken(tokenName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/token/search/{id}")
    public ResponseEntity<TokenDTO> findToken(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(tokenService.findToken(id));
    }
}
