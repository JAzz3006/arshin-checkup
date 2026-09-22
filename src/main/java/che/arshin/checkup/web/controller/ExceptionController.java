package che.arshin.checkup.web.controller;
import che.arshin.checkup.exception.BadArshinResponseException;
import che.arshin.checkup.exception.EntityNotFoundException;
import che.arshin.checkup.exception.ExcelParseException;
import che.arshin.checkup.web.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.callback.ReactiveEntityCallbacks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException (EntityNotFoundException e){
        log.warn(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(BadArshinResponseException.class)
    public ResponseEntity<ErrorResponse> handleBadArshinResponseException(BadArshinResponseException e){
        log.warn(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ExcelParseException.class)
    public ResponseEntity<ErrorResponse> handleExcelParseException(ExcelParseException e){
        log.warn(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
    }
}
