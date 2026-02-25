package pl.czyzlowie.modules.imgw_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw_api.entity.ImgwImportLog;

@Repository
public interface ImgwImportLogRepository extends JpaRepository<ImgwImportLog, Long> {
}
