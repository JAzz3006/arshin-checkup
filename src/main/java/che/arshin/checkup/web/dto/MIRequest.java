package che.arshin.checkup.web.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MIRequest {
    private String model;
    private String serialNumber;
    private String queryMIName;
    private String queryMIDesignation;
    private String queryMIModification;
}