package ZooStock.zoostock_backend.trade;

import ZooStock.zoostock_backend.trade.dto.TradeHistoryResponse;
import ZooStock.zoostock_backend.trade.exception.TradeErrorCode;
import ZooStock.zoostock_backend.trade.exception.TradeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TradeHistoryService {

    private final TradeHistoryRepository tradeHistoryRepository;

    public TradeHistoryService(
            TradeHistoryRepository tradeHistoryRepository
    ) {
        this.tradeHistoryRepository = tradeHistoryRepository;
    }


    public Page<TradeHistoryResponse> getMemberTokenList(Long memberUid, int page) {

        if (page <= 0)
            throw new TradeException(TradeErrorCode.INVALID_PAGE);

        if (memberUid == null)
            throw new TradeException(TradeErrorCode.MEMBER_NOT_FOUND);

        Pageable pageable = PageRequest.of(
                page - 1,
                30,
                Sort.by(Sort.Direction.DESC, "tradedAt")
        );

        return tradeHistoryRepository.findByMemberUid(memberUid, pageable)
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
                );
    }
}
