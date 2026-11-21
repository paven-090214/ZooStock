package ZooStock.zoostock_backend.market;

import ZooStock.zoostock_backend.market.dto.TokenChangeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository <PriceHistoryEntity, Long> {

    // RecordedAtGreaterThanEqual : recorded_at >= ?
    //RecordedAtLessThan : recorded_at < ?
    public List<PriceHistoryEntity> findByTokenIdAndRecordedAtGreaterThanEqualAndRecordedAtLessThanOrderByRecordedAtAsc(
            Long tokenId,
            LocalDateTime start,
            LocalDateTime end
    );

    //이 값을 전달 받기 위해 새로운 projection 생성했음
    @Query(value = """
    SELECT
        t.token_id AS "tokenId",
        t.token_name AS "tokenName",
        t.token_price AS "currentPrice",
        h.price AS "startPrice",
        (t.token_price - h.price) * 100.0
            / NULLIF(h.price, 0) AS "changeRate"
    FROM token t
    JOIN LATERAL (
        SELECT price
        FROM price_history
        WHERE token_id = t.token_id
          AND recorded_at >= CURRENT_DATE
        ORDER BY recorded_at ASC, history_id ASC
        LIMIT 1
    ) h ON TRUE
    ORDER BY
        CASE WHEN :direction = 'UP' THEN
            (t.token_price - h.price) * 100.0 / NULLIF(h.price, 0)
        END DESC,
        CASE WHEN :direction = 'DOWN' THEN
            (t.token_price - h.price) * 100.0 / NULLIF(h.price, 0)
        END ASC,
        t.token_id ASC
    LIMIT 5
    """, nativeQuery = true)
    public List<TokenChangeProjection> findDailyChangeRates(
            //@Param : DB에 컬럼으로 직접 비교하기 위해 사용
            @Param("direction") String direction
    );

    @Query(
            value = """
        SELECT *
        FROM token
        ORDER BY RANDOM()
        LIMIT 5
        """,
            nativeQuery = true
    )
    public List<TokenEntity> getRandomTokens();
}
