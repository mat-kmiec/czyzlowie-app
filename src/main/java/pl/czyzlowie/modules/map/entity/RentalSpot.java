package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import pl.czyzlowie.modules.map.entity.MapSpot;

@Entity
@DiscriminatorValue("RENTALS")
public class RentalSpot extends MapSpot {
}