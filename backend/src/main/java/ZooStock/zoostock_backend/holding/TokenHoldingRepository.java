package ZooStock.zoostock_backend.holding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenHoldingRepository extends JpaRepository<TokenHoldingEntity, Long> {

    public Optional<TokenHoldingEntity> findByMemberUidAndTokenId(Long memberUid,Long tokenId);
}
