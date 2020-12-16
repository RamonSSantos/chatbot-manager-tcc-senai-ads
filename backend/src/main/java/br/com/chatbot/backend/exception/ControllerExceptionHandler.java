package br.com.chatbot.backend.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.security.AccessControlException;
import java.time.LocalDateTime;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "OPS! ocorreu um erro inesperado. Tente novamente!");
        return ResponseEntityBuilder.build(errorMessage);
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<Object> handleIllegalArgumentExceptionOrNullPointerException(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntityBuilder.build(errorMessage);
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<Object> handleIllegalStateException(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage());
        return ResponseEntityBuilder.build(errorMessage);
    }

    @ExceptionHandler({AccessControlException.class})
    public ResponseEntity<Object> handleAccessControlException(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return ResponseEntityBuilder.build(errorMessage);
    }

    @ExceptionHandler({FileStorageException.class})
    public ResponseEntity<Object> handleFileStorageException(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage());
        return ResponseEntityBuilder.build(errorMessage);
    }

    @ExceptionHandler({FileNotFoundException.class})
    public ResponseEntity<Object> handleFileNotFoundException(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntityBuilder.build(errorMessage);
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpStatus.EXPECTATION_FAILED.value(),
                "O tamanho do arquivo anexado excedeu o limite de 1MB.");
        return ResponseEntityBuilder.build(errorMessage);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorMessage err = new ErrorMessage(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Method Not Found");
        return ResponseEntityBuilder.build(err);
    }
}