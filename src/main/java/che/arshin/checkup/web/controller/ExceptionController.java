package che.arshin.checkup.web.controller;
import che.arshin.checkup.exception.*;
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

    @ExceptionHandler(MultipleMatchedStrategiesException.class)
    public ResponseEntity<ErrorResponse> handleMultipleMatchedStrategiesException(MultipleMatchedStrategiesException e){
        log.warn(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(StrategyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStrategyNotFoundException(StrategyNotFoundException e){
        log.warn(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ErrorResponse(e.getMessage()));
    }

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

    @ExceptionHandler(NoMeasuringInstrumentException.class)
    public ResponseEntity<ErrorResponse> handleNoMeasuringInstrumentException(NoMeasuringInstrumentException e){
        log.warn(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ExcelParseException.class)
    public ResponseEntity<ErrorResponse> handleExcelParseException(ExcelParseException e){
        log.warn(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ReportWritingException.class)
    public ResponseEntity<ErrorResponse> handleReportWritingException(
            ReportWritingException e
    ) {
        log.error("Ошибка формирования отчёта", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "Не удалось сохранить отчёт препроцессинга"
                ));
    }
}