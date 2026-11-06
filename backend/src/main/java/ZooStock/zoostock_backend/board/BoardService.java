package ZooStock.zoostock_backend.board;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService (
            BoardRepository boardRepository
    ) {
        this.boardRepository = boardRepository;
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

        boardRepository.save(boardEntity);

        return new BoardDTO(
                boardEntity.getBoardId(),
                boardEntity.getMemberUid(),
                boardEntity.getTitle(),
                boardEntity.getContent(),
                boardEntity.getViewCount(),
                boardEntity.getBoardCreatedAt()
        );
    }

    public BoardDTO updateBoard(Long boardId, BoardDTO boardDTO) {
        Optional<BoardEntity> optionalBoard =
                boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new IllegalArgumentException("게시글 번호를 찾을 수 없습니다.");
        }

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
                boardEntity.getBoardCreatedAt()
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
}
