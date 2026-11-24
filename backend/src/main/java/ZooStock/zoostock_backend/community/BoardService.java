package ZooStock.zoostock_backend.community;

import ZooStock.zoostock_backend.community.dto.BoardDTO;
import ZooStock.zoostock_backend.member.dto.MemberDTO;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;

    public BoardService (
            BoardRepository boardRepository,
            BoardLikeRepository boardLikeRepository
    ) {
        this.boardRepository = boardRepository;
        this.boardLikeRepository = boardLikeRepository;
    }

    public List<BoardDTO> getBoards() {
        // ponytail: 좋아요 수는 목록에서 생략(null). 필요하면 쿼리 1번으로 묶어서 추가
        return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "boardId")).stream()
                .map(b -> new BoardDTO(
                        b.getBoardId(),
                        b.getMemberUid(),
                        b.getTitle(),
                        b.getContent(),
                        b.getViewCount(),
                        b.getBoardCreatedAt(),
                        null
                ))
                .toList();
    }

    public void createBoard(
            Long memberUid,
            BoardDTO boardDTO
    ) {
        String content = boardDTO.getContent();
        String title = boardDTO.getTitle();

        if (
                content == null ||
                        content.isBlank() ||
                        title == null ||
                        title.isBlank()
        ) {
            throw new IllegalArgumentException("제목과 본문에 공백이 있을 수 없습니다.");
        }

        Date now = new Date();

        BoardEntity boardEntity = new BoardEntity(memberUid, title, content, now);
        boardEntity.setViewCount(0L);
        boardRepository.save(boardEntity);
    }

    public BoardDTO getBoard(Long boardId) {
        Optional<BoardEntity> optionalBoard =
                boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }

        BoardEntity boardEntity = optionalBoard.get();

        boardEntity.setViewCount(
                boardEntity.getViewCount() + 1
        );

        Long count = boardLikeRepository.countLikesByBoardId(boardId);

        boardRepository.save(boardEntity);

        return new BoardDTO(
                boardEntity.getBoardId(),
                boardEntity.getMemberUid(),
                boardEntity.getTitle(),
                boardEntity.getContent(),
                boardEntity.getViewCount(),
                boardEntity.getBoardCreatedAt(),
                count
        );
    }

    public BoardDTO updateBoard(Long boardId, BoardDTO boardDTO) {
        Optional<BoardEntity> optionalBoard =
                boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new IllegalArgumentException("게시글 번호를 찾을 수 없습니다.");
        }

        Long Count = boardLikeRepository.countLikesByBoardId(boardId);

        BoardEntity boardEntity = optionalBoard.get();

        boardEntity.setContent(boardDTO.getContent());
        boardEntity.setTitle(boardDTO.getTitle());
        boardRepository.save(boardEntity);

        return new BoardDTO(
                boardEntity.getBoardId(),
                boardEntity.getMemberUid(),
                boardEntity.getTitle(),
                boardEntity.getContent(),
                boardEntity.getViewCount(),
                boardEntity.getBoardCreatedAt(),
                Count
        );
    }

    public void deleteBoard(Long boardId) {
        Optional<BoardEntity> optionalBoard =
                boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new IllegalArgumentException("boardId를 찾을 수 없습니다.");
        }

        BoardEntity entity = optionalBoard.get();

        boardRepository.delete(entity);
    }

    public Long likeAction(Long boardId, MemberDTO memberDTO) {

        BoardLikeEntity boardLikeEntity = new BoardLikeEntity(
                memberDTO.getMemberUid(),
                boardId
        );

        boardLikeRepository.save(boardLikeEntity);

        return boardLikeRepository.countLikesByBoardId(boardId);
    }
}
