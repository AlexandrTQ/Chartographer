package chartographer.service;

import chartographer.enitys.Fragment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class AuxiliaryLogicTest {

    @ParameterizedTest
    @MethodSource("getFragmentNumberCases")
    void getFragmentNumber(int y, int expected) {
        int actual = AuxiliaryLogic.getFragmentNumber(y);
        assertEquals(expected, actual);
    }

    @Test
    void getLocalY() {
        Fragment fragment = new Fragment();
        fragment.setNumber(3);
        int actual = AuxiliaryLogic.getLocalY(16512, fragment);
        assertEquals(1512, actual);
    }

    @ParameterizedTest
    @MethodSource("getLocalHCases")
    void getLocalH(int imageHeight, int localY, int height, int upBoarder, int expected) {
        int actual = AuxiliaryLogic.getLocalH(imageHeight, localY, height, upBoarder);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("countMutableFragmentsCases")
    void countMutableFragments(int height, int y, int expected) {
        long actual = AuxiliaryLogic.countMutableFragments(height, y);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("cutSizeCases")
    void cutSize(int size, int coordinate, int chartographerSize, int expected) {
        long actual = AuxiliaryLogic.cutSize(size, coordinate, chartographerSize);
        assertEquals(expected, actual);
    }

    static Stream<Arguments> getFragmentNumberCases() {
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(251, 0),
                Arguments.of(4999, 0),
                Arguments.of(5000, 1),
                Arguments.of(50000, 10),
                Arguments.of(5999, 1),
                Arguments.of(9999, 1),
                Arguments.of(10000, 2),
                Arguments.of(10001, 2)

        );
    }

    static Stream<Arguments> getLocalHCases() {
        return Stream.of(
                Arguments.of(5000, 0, 1050, 0, 1050),
                Arguments.of(5000, 4000, 1050, 0, 1000),
                Arguments.of(5000, 4000, 999, 0, 999),
                Arguments.of(5000, 0, 6000, 0, 5000),
                Arguments.of(5000, 0, 6000, 700, 5000),
                Arguments.of(5000, 0, 6000, 1200, 4800),
                Arguments.of(421, 50, 1050, 100, 371)
        );
    }

    static Stream<Arguments> countMutableFragmentsCases() {
        return Stream.of(
                Arguments.of(500, 1500, 1),
                Arguments.of(1, 1500, 1),
                Arguments.of(1, 5000, 1),
                Arguments.of(1, 5001, 1),
                Arguments.of(5000, 0, 1),
                Arguments.of(5001, 0, 2),
                Arguments.of(5000, 1, 2),
                Arguments.of(321, 42000, 1),
                Arguments.of(42000, 321, 9)
        );
    }

    static Stream<Arguments> cutSizeCases() {
        return Stream.of(
                Arguments.of(1500, 1500, 3000, 1500),
                Arguments.of(1500, 1500, 2000, 500),
                Arguments.of(5000, 0, 2000, 2000),
                Arguments.of(5000, 2000, 2000, 0),
                Arguments.of(720, 500, 1000, 500)
        );
    }
}