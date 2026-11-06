package ZooStock.zoostock_backend.board;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BoardController {

    private final BoardService boardService;

    public BoardController(
            BoardService boardService
    ) {
        this.boardService = boardService;
    }

    @PostMapping ("/board/create")
    public ResponseEntity<String> createBoard(
            @RequestBody BoardDTO boardDTO,
            HttpServletRequest request
    ) {
        Long memberUid = (Long) request.getAttribute("memberUid");

        boardService.createBoard(memberUid, boardDTO);
        return ResponseEntity.ok("글 생성 성공");
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<BoardDTO> getBoard(
            @PathVariable Long boardId
    ) {
        BoardDTO response = boardService.getBoard(boardId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/board/{boardId}/Update")
    public ResponseEntity<BoardDTO> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardDTO boardDTO
    ){
        BoardDTO response = boardService.updateBoard(boardId, boardDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/board/{boardId}/delete")
    public void deleteBoard(
            @PathVariable Long boardId
    ) {
        boardService.deleteBoard(boardId);
    }
}
