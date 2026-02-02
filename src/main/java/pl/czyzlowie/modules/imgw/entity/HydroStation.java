package pl.czyzlowie.modules.imgw.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "imgw_hydro_stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HydroStation {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String river;

    @Column(length = 100, nullable = false)
    private String province;

    @Column(nullable = false, precision = 10, scale = 8)
    private double latitude;

    @Column(nullable = false, precision = 10, scale = 8)
    private double longitude;

    @Column(name = "is_active")
    private boolean active;
}
