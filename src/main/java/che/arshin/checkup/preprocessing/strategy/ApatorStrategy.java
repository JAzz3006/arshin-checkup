package che.arshin.checkup.preprocessing.strategy;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.web.dto.MIRequest;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ApatorStrategy implements PreProcessingStrategy{

    private static final List<Pattern> APATOR_PATTERN = List.of(
            Pattern.compile("apator", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
    );

    @Override
    public boolean supports(MeasuringInstrument mi) {
        if (mi.getModel() == null || mi.getModel().isBlank()) return false;
        return APATOR_PATTERN.stream()
                .anyMatch(p -> p.matcher(mi.getModel()).find());
    }

    @Override
    public MIRequest process(MeasuringInstrument mi) {
        MIRequest request = new MIRequest();
        request.setModification("*apator*");
        return request;
    }
}