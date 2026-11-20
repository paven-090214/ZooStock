package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.market.dto.PriceHistoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceHistoryRepository extends JpaRepository <PriceHistoryEntity, Long> {

    // RecordedAtGreaterThanEqual : recorded_at >= ?
    //RecordedAtLessThan : recorded_at < ?
    public List<PriceHistoryEntity> findByTokenIdAndRecordedAtGreaterThanEqualAndRecordedAtLessThanOrderByRecordedAtAsc(
            Long tokenId,
            LocalDateTime start,
            LocalDateTime end
    );
}
