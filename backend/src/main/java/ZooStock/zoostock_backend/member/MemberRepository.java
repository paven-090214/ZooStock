package ZooStock.zoostock_backend.member;

import ZooStock.zoostock_backend.member.dto.TopMemberResponse;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends  JpaRepository<MemberEntity, Long>
{
    // 낙관적 락으로 변경
    //@Query("SELECT m FROM MemberEntity m WHERE m.memberUid = :memberUid")
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    //Optional<MemberEntity> findByIdForUpdate(
    //        @Param("memberUid") Long memberUid
    //);

    Optional<MemberEntity> findByMemberId(String memberID);

    boolean existsByMemberId(String memberId);

    boolean existsByMemberName(String memberName);

    @Query(value = """
        SELECT
            m.member_name,
            m.member_point +
            COALESCE(SUM(h.quantity * t.token_price), 0)
                AS total_assets
        FROM member m
        LEFT JOIN token_holding h
            ON m.member_uid = h.member_uid
        LEFT JOIN token t
            ON h.token_id = t.token_id
        GROUP BY
            m.member_uid,
            m.member_name,
            m.member_point
        ORDER BY total_assets DESC, m.member_uid ASC
    """, nativeQuery = true)
    Page<TopMemberResponse> findTopMember(Pageable pageable);
}