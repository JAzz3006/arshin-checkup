package che.arshin.checkup.web.controller;
import che.arshin.checkup.entity.MeasuringInstrument;
import che.arshin.checkup.mapper.MIMapper;
import che.arshin.checkup.preprocessing.excel.ExcelParser;
import che.arshin.checkup.service.MIService;
import che.arshin.checkup.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mi")
public class MIController {

    private final MIMapper miMapper;
    private final MIService miService;
    private final ExcelParser excelParser;
    private AtomicInteger callCount = new AtomicInteger(0);

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
    public ResponseEntity<VerificationResult> checkVerificationById(@PathVariable Long id){
        return ResponseEntity.ok(
                miService.checkVerificationById(id)
        );
    }

    @PostMapping("/verify-group")
    public ResponseEntity<VerificationListResult> checkVerificationByIds(@RequestBody MIListRequest request){
        return ResponseEntity.ok(
                miService.checkVerificationByMultipleIds(request.getIds())
        );
    }

    @PostMapping("/fill-in-massive")
    public ResponseEntity<Void> massiveFillIn() throws IOException{

        int callNumber = callCount.incrementAndGet();

        log.warn("========== IMPORT CALL #{} START==========", callNumber);

        List<MIRequest> requests = excelParser.getRequests();
        log.warn("IMPORT CALL #{}, parsed {} requests", callNumber, requests.size());


        miService.fillDB(requests);
        log.warn("========== IMPORT CALL #{} FINISH==========", callNumber);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/test-timeout")
    public ResponseEntity<String> testTimeout()
            throws InterruptedException {

        log.warn("TIMEOUT TEST START");

        Thread.sleep(90_000);

        log.warn("TIMEOUT TEST FINISH");

        return ResponseEntity.ok("OK");
    }
}