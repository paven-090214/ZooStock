package ZooStock.zoostock_backend.trade;

import ZooStock.zoostock_backend.trade.dto.TradeHistoryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TradeHistoryService {

    private final TradeHistoryRepository tradeHistoryRepository;

    public TradeHistoryService(
            TradeHistoryRepository tradeHistoryRepository
    ) {
        this.tradeHistoryRepository = tradeHistoryRepository;
    }


    public List<TradeHistoryResponse> getMemebrTokenList(Long memberUid) {
        if (memberUid == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "조회에 실패했습니다.");
        return tradeHistoryRepository.findByMemberUid(memberUid)
                .stream()
                .map(
                        token -> token.toTradeHistoryResponse(
                                token.getTradeId(),
                                token.getMemberUid(),
                                token.getTokenId(),
                                token.getTradePrice(),
                                token.getQuantity(),
                                token.getTradedAt(),
                                token.getTradeType()
                        )
                )
                .toList();
    }
}
