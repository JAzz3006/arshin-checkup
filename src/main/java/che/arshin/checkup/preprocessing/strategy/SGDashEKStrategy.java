package che.arshin.checkup.preprocessing.strategy;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.web.dto.MIRequest;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class SGDashEKStrategy implements PreProcessingStrategy{

    private static final List<Pattern> SGDashEK_PATTERN = List.of(
            Pattern.compile("^СГ-ЭК.+", Pattern.UNICODE_CASE)
    );

    @Override
    public boolean supports(MeasuringInstrument mi) {
        if (mi.getModel() == null || mi.getModel().isBlank()) return false;
        return SGDashEK_PATTERN.stream()
                .anyMatch(p -> p.matcher(mi.getModel()).find());
    }

    @Override
    public MIRequest process(MeasuringInstrument mi) {
        MIRequest request = new MIRequest();
        request.setMiType("СГ-ЭК");
        return request;
    }
}