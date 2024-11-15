package login_test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinateDTO {
    private double x;
    private double y;
    private Timestamp createdDate;
    private Long user_id;
}