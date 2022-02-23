package chartographer.exceptions;

import chartographer.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class CustomExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler({IllegalSIzeException.class, CoordinatesOutOfSizeException.class,
            ProblemsWithInputFileException.class, CantCreateFileException.class})
    public ResponseEntity<ExceptionDto> illegalArgumentHandle(Exception exception) {
        return new ResponseEntity<>
                (new ExceptionDto(exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ChartDoesNotExistException.class, FragmentWasLostException.class, EntityNotFoundException.class})
    public ResponseEntity<ExceptionDto> NotFoundHandle(Exception exception) {
        return new ResponseEntity<>
                (new ExceptionDto(exception.getLocalizedMessage()), HttpStatus.NOT_FOUND);
    }
}