package ZooStock.zoostock_backend.trade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TradeHistoryRepository extends JpaRepository <TradeHistoryEntity, Long> {

    public Page<TradeHistoryEntity> findByMemberUid(Long memberUid, Pageable pageable);

    // TradedAtGreaterThanEqual : traded_at >= ?
    public List<TradeHistoryEntity> findByMemberUidAndTokenIdAndTradedAtGreaterThanEqualOrderByTradedAtAsc(
            Long memberUid,
            Long tokenId,
            Date start
    );
}
