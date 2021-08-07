package az.code.Tour032.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AgentExistsException extends RuntimeException {
    public AgentExistsException(String message) {
        super(message);
    }
}
