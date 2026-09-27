package che.arshin.checkup.web.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerificationListResult {

    @Builder.Default
    private List<VerificationResult> results = new ArrayList<>();

    @Builder.Default
    private Map<String, Integer> summary = new HashMap<>();
}