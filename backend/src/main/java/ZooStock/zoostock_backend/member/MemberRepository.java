package ZooStock.zoostock_backend.member;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MemberRepository extends  JpaRepository<MemberEntity, Long>
{
}
