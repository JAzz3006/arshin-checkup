package che.arshin.checkup.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

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

    @Column(name = "mi_number")
    private String serialNumber;

    @Column(name = "verification_date")
    private Instant verificationDate;

    @Column(name = "valid_date")
    private Instant validDate;

    //TODO подумать над расширением перечня query
    @Column(name = "mi_mitype")
    private String miType;

    @Column(name = "mi_modification")
    private String modification;

    @Column(name = "mi_mititle")
    private String miTitle;
}