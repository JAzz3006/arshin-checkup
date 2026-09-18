package che.arshin.checkup.service;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.exception.EntityNotFoundException;
import che.arshin.checkup.repository.MIRepository;
import che.arshin.checkup.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MIService {

    private final MIRepository miRepository;

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
}