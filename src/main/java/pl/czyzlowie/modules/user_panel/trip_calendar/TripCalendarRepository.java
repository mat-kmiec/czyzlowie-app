package pl.czyzlowie.modules.user_panel.trip_calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for TripCalendar entities.
 */
@Repository
public interface TripCalendarRepository extends JpaRepository<TripCalendar, Long> {

    List<TripCalendar> findByUserIdOrderByStartDateDesc(Long userId);

    @Query("SELECT t FROM TripCalendar t WHERE t.user.id = :userId AND t.startDate >= :start AND t.startDate <= :end")
    List<TripCalendar> findTripsInDateRange(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
