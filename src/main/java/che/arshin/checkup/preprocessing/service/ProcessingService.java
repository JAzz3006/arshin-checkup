package che.arshin.checkup.preprocessing.service;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.exception.MultipleMatchedStrategiesException;
import che.arshin.checkup.exception.StrategyNotFoundException;
import che.arshin.checkup.mapper.MIMapper;
import che.arshin.checkup.preprocessing.strategy.PreProcessingStrategy;
import che.arshin.checkup.service.MIService;
import che.arshin.checkup.web.dto.MIRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessingService {

    private final List<PreProcessingStrategy> strategies;
    private final MIService miService;
    private final MIMapper miMapper;

    public void preProcessOne(MeasuringInstrument mi){
         List<PreProcessingStrategy> matchedStrategies = strategies.stream()
                .filter(strategy -> strategy.supports(mi))
                .toList();

         if (matchedStrategies.isEmpty()){
             throw new StrategyNotFoundException(String.format(
                     "Подходящая стратегия для СИ с id='%d' не найдена", mi.getId())
             );
         }

         if (matchedStrategies.size() > 1){
             throw new MultipleMatchedStrategiesException(String.format("Найдено %d страт. для обработки СИ с id='%d'",
                     matchedStrategies.size(),
                     mi.getId())
             );
         }

        MIRequest request = matchedStrategies.getFirst().process(mi);

        miService.updateMI(mi.getId(), miMapper.from(request));
    }

    public void analyzeAutoCheckability(){
        List<MeasuringInstrument> mis = miService.findAllMI();
        MIRequest request = new MIRequest();
        for (MeasuringInstrument mi :mis){
            if (mi.getModel() == null ||
                    mi.getModel().isBlank() ||
                    mi.getSerialNumber() == null ||
                    mi.getSerialNumber().isBlank()||
                    mi.getApplicability().equals(Boolean.FALSE)){
                request.setAutoCheckUp(false);
                miService.updateMI(mi.getId(), miMapper.from(request));
            }else{
                request.setAutoCheckUp(true);
                miService.updateMI(mi.getId(), miMapper.from(request));
            }
        }
    }
}