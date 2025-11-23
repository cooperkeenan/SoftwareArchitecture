package napier.destore.common.exception;

public class ServiceException extends RuntimeException {

    private final String errorCode;
    private final String serviceName;

    public ServiceException(String message) {
        super(message);
        this.errorCode = "SERVICE_ERROR";
        this.serviceName = "unknown";
    }

    public ServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.serviceName = "unknown";
    }

    public ServiceException(String message, String errorCode, String serviceName) {
        super(message);
        this.errorCode = errorCode;
        this.serviceName = serviceName;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SERVICE_ERROR";
        this.serviceName = "unknown";
    }

    public ServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.serviceName = "unknown";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getServiceName() {
        return serviceName;
    }
}