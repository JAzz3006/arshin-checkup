package che.arshin.checkup.preprocessing.strategy;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.web.dto.MIRequest;
import che.arshin.checkup.web.dto.MIResponse;

import java.util.List;
import java.util.regex.Pattern;

public class TekonStrategy implements PreProcessingStrategy{

    private static final List<Pattern> METRAN_PATTERN = List.of(
            Pattern.compile("тэкон", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
    );

    @Override
    public boolean supports(MeasuringInstrument mi) {
        if (mi.getModel() == null || mi.getModel().isBlank()) return false;
        return METRAN_PATTERN.stream()
                .anyMatch(p -> p.matcher(mi.getModel()).find());
    }

    @Override
    public MIRequest process(MeasuringInstrument mi) {
        MIRequest request = new MIRequest();
        request.setMiType("тэкон");
        return request;
    }
}
