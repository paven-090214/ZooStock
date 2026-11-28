package ZooStock.zoostock_backend.member.exception;

import ZooStock.zoostock_backend.global.exception.GeneralException;

public class MemberException extends GeneralException {
    public MemberException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
