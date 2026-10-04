package che.arshin.checkup.preprocessing.strategy;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.exception.NoMeasuringInstrumentException;
import che.arshin.checkup.web.dto.MIRequest;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class MetranStrategy1 implements PreProcessingStrategy{

    private static final List<Pattern> METRAN_PATTERN = List.of(
            Pattern.compile("метран", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("metran", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
    );
    @Override
    public boolean supports(MeasuringInstrument mi) {
        if (mi.getModel() == null || mi.getModel().isBlank()) return false;
        return METRAN_PATTERN.stream()
                .anyMatch(p -> p.matcher(mi.getModel()).find());
    }

    @Override
    public MIRequest process(MeasuringInstrument mi) {
        if (mi == null) throw new NoMeasuringInstrumentException("СИ не передано");
        MIRequest request = new MIRequest();
        request.setMiType("метран");
        String model = mi.getModel();
        int start = model.indexOf('-') + 1;

        int end = start;
        while (end < model.length() && Character.isDigit(model.charAt(end))){
            end++;
        }
        String modification = model.substring(start, end);

        if (!modification.equals("")){
            request.setModification(modification);
        }else request.setModification(null);

        return request;
    }
}