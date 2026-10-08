package che.arshin.checkup.preprocessing.service;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.exception.MultipleMatchedStrategiesException;
import che.arshin.checkup.exception.StrategyNotFoundException;
import che.arshin.checkup.mapper.MIMapper;
import che.arshin.checkup.preprocessing.strategy.PreProcessingStrategy;
import che.arshin.checkup.service.MIService;
import che.arshin.checkup.web.dto.MIRequest;
import che.arshin.checkup.web.dto.PreProcessResult;
import che.arshin.checkup.web.dto.PreProcessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
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

    public void preProcessAll(){
        List<PreProcessResult> results = new ArrayList<>();
        List<MeasuringInstrument> mis = miService.findAllMI();
        for (MeasuringInstrument mi : mis) {
            try {
                preProcessOne(mi);
                results.add(buildResult(mi.getId(), mi.getModel(), mi.getSerialNumber(), PreProcessStatus.PROCESSED, "OK"));
            } catch (StrategyNotFoundException e) {
                results.add(buildResult(mi.getId(), mi.getModel(), mi.getSerialNumber(), PreProcessStatus.NO_STRATEGY_FOUND, e.getMessage()));
            } catch (MultipleMatchedStrategiesException e) {
                results.add(buildResult(mi.getId(), mi.getModel(), mi.getSerialNumber(), PreProcessStatus.MULTIPLE_STRATEGIES_FOUND, e.getMessage()));
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

    private PreProcessResult buildResult(
            Long id,
            String model,
            String serialNumber,
            PreProcessStatus status,
            String message){
        return PreProcessResult.builder()
                .id(id)
                .model(model)
                .serialNumber(serialNumber)
                .status(status)
                .message(message)
                .preProcessTime(Instant.now())
                .build();
    }
}