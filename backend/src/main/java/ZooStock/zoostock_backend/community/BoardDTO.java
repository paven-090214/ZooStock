package ZooStock.zoostock_backend.community;

import java.util.Date;

public class BoardDTO {
    private Long boardId;
    private Long memberUid;
    private String title;
    private String content;
    private Long viewCount;
    private Date boardCreatedAt;
    private Long likeCount;

    public BoardDTO(){}

    public BoardDTO(Long boardId, Long memberUid, String title, String content, Long viewCount, Date boardCreatedAt, Long likeCount) {
        this.boardId = boardId;
        this.memberUid = memberUid;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.boardCreatedAt = boardCreatedAt;
        this.likeCount = likeCount;
    }

    public BoardDTO(Long boardId, Long memberUid, String title, String content) {
        this.boardId = boardId;
        this.memberUid = memberUid;
        this.title = title;
        this.content = content;
    }

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

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
}
