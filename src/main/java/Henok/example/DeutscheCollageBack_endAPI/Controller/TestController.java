package Henok.example.DeutscheCollageBack_endAPI.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok("WellCome to Doutsche Collage Backend API!!");
    }
}
