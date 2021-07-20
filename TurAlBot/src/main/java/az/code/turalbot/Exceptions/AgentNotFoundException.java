package az.code.turalbot.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AgentNotFoundException extends RuntimeException{
    public AgentNotFoundException(String message) {
        super(message);
    }
}
