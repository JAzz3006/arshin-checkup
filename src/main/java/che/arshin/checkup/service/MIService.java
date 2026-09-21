package che.arshin.checkup.service;
import che.arshin.checkup.client.ArshinClient;
import che.arshin.checkup.client.dto.ArshinResponse;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.exception.EntityNotFoundException;
import che.arshin.checkup.exception.BadArshinResponseException;
import che.arshin.checkup.mapper.MIMapper;
import che.arshin.checkup.repository.MIRepository;
import che.arshin.checkup.utils.BeanUtils;
import che.arshin.checkup.web.dto.ArshinQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
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

    public Page<MeasuringInstrument> findAllMI(Pageable pageable){
        return miRepository.findAll(pageable);
    }

    public MeasuringInstrument createMI(MeasuringInstrument mi){
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
        ArshinQuery arshinQuery = miMapper.arshinQueryFrom(mi);
        ArshinResponse arshinResponse = arshinClient.getArshinResponse(arshinQuery);
        if (arshinResponse == null) throw new BadArshinResponseException("Cannot get GIS Arshin response");
        if (arshinResponse.getResponse().getNumFound() == 1){
            Instant verificationDate = arshinResponse.getResponse().getDocs().getFirst().getVerificationDate();
            Instant validDate = arshinResponse.getResponse().getDocs().getFirst().getValidDate();
            if (validDate.isAfter(mi.getValidDate())){
                mi.setVerificationDate(verificationDate);
                mi.setValidDate(validDate);
            }
            return miRepository.save(mi);
        } else if (arshinResponse.getResponse().getNumFound() == 0) {
            throw new BadArshinResponseException("GIS Arshin response contains no response");
            //надо проверить каков ответ, если СИ не найдено
        }
        throw new BadArshinResponseException("GIS Arshin response contains more than one response");
    }
}