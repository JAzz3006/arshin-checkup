package che.arshin.checkup.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "measuring_instruments")
public class MeasuringInstrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "model")
    private String model;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "last_verification_date")
    private LocalDate lastVerificationDate;

    @Column(name = "next_verification_date")
    private LocalDate nextVerificationDate;

    //TODO подумать над расширением перечня query_
    @Column(name = "query_MI_name")
    private String queryMIName;

    @Column(name = "query_MI_designation")
    private String queryMIDesignation;

    @Column(name = "query_MI_modification")
    private String queryMIModification;
}