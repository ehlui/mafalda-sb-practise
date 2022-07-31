package org.learning.sprinbootapitrest.persons.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiException {
    @NonNull
    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @Builder.Default
    private LocalDateTime dateTime = LocalDateTime.now();
    @NonNull
    private String message;
    private List<String> details;

    public ApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
