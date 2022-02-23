package chartographer.service;

import chartographer.enitys.Fragment;
import java.util.ArrayList;
import java.util.List;

public class AuxiliaryLogic {

    public static int getFragmentNumber(int y) {
        return y / 5000;
    }

    public static int getLocalY(int y, Fragment fragment) {
        return y - 5000 * fragment.getNumber();
    }

    public static int getLocalH(int imageHeight, int localY, int height, int upBoarder) {
        return (imageHeight < localY + height - upBoarder ? imageHeight - localY : height - upBoarder);
    }

    public static long countMutableFragments(int height, int y) {
        int maxY = height -1 + y;
        List<Double> borders = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            borders.add(4999.5 + 5000 * i);
        }
        return borders.stream().filter(o -> o > y && o < maxY).count() + 1;
    }

    public static int cutSize(int size, int coordinate, int chartographerSize) {
        return chartographerSize < coordinate + size ? chartographerSize - coordinate : size;
    }
}
