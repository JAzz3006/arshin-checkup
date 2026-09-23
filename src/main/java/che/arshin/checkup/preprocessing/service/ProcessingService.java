package che.arshin.checkup.preprocessing.service;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.mapper.MIMapper;
import che.arshin.checkup.preprocessing.strategy.MetranStrategy;
import che.arshin.checkup.service.MIService;
import che.arshin.checkup.web.dto.MIRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessingService {
    private static final String METRAN_ATTRIBUTE = "метран";

    private final MIService miService;
    private final MIMapper miMapper;
    private final MetranStrategy metranStrategy;

    public void preProcessAll(){
        List<MeasuringInstrument> mis = miService.findAllMI();
        for (MeasuringInstrument mi : mis){
            if (mi.getModel() == null){
                //TODO set autoCheckup = false
                continue;
            }
            if (mi.getModel().toLowerCase(Locale.ROOT).contains(METRAN_ATTRIBUTE)){
                MIRequest request = metranStrategy.process(mi);
                miService.updateMI(mi.getId(), miMapper.from(request));
            }
        }
    }

    public void analyzeAutoCheckability(){
        List<MeasuringInstrument> mis = miService.findAllMI();
        MIRequest request = new MIRequest();
        for (MeasuringInstrument mi :mis){
            if (mi.getModel() == null ||
                    mi.getModel().isBlank() ||
                    mi.getSerialNumber() == null ||
                    mi.getSerialNumber().isBlank()){
                request.setAutoCheckUp(false);
                miService.updateMI(mi.getId(), miMapper.from(request));
            }else{
                request.setAutoCheckUp(true);
                miService.updateMI(mi.getId(), miMapper.from(request));
            }
        }
    }
}