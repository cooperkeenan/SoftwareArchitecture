package napier.destore.common.exception;

public class ExternalServiceException extends ServiceException {

    private final String externalService;
    private final Integer statusCode;

    public ExternalServiceException(String externalService, String message) {
        super(String.format("External service '%s' error: %s", externalService, message), "EXTERNAL_SERVICE_ERROR");
        this.externalService = externalService;
        this.statusCode = null;
    }

    public ExternalServiceException(String externalService, String message, Integer statusCode) {
        super(String.format("External service '%s' returned %d: %s", externalService, statusCode, message),
                "EXTERNAL_SERVICE_ERROR");
        this.externalService = externalService;
        this.statusCode = statusCode;
    }

    public ExternalServiceException(String externalService, String message, Throwable cause) {
        super(String.format("External service '%s' error: %s", externalService, message),
                "EXTERNAL_SERVICE_ERROR", cause);
        this.externalService = externalService;
        this.statusCode = null;
    }

    public String getExternalService() {
        return externalService;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}