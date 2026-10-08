package che.arshin.checkup.web.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreProcessResult {
    private Long id;
    private String model;
    private String serialNumber;

    private PreProcessStatus status;
    private String message;

    private Instant preProcessTime;
}