package ZooStock.zoostock_backend.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceHistoryRepository extends JpaRepository <PriceHistoryEntity, Long> {

}
