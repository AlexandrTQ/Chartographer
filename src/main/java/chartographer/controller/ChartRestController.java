package chartographer.controller;

import chartographer.dto.SuccessDto;
import chartographer.service.ChartService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@RestController
@RequestMapping("/chartas")
public class ChartRestController {
    private final ChartService chartService;

    @PostMapping
    public ResponseEntity<Long> createEmptyChart(@RequestParam("width") int width, @RequestParam("height") int height ) {
        return new ResponseEntity<> (chartService.createNewChart(width, height), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/")
    public ResponseEntity<SuccessDto> addFragment (@PathVariable long id, @RequestParam int x,
                                                   @RequestParam int y, @RequestParam int width,
                                                   @RequestParam int height, @RequestBody MultipartFile image) {
        chartService.addFragment(id, x, y, width, height, image);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @GetMapping( value ="/{id}/", produces = "image/bmp")
    public ResponseEntity<byte[]> getFragment (@PathVariable long id, @RequestParam int x,
                                                   @RequestParam int y, @RequestParam int width,
                                                   @RequestParam int height) {
        return new ResponseEntity<>(chartService.getFragment(id, x, y, width, height), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/")
    public ResponseEntity<SuccessDto> deleteChart (@PathVariable long id) {
        chartService.delete(id);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }
}
