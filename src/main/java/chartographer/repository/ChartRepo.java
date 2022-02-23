package chartographer.repository;

import chartographer.enitys.Chartographer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartRepo extends JpaRepository<Chartographer, Long> {

}
