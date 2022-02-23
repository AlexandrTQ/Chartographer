package chartographer.enitys;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity (name = "chartographer")
public class Chartographer {

    @Column(name = "chartographer_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long charId;

    @Column(name = "directory_path")
    private String directory;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    public Chartographer(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
