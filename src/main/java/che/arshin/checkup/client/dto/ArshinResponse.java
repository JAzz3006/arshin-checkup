package che.arshin.checkup.client.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArshinResponse {
    private ArshinResponseHeader responseHeader;
    private ArshinResponseBody response;
}