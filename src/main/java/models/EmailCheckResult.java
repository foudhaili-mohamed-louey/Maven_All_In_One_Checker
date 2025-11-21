package models;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class EmailCheckResult {
    private String email;
    private Platform platform;
    private boolean registered;
    private String responseMessage;
    private LocalDateTime checkedAt;
    private String errorDetails;
}
