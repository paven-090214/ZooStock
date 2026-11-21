package ZooStock.zoostock_backend.trade;

import ZooStock.zoostock_backend.market.TokenEntity;
import ZooStock.zoostock_backend.trade.dto.TokenHoldingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenHoldingRepository extends JpaRepository<TokenHoldingEntity, Long> {

    public Optional<TokenHoldingEntity> findByMemberUidAndTokenId(Long memberUid,Long tokenId);

    public List<TokenHoldingEntity> findByMemberUid(Long memberUid);

}
