package che.arshin.checkup.web.controller;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.mapper.MIMapper;
import che.arshin.checkup.preprocessing.excel.ExcelParser;
import che.arshin.checkup.service.MIService;
import che.arshin.checkup.web.dto.MIListRequest;
import che.arshin.checkup.web.dto.MIListResponse;
import che.arshin.checkup.web.dto.MIRequest;
import che.arshin.checkup.web.dto.MIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mi")
public class MIController {

    private final MIMapper miMapper;
    private final MIService miService;
    private final ExcelParser excelParser;

    @GetMapping("/{id}")
    public ResponseEntity<MIResponse> getMIById(@PathVariable Long id){
        return ResponseEntity.ok(
                miMapper.from(
                        miService.findMIById(id)
                )
        );
    }

    @GetMapping("/in-number-range")
    public ResponseEntity<List<MIResponse>> gelAllMIInSerialNubberRange(
            @RequestParam Long min,
            @RequestParam Long max
    ){
        return ResponseEntity.ok(
                miMapper.from(
                        miService.findAllMIInSerialNumberRange(min, max)
                )
        );
    }

    @GetMapping()
    public ResponseEntity<MIListResponse> gelAllMI(
            @RequestParam int page,
            @RequestParam int size
    ){
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").ascending()
        );
        return ResponseEntity.ok(
                miMapper.from(
                        miService.findAllMI(pageable)
                )
        );
    }

    @GetMapping("/v-needed")
    public ResponseEntity<MIListResponse> getAllMIVerificationNeeded(
            @RequestParam (defaultValue = "${app.pagination.default-page-number}") int page,
            @RequestParam (defaultValue = "${app.pagination.default-page-size}") int size
    ){
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").ascending()
        );
        return ResponseEntity.ok(
                miMapper.from(
                        miService.findAllMIVerificationNeeded(pageable)
                )
        );
    }

    @PostMapping()
    public ResponseEntity<MIResponse> registerMI(@RequestBody MIRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                miMapper.from(
                        miService.createMI(
                                miMapper.from(request)
                        )
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<MIResponse> updateMI(
            @PathVariable Long id,
            @RequestBody MIRequest request
    ){
        return ResponseEntity.ok(
                miMapper.from(
                        miService.updateMI(id, miMapper.from(request))
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MIResponse> deleteMIById(@PathVariable Long id){
        MeasuringInstrument currentMI = miService.findMIById(id);
        miService.deleteMIById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                miMapper.from(currentMI)
        );
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<MIResponse> checkVerificationById(@PathVariable Long id){
        return ResponseEntity.ok(
                miMapper.from(
                        miService.checkVerificationById(id)
                )
        );
    }

    @PostMapping("/verify-group")
    public ResponseEntity<String> checkVerificationByIds(@RequestBody MIListRequest request){
        String report = "";
        for (Long id : request.getIds()){
            MeasuringInstrument mi = miService.checkVerificationById(id);
        }


        return ResponseEntity.ok(
                miMapper.from(
                        miService.checkVerificationById(id)
                )
        );
    }

    @PostMapping("/fill-in-massive")
    public ResponseEntity<Void> massiveFillIn(){
        try{
            miService.fillDB(excelParser.getRequests());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}