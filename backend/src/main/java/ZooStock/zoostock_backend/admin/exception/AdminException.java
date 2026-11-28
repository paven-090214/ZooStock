package ZooStock.zoostock_backend.admin.exception;

import ZooStock.zoostock_backend.global.exception.GeneralException;

public class AdminException extends GeneralException {
    public AdminException(AdminErrorCode adminErrorCode) {
        super(adminErrorCode);
    }
}
