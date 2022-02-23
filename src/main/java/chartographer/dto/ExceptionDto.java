package chartographer.dto;

import lombok.Data;

@Data
public class ExceptionDto {
    private String exception;

    public ExceptionDto(String exception) {
        this.exception = exception;
    }
}