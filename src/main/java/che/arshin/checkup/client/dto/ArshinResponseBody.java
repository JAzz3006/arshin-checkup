package che.arshin.checkup.client.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArshinResponseBody {

    private int numFound;

    private int start;

    private boolean numFoundExact;

    List<ArshinDocument> docs = new ArrayList<>();
}