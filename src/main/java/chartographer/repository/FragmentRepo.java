package chartographer.repository;

import chartographer.enitys.Chartographer;
import chartographer.enitys.Fragment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FragmentRepo extends JpaRepository<Fragment, Long> {

    Fragment findByChartographerAndNumber (Chartographer chartographer, int number);

    void deleteByChartographer(Chartographer chartographer);
}
