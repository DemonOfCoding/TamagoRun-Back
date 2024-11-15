package login_test.demo.controller;

import login_test.demo.dto.CoordinateResponseDTO;
import login_test.demo.dto.RunningDto;
import login_test.demo.model.Coordinate;
import login_test.demo.service.RunningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/running")
public class RunningController {
    private final RunningService runningService;

    // 프론트로부터 러닝 데이터를 받아 저장하고 저장된 데이터를 반환
    @PostMapping("/record")
    public ResponseEntity<String> dailyRunningRecord(@RequestBody RunningDto runningDto) {
        try {
            runningService.getRunningData(runningDto);
            // 저장 성공 시 요청받은 데이터를 그대로 반환
            return ResponseEntity.ok("러닝 데이터의 저장을 완료했습니다.");
        } catch (Exception e) {
            // 예외가 발생하면 HTTP 500 오류와 함께 null 또는 빈 DTO를 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("러닝 데이터 저장을 실패했습니다.");
        }
    }

    // 특정 날짜의 러닝 데이터를 조회

    @GetMapping("/record/{date}")
    public ResponseEntity<?> getDailyCoordinate(@RequestHeader(name = "SessionId") String sessionId, @PathVariable("date") String date) {
        try {
            LocalDate localDate = LocalDate.parse(date); // yyyy-MM-dd 형식의 문자열을 LocalDate로 변환
            List<Coordinate> coordinates = runningService.getDailyCoordinates(sessionId, localDate);

            if (coordinates.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 날짜의 러닝 기록이 없습니다.");
            }

            // 필요한 데이터만 추출하여 DTO로 변환
            List<CoordinateResponseDTO> response = coordinates.stream()
                    .map(coordinate -> new CoordinateResponseDTO(coordinate.getX(), coordinate.getY()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("러닝 데이터 조회를 실패했습니다.");
        }
    }
}