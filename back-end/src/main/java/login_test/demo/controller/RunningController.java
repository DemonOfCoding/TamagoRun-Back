package login_test.demo.controller;

import login_test.demo.dto.RunningDto;
import login_test.demo.service.RunningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/running")
public class RunningController {
    private final RunningService runningService;

    // 프론트로부터 러닝 데이터를 받아 저장하고 저장된 데이터를 반환
    @PostMapping("/record")
    public ResponseEntity<RunningDto> dailyRunningRecord(@RequestBody RunningDto runningDto) {
        try {
            runningService.getRunningData(runningDto);
            // 저장 성공 시 요청받은 데이터를 그대로 반환
            return ResponseEntity.ok(runningDto);
        } catch (Exception e) {
            // 예외가 발생하면 HTTP 500 오류와 함께 null 또는 빈 DTO를 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}