package che.arshin.checkup.preprocessing.controller;
import che.arshin.checkup.mapper.MIMapper;
import che.arshin.checkup.preprocessing.service.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mi/preprocess")
public class ProcessingController {

    private final ProcessingService processingService;
    private final MIMapper miMapper;

    @PostMapping("/all")
    public ResponseEntity<String> preprocessAllMI(){
        processingService.preProcessAll();
        return ResponseEntity.ok("DONE!"); //TODO set something more meaningful
    }

    @PostMapping("/auto-check")
    public ResponseEntity<String> analyzeAutoCheck(){
        processingService.analyzeAutoCheckability();
        return ResponseEntity.ok("DONE!"); //TODO set something more meaningful
    }
}