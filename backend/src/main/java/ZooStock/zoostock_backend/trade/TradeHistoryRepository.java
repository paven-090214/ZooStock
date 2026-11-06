package ZooStock.zoostock_backend.trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeHistoryRepository extends JpaRepository <TradeHistoryEntity, Long> {

}
