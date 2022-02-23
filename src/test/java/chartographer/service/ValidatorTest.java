package chartographer.service;

import chartographer.enitys.Chartographer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {
    Validator validator = new Validator();
     {
        ReflectionTestUtils.setField(validator, "maxWidth", 20000);
        ReflectionTestUtils.setField(validator, "maxHeight", 50000);
        ReflectionTestUtils.setField(validator, "maxFragmentWidth", 5000);
        ReflectionTestUtils.setField(validator, "maxFragmentHeight", 5000);
    }

    @ParameterizedTest
    @MethodSource("incorrectSizeCases")
    void incorrectSize(int w, int h, boolean expected) {
        boolean actual = validator.incorrectSize(w, h);
        assertEquals(expected, actual, "Error with w = " + w +", h = " + h);
    }

    @ParameterizedTest
    @MethodSource("incorrectFragmentSizeCases")
    void incorrectFragmentSize(int w, int h, boolean expected) {
        boolean actual = validator.incorrectFragmentSize(w, h);
        assertEquals(expected, actual, "Error with w = " + w +", h = " + h);
    }

    @ParameterizedTest
    @MethodSource("incorrectCoordinatesCases")
    void incorrectCoordinates(int x, int y, boolean expected) {
        Chartographer chartographer = new Chartographer(10000, 10000);
        boolean actual = validator.incorrectCoordinates(x, y, chartographer);
        assertEquals(expected, actual, "Error with x = " + x +", y = " + y);
    }

    @Test
    void incorrectImageSize() {
        BufferedImage image = new BufferedImage(123, 51, 1);
        boolean actual = validator.incorrectImageSize(image, 100, 100);
        assertTrue(actual);
        image = new BufferedImage(100, 100, 1);
        actual = validator.incorrectImageSize(image, 100, 100);
        assertFalse(actual);
    }

    static Stream<Arguments> incorrectSizeCases() {
        return Stream.of(
                Arguments.of(-15, 500, true),
                Arguments.of(213, -1, true),
                Arguments.of(-5131, -1, true),
                Arguments.of(0, 0, true),
                Arguments.of(0, 500, true),
                Arguments.of(511, 0, true),
                Arguments.of(20001, 500, true),
                Arguments.of(521, 50001, true),
                Arguments.of(20000, 50000, false),
                Arguments.of(613, 7234, false),
                Arguments.of(1, 1, false)
        );
    }

    static Stream<Arguments> incorrectFragmentSizeCases() {
        return Stream.of(
                Arguments.of(-15, 500, true),
                Arguments.of(213, -1, true),
                Arguments.of(-41, -512, true),
                Arguments.of(0, 0, true),
                Arguments.of(0, 500, true),
                Arguments.of(511, 0, true),
                Arguments.of(5001, 500, true),
                Arguments.of(521, 5001, true),
                Arguments.of(5000, 5000, false),
                Arguments.of(4123, 3141, false),
                Arguments.of(1, 1, false)
        );
    }

    static Stream<Arguments> incorrectCoordinatesCases() {
        return Stream.of(
                Arguments.of(-15, 500, true),
                Arguments.of(213, -1, true),
                Arguments.of(-41, -512, true),
                Arguments.of(10000, 500, true),
                Arguments.of(521, 10000, true),
                Arguments.of(0, 0, false),
                Arguments.of(0, 500, false),
                Arguments.of(511, 0, false),
                Arguments.of(9999, 9999, false),
                Arguments.of(4123, 3141, false),
                Arguments.of(1, 1, false)
        );
    }
}