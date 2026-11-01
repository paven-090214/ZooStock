package ZooStock.zoostock_backend.member;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface MemberRepository extends  JpaRepository<MemberEntity, Long>
{
    // 트랜잭션 동시성 처리
    @Query("SELECT m FROM MemberEntity m WHERE m.memberUid = :memberUid")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<MemberEntity> findByIdForUpdate(
            @Param("memberUid") Long memberUid
    );

    Optional<MemberEntity> findByMemberId(String memberID);

    boolean existsByMemberId(String memberId);

    boolean existsByMemberName(String memberName);
}


