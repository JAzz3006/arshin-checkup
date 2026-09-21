package che.arshin.checkup.web.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArshinQuery {
    private String miType;
    private String serialNumber;
    private String modification;
}