package org.learning.sprinbootapitrest.persons.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorApi {
    @NonNull
    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @Builder.Default
    private LocalDateTime dateTime = LocalDateTime.now();
    @NonNull
    private String message;

    public ErrorApi(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
