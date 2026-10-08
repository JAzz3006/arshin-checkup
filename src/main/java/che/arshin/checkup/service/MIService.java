package che.arshin.checkup.service;
import che.arshin.checkup.client.ArshinClient;
import che.arshin.checkup.client.dto.ArshinDocument;
import che.arshin.checkup.client.dto.ArshinResponse;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.exception.EntityNotFoundException;
import che.arshin.checkup.mapper.MIMapper;
import che.arshin.checkup.repository.MIRepository;
import che.arshin.checkup.utils.BeanUtils;
import che.arshin.checkup.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import che.arshin.checkup.exception.BadArshinResponseException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MIService {

    @Value("${app.settings.max-arshin-results-count}")
    private int maxArshinResultsCount;
    @Value("${app.settings.request-delay-ms}")
    private int requestDelay;

    private final MIRepository miRepository;
    private final ArshinClient arshinClient;
    private final MIMapper miMapper;

    public MeasuringInstrument findMIById(Long id){
        return miRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("MI with id='%d' not found", id)
                        )
                );
    }

    public List<MeasuringInstrument> findAllMI(){
        return miRepository.findAll();
    }

    public List<MeasuringInstrument> findAllMIInSerialNumberRange(Long min, Long max){
        return miRepository.findAllNumberRange(min, max);
    }

    public Page<MeasuringInstrument> findAllMI(Pageable pageable){
        return miRepository.findAll(pageable);
    }

    public Page<MeasuringInstrument> findAllMIVerificationNeeded(Pageable pageable){
        return miRepository.findAllNeedsVerification(pageable, Instant.now());
    }

    public MeasuringInstrument createMI(MeasuringInstrument mi){
        mi.setAutoCheckUp(true);
        //TODO подумать над автоматическим запросом к ГИС при создании СИ
        return miRepository.save(mi);
    }

    public MeasuringInstrument updateMI(Long id, MeasuringInstrument mi){
        MeasuringInstrument currentMI = findMIById(id);
        BeanUtils.copyNonNullProperties(mi, currentMI);
        return miRepository.save(currentMI);
    }

    public void deleteMIById(Long id){
        miRepository.deleteById(id);
    }

    public VerificationResult checkVerificationById(Long id){

        MeasuringInstrument mi = findMIById(id);
        Instant previousValidDate = mi.getValidDate();

        if (!Boolean.TRUE.equals(mi.getAutoCheckUp())){
            return VerificationResult.builder()
                    .id(id)
                    .model(mi.getModel())
                    .serialNumber(mi.getSerialNumber())
                    .status(VerificationStatus.NO_AUTO_CHECK)
                    .message("СИ не входит в группу проверяемых автоматически")
                    .previousValidDate(previousValidDate)
                    .build();
        }

        ArshinQuery arshinQuery = miMapper.arshinQueryFrom(mi);
        ArshinResponse arshinResponse = arshinClient.getArshinResponse(arshinQuery);

        if (arshinResponse == null){
            throw new BadArshinResponseException ("ГИС Аршин не предоставил ответ");
        }

        int resultCount = arshinResponse.getResponse().getNumFound();
        log.info("resultCount = {}", resultCount);

        List<ArshinDocument> docs = arshinResponse.getResponse().getDocs();

        if (resultCount == 0 || docs == null || docs.isEmpty()){
            return VerificationResult.builder()
                    .id(id)
                    .model(mi.getModel())
                    .serialNumber(mi.getSerialNumber())
                    .status(VerificationStatus.NO_RESULT)
                    .message(String.format("В ГИС Аршин нет данных о поверке СИ с id='%d'", id))
                    .previousValidDate(previousValidDate)
                    .resultCount(resultCount)
                    .applicability(mi.getApplicability())
                    .build();
        }

        ArshinDocument document = docs.getFirst(); // Arshin response is sorted by verification_date desc, so the first document contains the latest verification

        if (resultCount > maxArshinResultsCount){
            return VerificationResult.builder()
                    .id(id)
                    .model(mi.getModel())
                    .serialNumber(mi.getSerialNumber())
                    .status(VerificationStatus.MULTIPLE_RESULTS)
                    .message(
                            String.format("Неопределенность результата - ГИС Аршин вернул более '%d' результатов",
                                    maxArshinResultsCount)
                    )
                    .previousValidDate(previousValidDate)
                    .resultCount(resultCount)
                    .applicability(mi.getApplicability())
                    .build();
        }

        boolean miUsable = document.isApplicability();
        Instant verificationDate = document.getVerificationDate();
        Instant validDate = document.getValidDate();

        if(!miUsable) {
            mi.setVerificationDate(verificationDate);
            mi.setValidDate(validDate);
            mi.setResultsCount(resultCount);
            mi.setApplicability(false);
            MeasuringInstrument savedMI = miRepository.save(mi);
            return VerificationResult.builder()
                    .id(id)
                    .model(mi.getModel())
                    .serialNumber(mi.getSerialNumber())
                    .status(VerificationStatus.UNUSABLE)
                    .message("СИ признано непригодным")
                    .previousValidDate(previousValidDate)
                    .arshinValidDate(savedMI.getValidDate())
                    .resultCount(resultCount)
                    .applicability(savedMI.getApplicability())
                    .build();
        }else if(mi.getValidDate() == null || validDate.isAfter(mi.getValidDate())) {
            mi.setVerificationDate(verificationDate);
            mi.setValidDate(validDate);
            mi.setResultsCount(resultCount);
            mi.setApplicability(true);
            MeasuringInstrument savedMI = miRepository.save(mi);
            return VerificationResult.builder()
                    .id(id)
                    .model(mi.getModel())
                    .serialNumber(mi.getSerialNumber())
                    .status(VerificationStatus.UPDATED)
                    .message("Даты поверки СИ обновлены")
                    .previousValidDate(previousValidDate)
                    .arshinValidDate(savedMI.getValidDate())
                    .resultCount(resultCount)
                    .applicability(savedMI.getApplicability())
                    .build();
        }else{
            mi.setResultsCount(resultCount);
            mi.setApplicability(true);
            miRepository.save(mi);
            return VerificationResult.builder()
                    .id(id)
                    .model(mi.getModel())
                    .serialNumber(mi.getSerialNumber())
                    .status(VerificationStatus.NOT_UPDATED)
                    .message("Даты поверки СИ не обновлены - ГИС не содержит актуальных дат")
                    .previousValidDate(previousValidDate)
                    .resultCount(resultCount)
                    .applicability(true)
                    .build();
        }
    }

    public VerificationListResult checkVerificationByMultipleIds(List<Long> ids){
        VerificationListResult report = new VerificationListResult();
        for (Long id : ids){
            try{
                VerificationResult result = checkVerificationById(id);
                report.getResults().add(result);
                Thread.sleep(requestDelay);
            }catch (EntityNotFoundException e){
                report.getResults().add(
                        VerificationResult.builder()
                                .id(id)
                                .status(VerificationStatus.NOT_FOUND)
                                .message(String.format("СИ с id = '%d' не найдено в БД", id))
                                .build()
                );
            }catch (BadArshinResponseException e){
                report.getResults().add(
                        VerificationResult.builder()
                                .id(id)
                                .status(VerificationStatus.ERROR)
                                .message(String.format("ГИС Аршин не предоставил ответ на запрос по СИ id = '%d'", id))
                                .build()
                );
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                throw new RuntimeException(
                        String.format(
                                "Выполнение проверки прервано при обработке СИ с id='%d'", id
                        ),
                        e
                );
            }
        }
        Map<String, Integer> summary = BeanUtils.getSummaryTemplate();
        for (VerificationResult r : report.getResults()) {
            summary.merge(r.getStatus().name(), 1, Integer::sum);
        }
        report.setSummary(summary);
        return report;
    }

    public void fillDB(List<MIRequest> requests){
        for (MIRequest request : requests){
            log.info("СИ '{}' номер '{}'", request.getModel(), request.getSerialNumber());
            createMI(miMapper.from(request));
        }
//        requests.stream() //тестовый вариант
//                .limit(100)
//                .forEach(r -> log.info("СИ '{}' номер '{}'", r.getModel(), r.getSerialNumber()));
    }
}