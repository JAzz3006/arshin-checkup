package che.arshin.checkup.repository;
import che.arshin.checkup.entity.MeasuringInstrument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;

public interface MIRepository
        extends JpaRepository<MeasuringInstrument, Long> {

    @Query("""
            SELECT mi
            FROM MeasuringInstrument mi
            WHERE mi.validDate IS NULL
            OR mi.validDate < :date
            """)
    Page<MeasuringInstrument> findAllNeedsVerification(
            Pageable pageable,
            @Param("date") Instant date
    );
}