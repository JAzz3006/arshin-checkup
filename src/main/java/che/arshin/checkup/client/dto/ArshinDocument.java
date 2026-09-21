package che.arshin.checkup.client.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArshinDocument {

    @JsonProperty("mi.mitnumber")
    private String miTypeNumber;

    @JsonProperty("mi.modification")
    private String modification;

    @JsonProperty("mi.number")
    private String serialNumber;

    @JsonProperty("valid_date")
    private Instant validDate;

    @JsonProperty("result_docnum")
    private String resultDocumentNumber;

    @JsonProperty("mi.mitype")
    private String miType;

    @JsonProperty("mi.mititle")
    private String miTitle;

    @JsonProperty("org_title")
    private String organizationTitle;

    private boolean applicability;

    @JsonProperty("vri_id")
    private String verificationId;

    @JsonProperty("verification_date")
    private Instant verificationDate;
}