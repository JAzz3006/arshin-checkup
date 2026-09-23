package che.arshin.checkup.web.dto;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MIResponse {
    private Long id;
    private String model;
    private String serialNumber;
    private Instant verificationDate;
    private Instant validDate;
    private String miType;
    private String modification;
    private String miTitle;
    private Boolean autoCheckUp;
    private Integer resultsCount;
}