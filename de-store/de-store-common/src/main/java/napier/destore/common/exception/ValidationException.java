package napier.destore.common.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationException extends ServiceException {

    private final List<ValidationError> errors;

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.errors = new ArrayList<>();
    }

    public ValidationException(String message, List<ValidationError> errors) {
        super(message, "VALIDATION_ERROR");
        this.errors = new ArrayList<>(errors);
    }

    public ValidationException(String field, String message) {
        super(message, "VALIDATION_ERROR");
        this.errors = new ArrayList<>();
        this.errors.add(new ValidationError(field, message));
    }

    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void addError(String field, String message) {
        this.errors.add(new ValidationError(field, message));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public record ValidationError(String field, String message) {
    }
}