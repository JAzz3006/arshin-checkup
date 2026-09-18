package che.arshin.checkup.web.dto;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MIResponse {
    private Long id;
    private String model;
    private String serialNumber;
    private LocalDate lastVerificationDate;
    private LocalDate nextVerificationDate;
    private String queryMIName;
    private String queryMIDesignation;
    private String queryMIModification;
}