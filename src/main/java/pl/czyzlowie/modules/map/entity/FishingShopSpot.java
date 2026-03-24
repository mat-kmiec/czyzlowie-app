package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FISHING_SHOP")
public class FishingShopSpot extends MapSpot {
}