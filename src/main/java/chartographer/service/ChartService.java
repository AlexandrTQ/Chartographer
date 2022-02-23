package chartographer.service;

import chartographer.enitys.Chartographer;
import chartographer.enitys.Fragment;
import chartographer.exceptions.*;
import chartographer.repository.ChartRepo;
import chartographer.repository.FragmentRepo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static chartographer.service.AuxiliaryLogic.*;

@Slf4j
@Data
@Service
public class ChartService {

    private final FragmentRepo fragmentRepo;
    private final ChartRepo chartRepo;
    private final Validator validator;

    @Value("${threads}")
    private Integer threads;

    @Value("${directory}")
    private String directory;

    @Transactional
    public void delete (long id) {
        Chartographer chartographer = chartRepo.getById(id);
        FileSystemUtils.deleteRecursively(new File(chartographer.getDirectory()));
        fragmentRepo.deleteByChartographer(chartographer);
        chartRepo.delete(chartographer);
        log.info("Delete with id " + id);
    }

    @Transactional
    public long createNewChart (int width, int height) {
        if (validator.incorrectSize(width, height)) {
            throw new IllegalSIzeException("Incorrect size");
        }
        log.info("create with size " + width + "x" + height);
        Chartographer chartographer = createChartographer(width, height);
        ExecutorService service = Executors.newFixedThreadPool(threads);
        List<Callable<Void>> callables = addTasks(width, height, chartographer);
        List<Future<Void>> futures = invokeExecutor(service, callables);
        waitThreads(futures, chartographer);
        return chartographer.getCharId();
    }

    private Chartographer createChartographer(int width, int height) {
        Chartographer chartographer = new Chartographer(width, height);
        chartRepo.save(chartographer);
        chartographer.setDirectory(directory + chartographer.getCharId());
        FileOperator.createDirectory(chartographer.getDirectory());
        return chartographer;
    }

    private List<Callable<Void>> addTasks(int width, int height, Chartographer chartographer) {
        List<Callable<Void>> callables = new ArrayList<>();
        int fragmentMaxHeight = 5000;
        for (int number = 0; height > 0; height -= fragmentMaxHeight, number++) {
            int finalHeight = height;
            Fragment currentFragment = new Fragment(number, chartographer);
            fragmentRepo.save(currentFragment);
            callables.add( () -> {
                int h = finalHeight >= fragmentMaxHeight ? fragmentMaxHeight : finalHeight % fragmentMaxHeight;
                BufferedImage fragment = new BufferedImage(width, h, BufferedImage.TYPE_INT_RGB);
                if (new File(chartographer.getDirectory()).canWrite()) {
                    FileOperator.createFile(fragment, currentFragment.getFilePath());
                    if(new File(currentFragment.getFilePath()).exists()) {
                        return null;
                    }
                } throw new CantCreateFileException("Failed to create file. The directory may not exist/is not mutable line");
            } );
        }
        return callables;
    }

    private List<Future<Void>> invokeExecutor(ExecutorService service, List<Callable<Void>> callables) {
        try {
            return service.invokeAll(callables);
        } catch (InterruptedException e) {
            throw new ProblemsWithInputFileException("");
        }
    }

    private void waitThreads(List<Future<Void>> futures, Chartographer chartographer) {
        futures.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                if (new File(chartographer.getDirectory()).exists()) {
                    FileSystemUtils.deleteRecursively(new File(chartographer.getDirectory()));
                }
                throw new CantCreateFileException("Failed to create file. The directory may not exist/is not mutable");
            }
        });
    }

    @Transactional
    public void addFragment(long id, int x, int y, int width, int height, MultipartFile image) {
        Chartographer chartographer = chartRepo.getById(id);
        if (validator.incorrectCoordinates(x, y, chartographer)) {
            throw new CoordinatesOutOfSizeException("coordinates outside the chartographer size");
        }
        log.info("add on coordinates x" + x + "and y " + y);
        BufferedImage inputImage = createImageFromMultFile(image, width, height);
        width = cutSize(width, x, chartographer.getWidth());
        height = cutSize(height, y, chartographer.getHeight());
        processImage(x, y, width, height, inputImage, true, chartographer);
    }

    private BufferedImage createImageFromMultFile(MultipartFile image, int width, int height) {
        BufferedImage inputImage;
        try {
            inputImage = ImageIO.read(image.getInputStream());
            if (validator.incorrectImageSize(inputImage, width, height)) {
                throw new IOException();
            }
            return inputImage;
        } catch (IOException ex) {
            throw new ProblemsWithInputFileException
                    ("uploaded file not found / wrong format / resolution does not match the declared");
        }
    }

    public byte[] getFragment (long id, int x, int y, int width, int height) {
        Chartographer chartographer = chartRepo.getById(id);
        if (validator.incorrectFragmentSize(width, height)) {
            throw new IllegalSIzeException("incorrect size");
        }
        if (validator.incorrectCoordinates(x, y, chartographer)) {
            throw new CoordinatesOutOfSizeException("coordinates outside the chartographer size");
        }
        log.info("get by coordinates x " + x + "and y " + y);
        width = cutSize(width, x, chartographer.getWidth());
        height = cutSize(height, y, chartographer.getHeight());
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        processImage(x, y, width, height, output, false, chartographer);
        return getByteArrayFromImage(output);
    }

    private byte[] getByteArrayFromImage(BufferedImage output) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(output, "bmp", stream);
        } catch (IOException e) {
            throw new FragmentWasLostException("=)");
        }
        return stream.toByteArray();
    }

    public void processImage (int x, int y, int width, int height, BufferedImage image,
                              boolean post, Chartographer chartographer) {
        int upBoarder = 0;
        long fragments = countMutableFragments(height, y);
        for (int count = 0; count < fragments; count++) {

            Fragment fragment = fragmentRepo.findByChartographerAndNumber(chartographer, getFragmentNumber(y));
            BufferedImage fragmentImage;
            try {
                fragmentImage= ImageIO.read(new File(fragment.getFilePath()));
            } catch (IOException ex) {
                throw new FragmentWasLostException ("=)");
            }
            int localY = getLocalY(y, fragment);
            int localHeight = getLocalH(fragmentImage.getHeight(), localY, height, upBoarder);
            for (int i = 0; i < width; i++) {
                for (int i1 = 0; i1 < localHeight; i1++) {
                    if (post) {
                        fragmentImage.setRGB(x + i, localY + i1, image.getRGB(i, i1 + upBoarder));
                    } else {
                        image.setRGB(i, i1 + upBoarder, fragmentImage.getRGB(x + i, localY + i1));
                    }
                }
            }
            if (post) {
                FileOperator.createFile(fragmentImage, fragment.getFilePath());
            }
            upBoarder += localHeight;
            y += localHeight;
        }
    }
}