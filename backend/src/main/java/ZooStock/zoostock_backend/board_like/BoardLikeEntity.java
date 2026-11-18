package ZooStock.zoostock_backend.board_like;

import jakarta.persistence.*;

@Entity
@Table(name = "board_like")
public class BoardLikeEntity {

    @Id
    @Column(name = "like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "member_id")
    private Long memberUid;

    public BoardLikeEntity(){}

    public BoardLikeEntity(
            Long memberUid,
            Long boardId
            ){
        this.memberUid = memberUid;
        this.boardId = boardId;
    }

    public Long getLikeId() {
        return likeId;
    }

    public void setLikeId(Long likeId) {
        this.likeId = likeId;
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
}