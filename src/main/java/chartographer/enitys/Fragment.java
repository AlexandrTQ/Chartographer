package chartographer.enitys;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "fragment")
public class Fragment {

    @Column(name = "fragment_id")
    @Id
    @GeneratedValue  (strategy= GenerationType.IDENTITY)
    private Long fragmentId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "number")
    private int number;

    @ManyToOne(targetEntity = Chartographer.class)
    @JoinColumn(name = "chartographer_id", foreignKey = @ForeignKey(name = "fk_fragments"))
    private Chartographer chartographer;

    public Fragment(int number, Chartographer chartographer) {
        this.number = number;
        this.chartographer = chartographer;
        this.filePath = chartographer.getDirectory() + "/fragment" + number + ".bmp";
    }
}
