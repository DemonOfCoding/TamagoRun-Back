package login_test.demo.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Coordinate {
    private double x;
    private double y;
    private Timestamp createdDate;
}
