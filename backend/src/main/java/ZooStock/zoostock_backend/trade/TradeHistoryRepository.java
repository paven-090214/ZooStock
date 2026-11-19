package ZooStock.zoostock_backend.trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeHistoryRepository extends JpaRepository <TradeHistoryEntity, Long> {

    public List<TradeHistoryEntity> findByMemberUid(Long memberUid);
}
