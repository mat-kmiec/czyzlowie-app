package pl.czyzlowie.modules.imgw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;

import java.util.List;

@Repository
public interface ImgwSynopStationRepository extends JpaRepository<ImgwSynopStation, String> {
    List<ImgwSynopStation> findAllByIsActiveTrue();
}