package chartographer.service;

import chartographer.exceptions.CantCreateFileException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;

public class FileOperator {

    public static void createFile (BufferedImage image, String path) {
        try {
            File file = new File(path);
                ImageIO.write(image, "bmp", file);
        } catch (IOException | InvalidPathException e) {
            throw new CantCreateFileException("Failed to create file. The directory may not exist/is not mutable");
        }
    }

    public static void createDirectory(String path) {
        new File(path).mkdirs();
    }
}
