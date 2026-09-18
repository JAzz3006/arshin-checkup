package che.arshin.checkup.repository;
import che.arshin.checkup.entity.MeasuringInstrument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MIRepository
        extends JpaRepository<MeasuringInstrument, Long> {
}