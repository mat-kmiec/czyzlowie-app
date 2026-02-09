package pl.czyzlowie.modules.forecast.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather_import_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherImportLog {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String importType;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Integer recordsCount;
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
