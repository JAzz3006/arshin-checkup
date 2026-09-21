package che.arshin.checkup.client.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArshinResponseHeader {
    private int status;

    @JsonProperty("QTime")
    private long qTime;
}