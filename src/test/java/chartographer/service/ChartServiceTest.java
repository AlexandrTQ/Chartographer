package chartographer.service;

import chartographer.enitys.Chartographer;
import chartographer.enitys.Fragment;
import chartographer.repository.ChartRepo;
import chartographer.repository.FragmentRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.write;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ChartServiceTest {
    @ContextConfiguration(classes = {ChartRepo.class, FragmentRepo.class, ChartRepo.class})
    static class testConfig {
        @MockBean
        private Validator validator;
    }
    @Autowired
    ChartService chartService;
    @Autowired
    FragmentRepo fragmentRepo;
    @Autowired
    ChartRepo chartRepo;

    @Test
    void createNewChart() {
        ReflectionTestUtils.setField(chartService, "threads", 4);
        ReflectionTestUtils.setField(chartService, "directory", "src/test/resources/chart");
        long expectedId =  chartService.createNewChart(1000, 11000);
        File directory = new File(chartService.getDirectory() + "1");
        boolean excepted = directory.exists();
        boolean excepted1 = new File(chartService.getDirectory() + "1/fragment0.bmp").exists();
        boolean excepted2 = new File(chartService.getDirectory() + "1/fragment1.bmp").exists();
        boolean excepted3 = new File(chartService.getDirectory() + "1/fragment2.bmp").exists();
        boolean excepted4 = new File(chartService.getDirectory() + "1/fragment3.bmp").exists();
        boolean excepted5 = chartRepo.findById(1L).isPresent();
        boolean excepted6 = chartRepo.findById(2L).isPresent();
        assertTrue(excepted);
        assertTrue(excepted1);
        assertTrue(excepted2);
        assertTrue(excepted3);
        assertFalse(excepted4);
        assertTrue(excepted5);
        assertFalse(excepted6);
        FileSystemUtils.deleteRecursively(directory);
    }

    @Test
    void addFragment() throws IOException {
        Chartographer chartographer = new Chartographer(1000, 6000);
        chartographer.setDirectory("src/test/resources/addtest/char1");
        Fragment fragment1 = new Fragment(0, chartographer);
        Fragment fragment2 = new Fragment(1, chartographer);
        chartRepo.save(chartographer);
        fragmentRepo.saveAll(List.of(fragment1, fragment2));
        BufferedImage bufferedImage1 = new BufferedImage(1000, 5000, BufferedImage.TYPE_INT_RGB);
        BufferedImage bufferedImage2 = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        BufferedImage inputImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        Color inputColor = new Color(0, 200, 200);
        for (int i = 0; i < inputImage.getHeight(); i++) {
            for (int i1 = 0; i1 < inputImage.getWidth(); i1++) {
                inputImage.setRGB(i1, i, inputColor.getRGB());
            }
        }
        new File(chartographer.getDirectory()).mkdirs();
        File file1 = new File(fragment1.getFilePath());
        File file2 = new File(fragment2.getFilePath());
        ImageIO.write(bufferedImage1, "bmp", file1);
        write(bufferedImage2, "bmp", file2);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        write(inputImage, "bmp", byteArrayOutputStream);
        MultipartFile multipartFile = new MockMultipartFile("1", byteArrayOutputStream.toByteArray());
        chartService.addFragment(chartographer.getCharId(), 750, 4900, 300, 300, multipartFile);
        chartService.addFragment(chartographer.getCharId(), 750, 5800, 300, 300, multipartFile);
        BufferedImage fragment1After = ImageIO.read(file1);
        BufferedImage fragment2After = ImageIO.read(file2);
        assertEquals(inputColor.getRGB(), fragment1After.getRGB(750, 4900));
        assertEquals(inputColor.getRGB(), fragment1After.getRGB(999, 4900));
        assertNotEquals(inputColor.getRGB(), fragment1After.getRGB(32, 4900));
        assertEquals(inputColor.getRGB(), fragment2After.getRGB(999, 199));
        assertEquals(inputColor.getRGB(), fragment2After.getRGB(899, 999));
        assertEquals(inputColor.getRGB(), fragment2After.getRGB(750, 800));
        assertNotEquals(inputColor.getRGB(), fragment2After.getRGB(750, 500));
        FileSystemUtils.deleteRecursively(new File(chartographer.getDirectory()));
    }

    @Test
    @Transactional
    void getFragment() throws IOException {
        Color color1 = new Color(189, 11, 208);
        Color color2 = new Color(255, 255, 131);
        Chartographer chartographer = new Chartographer(2000, 7000);
        chartographer.setDirectory("src/test/resources/getfragmenttest");
        Fragment fragment1 = new Fragment(0, chartographer);
        Fragment fragment2 = new Fragment(1, chartographer);
        chartRepo.save(chartographer);
        fragmentRepo.saveAll(List.of(fragment1, fragment2));
        byte[] output = chartService.getFragment(chartographer.getCharId(), 500, 4000, 5000, 5000);
        BufferedImage result = read(new ByteArrayInputStream(output));
        assertEquals(1500, result.getWidth());
        assertEquals(3000, result.getHeight());
        assertEquals(color1.getRGB(), result.getRGB(1000, 999));
        assertEquals(color2.getRGB(), result.getRGB(1000, 1000));
    }
}