package che.arshin.checkup.service;
import che.arshin.checkup.client.ArshinClient;
import che.arshin.checkup.client.dto.ArshinResponse;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.exception.EntityNotFoundException;
import che.arshin.checkup.mapper.MIMapper;
import che.arshin.checkup.repository.MIRepository;
import che.arshin.checkup.utils.BeanUtils;
import che.arshin.checkup.web.dto.ArshinQuery;
import che.arshin.checkup.web.dto.MIRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import che.arshin.checkup.exception.BadArshinResponseException;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MIService {

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

    public MeasuringInstrument checkVerificationById(Long id){
        MeasuringInstrument mi = findMIById(id);
        if (!mi.getAutoCheckUp()) return mi;

        ArshinQuery arshinQuery = miMapper.arshinQueryFrom(mi);
        ArshinResponse arshinResponse = arshinClient.getArshinResponse(arshinQuery);

        if (arshinResponse == null) throw new BadArshinResponseException("Cannot get GIS Arshin response");

        log.warn("NumFound = {}", arshinResponse.getResponse().getNumFound());

        if (arshinResponse.getResponse().getNumFound() > 0){
            //TODO make it not more than 3
            Instant verificationDate = arshinResponse.getResponse().getDocs().getFirst().getVerificationDate();
            Instant validDate = arshinResponse.getResponse().getDocs().getFirst().getValidDate();
            Integer resultsCount = arshinResponse.getResponse().getNumFound();
            log.warn("ResultsCount = {}", resultsCount);
            if (mi.getValidDate() == null || validDate.isAfter(mi.getValidDate())){
                mi.setVerificationDate(verificationDate);
                mi.setValidDate(validDate);
                mi.setResultsCount(resultsCount);
            }
            return miRepository.save(mi);
        } else if (arshinResponse.getResponse().getNumFound() == 0) {
            throw new BadArshinResponseException("GIS Arshin response contains no response");
            //надо проверить каков ответ, если СИ не найдено
        } else throw new BadArshinResponseException("GIS Arshin response contains more than one response");
    }

    public void fillDB(List<MIRequest> requests){
        for (MIRequest request : requests){
            createMI(miMapper.from(request));
        }
    }
}