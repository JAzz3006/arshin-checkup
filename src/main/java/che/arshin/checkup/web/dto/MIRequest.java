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
    private String miType;
    private String modification;
    private String miTitle;
    private Boolean autoCheckUp;
}