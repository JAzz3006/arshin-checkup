package che.arshin.checkup.preprocessing.strategy;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.web.dto.MIRequest;

public interface PreProcessingStrategy {
    boolean supports(MeasuringInstrument mi);
    MIRequest process(MeasuringInstrument mi);
}