package che.arshin.checkup.preprocessing.service;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.exception.MultipleMatchedStrategiesException;
import che.arshin.checkup.exception.ReportWritingException;
import che.arshin.checkup.exception.StrategyNotFoundException;
import che.arshin.checkup.mapper.MIMapper;
import che.arshin.checkup.preprocessing.strategy.PreProcessingStrategy;
import che.arshin.checkup.service.MIService;
import che.arshin.checkup.web.dto.MIRequest;
import che.arshin.checkup.web.dto.PreProcessResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessingService {

    @Value("${app.reports.directory}")
    private String reportsDirectory;

    private final List<PreProcessingStrategy> strategies;
    private final MIService miService;
    private final MIMapper miMapper;

    public Map<PreProcessStatus, Long> preProcessManager(){
        List<PreProcessResult> results = preProcessAll();
        try{
            Path reportPath = preProcessReportWriter(results);
            log.info("Отчет о препроцессинге сохранен в файл {}", reportPath);
        }catch (IOException e){
            throw new ReportWritingException("Не удалось записать отчет о предварительном процессинге", e);
        }
        return buildPreprocessSummary(results);
    }

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

    public List<PreProcessResult> preProcessAll(){
        List<PreProcessResult> results = new ArrayList<>();
        List<MeasuringInstrument> mis = miService.findAllMI();
        for (MeasuringInstrument mi : mis) {
            if (Boolean.FALSE.equals(mi.getApplicability())){
                results.add(buildResult(mi.getId(), mi.getModel(), mi.getSerialNumber(), PreProcessStatus.NOT_APPLICABLE, "СИ непригодно"));
                continue;
            }
            if (mi.getModel() == null ||
                    mi.getModel().isBlank() ||
                    mi.getSerialNumber() == null ||
                    mi.getSerialNumber().isBlank()){
                results.add(buildResult(mi.getId(), mi.getModel(), mi.getSerialNumber(), PreProcessStatus.NOT_AUTO_CHECKABLE, "Отсутствует модель или серийный номер"));
                continue;
            }
            try {
                preProcessOne(mi);
                results.add(buildResult(mi.getId(), mi.getModel(), mi.getSerialNumber(), PreProcessStatus.PROCESSED, "OK"));
            } catch (StrategyNotFoundException e) {
                results.add(buildResult(mi.getId(), mi.getModel(), mi.getSerialNumber(), PreProcessStatus.NO_STRATEGY_FOUND, e.getMessage()));
            } catch (MultipleMatchedStrategiesException e) {
                results.add(buildResult(mi.getId(), mi.getModel(), mi.getSerialNumber(), PreProcessStatus.MULTIPLE_STRATEGIES_FOUND, e.getMessage()));
            } catch (Exception e) {
                log.error("Ошибка препроцессинга СИ id={}", mi.getId(), e);
                results.add(buildResult(mi.getId(), mi.getModel(), mi.getSerialNumber(), PreProcessStatus.ERROR, e.getMessage()));
            }
        }
        return results;
    }

    public Path preProcessReportWriter(List<PreProcessResult> results) throws IOException {
        Path reportsDir = Path.of(reportsDirectory);
        Files.createDirectories(reportsDir);
        Path filePath = reportsDir.resolve(generatePreProcessReportName());
        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath,
                StandardCharsets.UTF_8
        )){
            writer.write("ID;Модель;Серийный номер;Статус;Сообщение;Время обработки");
            writer.newLine();
            for (PreProcessResult result : results){
                writer.write(String.join(";",
                        csvValue(result.getId()),
                        csvValue(result.getModel()),
                        csvValue(result.getSerialNumber()),
                        csvValue(result.getStatus()),
                        csvValue(result.getMessage()),
                        csvValue(result.getPreProcessTime())
                ));
                writer.newLine();
            }
        }
        return filePath;
    }

    private Map<PreProcessStatus, Long> buildPreprocessSummary(List<PreProcessResult> results){
        Map<PreProcessStatus, Long> summary = new EnumMap<>(PreProcessStatus.class);
        for (PreProcessStatus status : PreProcessStatus.values()){
            summary.put(status, 0L);
        }
        for (PreProcessResult result : results){
            summary.merge(result.getStatus(),1L, Long::sum);
        }
        return summary;
    }

    private String generatePreProcessReportName() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
                        .withZone(ZoneId.systemDefault());
        return "preprocess" + formatter.format(Instant.now()) + ".csv";
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

    private String csvValue(Object value) {
        if (value == null) return "";

        String text = value.toString();

        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}