package ZooStock.zoostock_backend.board;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "board")
public class BoardEntity {

    public BoardEntity(){}

    @Id
    @Column(name = "board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(name = "member_uid")
    private Long memberUid;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "board_created_at")
    private Date boardCreatedAt;

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getMemberUid() {
        return memberUid;
    }

    public void setMemberUid(Long memberUid) {
        this.memberUid = memberUid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Date getBoardCreatedAt() {
        return boardCreatedAt;
    }

    public void setBoardCreatedAt(Date boardCreatedAt) {
        this.boardCreatedAt = boardCreatedAt;
    }

    public BoardEntity (
        Long memberUid,
        String title,
        String content,
        Date boardCreatedAt
    ) {
        this.memberUid = memberUid;
        this.title = title;
        this.content = content;
        this.boardCreatedAt = boardCreatedAt;
    }
}
