package che.arshin.checkup.preprocessing.strategy;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.exception.NoMeasuringInstrumentException;
import che.arshin.checkup.web.dto.MIRequest;
import org.springframework.stereotype.Component;

@Component
public class MetranStrategy {

    public MIRequest process(MeasuringInstrument mi){
        if (mi == null) throw new NoMeasuringInstrumentException("No measuring instrument is submitted");
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