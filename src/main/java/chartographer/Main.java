package chartographer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            System.setProperty("dir", args[0] + "/chartographer");
        }
        SpringApplication.run(Main.class);
    }
}