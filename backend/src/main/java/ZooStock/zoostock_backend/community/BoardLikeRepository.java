package ZooStock.zoostock_backend.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

@Service
public interface BoardLikeRepository extends JpaRepository<BoardLikeEntity, Long> {

    @Query(
            // value : 실행할 쿼리
            // :boardId에는 Spring Data JPA가 메서드 인자 값을 연결
            value = "SELECT COUNT(*) FROM board_like WHERE board_id = :boardId",
            // 이 쿼리는 실제 DB의 SQL임을 알림
            nativeQuery = true
    )
    Long countLikesByBoardId(Long boardId);
}