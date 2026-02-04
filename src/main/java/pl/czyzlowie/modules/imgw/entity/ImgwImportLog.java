package pl.czyzlowie.modules.imgw.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.czyzlowie.modules.imgw.entity.enums.ImgwImportType;

import java.time.LocalDateTime;

@Entity
@Table(name = "imgw_import_logs")
@Getter
@Setter
@NoArgsConstructor
public class ImgwImportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "import_type", nullable = false)
    private ImgwImportType importType;

    @Column(name = "records_count", nullable = false)
    private Integer recordsCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    public ImgwImportLog(ImgwImportType importType, Integer recordsCount) {
        this.importType = importType;
        this.recordsCount = recordsCount;
    }


}
