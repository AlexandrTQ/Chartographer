package chartographer.service;

import chartographer.enitys.Chartographer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class Validator {
    @Value("${size.chartographer.width}")
    private int maxWidth;
    @Value("${size.chartographer.height}")
    private int maxHeight;
    @Value("${size.fragment.width}")
    private int maxFragmentWidth;
    @Value("${size.fragment.height}")
    private int maxFragmentHeight;

    public boolean incorrectSize (int width, int height) {
        return !((width > 0 && width <= maxWidth) && (height > 0 && height <= maxHeight));
    }
    public boolean incorrectFragmentSize (int width, int height) {
        return !((width > 0 && width <= maxFragmentWidth) && (height > 0 && height <= maxFragmentHeight));
    }
    public boolean incorrectCoordinates (int x, int y, Chartographer chartographer) {
        return (x >= chartographer.getWidth() || y >= chartographer.getHeight()) || x < 0 || y < 0;
    }
    public boolean incorrectImageSize (BufferedImage image, int width, int height) {
        return (image.getWidth() != width | image.getHeight() != height);
    }
}