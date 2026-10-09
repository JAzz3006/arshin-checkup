package che.arshin.checkup.exception;

public class ReportWritingException extends RuntimeException{
    public ReportWritingException(String message, Throwable cause){
        super(message, cause);
    }
}
