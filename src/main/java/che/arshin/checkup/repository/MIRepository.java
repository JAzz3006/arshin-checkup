package che.arshin.checkup.repository;
import che.arshin.checkup.entity.MeasuringInstrument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;

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

    @Query("""
            SELECT mi
            FROM MeasuringInstrument mi
            WHERE mi.modification = '75' AND CAST(mi.serialNumber AS Long) between :min and :max
            """)
    List<MeasuringInstrument> findAllNumberRange(
            @Param("min") Long min,
            @Param("max") Long max
    );
}