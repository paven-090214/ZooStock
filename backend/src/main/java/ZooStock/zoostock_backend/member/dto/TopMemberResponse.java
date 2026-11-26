package ZooStock.zoostock_backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Setter
@Getter
public class TopMemberResponse {
    private String memberName;
    private BigDecimal memberPoint;
}
