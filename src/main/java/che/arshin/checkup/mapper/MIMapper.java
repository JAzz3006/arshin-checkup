package che.arshin.checkup.mapper;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.web.dto.ArshinQuery;
import che.arshin.checkup.web.dto.MIListResponse;
import che.arshin.checkup.web.dto.MIRequest;
import che.arshin.checkup.web.dto.MIResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MIMapper {

    MeasuringInstrument from(MIRequest request);

    ArshinQuery arshinQueryFrom(MeasuringInstrument mi);

    MIResponse from(MeasuringInstrument mi);

    List<MIResponse> from(List<MeasuringInstrument> instruments);

    default MIListResponse from(Page<MeasuringInstrument> instrumentPage){
        return MIListResponse.builder()
                .miResponses(from(instrumentPage.getContent()))
                .page(instrumentPage.getNumber())
                .size(instrumentPage.getSize())
                .totalElements(instrumentPage.getTotalElements())
                .totalPages(instrumentPage.getTotalPages())
                .first(instrumentPage.isFirst())
                .last(instrumentPage.isLast())
                .build();
    };
}