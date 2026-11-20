package ZooStock.zoostock_backend.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long>
{
    public Optional<TokenEntity> findByTaxonId(Long taxonId);

    public List<TokenEntity> findByTokenNameContaining(String name);

    public Optional<TokenEntity> findByTokenId(Long tokenId);

    public boolean existsByTokenId(Long tokenId);
}
