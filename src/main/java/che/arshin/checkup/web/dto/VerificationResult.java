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
public class VerificationResult {
    private Long id;
    private String model;
    private String serialNumber;

    private VerificationStatus status;
    private String message;

    private Instant previousValidDate;
    private Instant arshinValidDate;

    private Integer resultCount;
    private Boolean applicability;
}