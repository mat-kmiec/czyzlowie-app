package pl.czyzlowie.modules.user_panel.trip_calendar;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing CRUD operations and custom queries
 * for the TripCalendar entity. Provides operations to perform database
 * interactions related to trip calendar entries, such as retrieving,
 * counting, and finding overlapping trips.
 *
 * Extends:
 * - JpaRepository<TripCalendar, Long>: Provides basic CRUD and pagination
 *   functionalities for the TripCalendar entity.
 *
 * Methods:
 * - findByUserIdOrderByStartDateDesc(Long userId):
 *     Retrieves a list of TripCalendar entities associated with a specific
 *     user, ordered by start date in descending order.
 *
 * - findFirstByUserIdAndEndDateAfterOrderByStartDateAsc(Long userId, LocalDateTime date):
 *     Retrieves the first upcoming TripCalendar for a user based on the
 *     start date in ascending order and where the end date is after the
 *     specified date.
 *
 * - findByUserIdAndEndDateAfterAndIdNotOrderByStartDateAsc(Long userId, LocalDateTime date, Long excludeId, Pageable pageable):
 *     Retrieves a paginated list of TripCalendar entities for a user where
 *     the end date is after a specified date and excludes a specific trip ID,
 *     ordered by start date in ascending order.
 *
 * - countByUserIdAndEndDateAfter(Long userId, LocalDateTime date):
 *     Counts the number of TripCalendar entries for a user with end dates
 *     after the specified date.
 *
 * - findOverlappingTrips(Long userId, LocalDateTime start, LocalDateTime end, Long excludeTripId):
 *     Retrieves a list of TripCalendar entities that overlap with a specific
 *     date range for a user. Allows exclusion of a specific trip ID from the results.
 *
 * Use this repository to perform database operations specific to the
 * TripCalendar entity.
 */
@Repository
public interface TripCalendarRepository extends JpaRepository<TripCalendar, Long> {

    List<TripCalendar> findByUserIdOrderByStartDateDesc(Long userId);
    TripCalendar findFirstByUserIdAndEndDateAfterOrderByStartDateAsc(Long userId, LocalDateTime date);
    Page<TripCalendar> findByUserIdAndEndDateAfterAndIdNotOrderByStartDateAsc(Long userId, LocalDateTime date, Long excludeId, Pageable pageable);
    long countByUserIdAndEndDateAfter(Long userId, LocalDateTime date);
    @Query("SELECT t FROM TripCalendar t WHERE t.user.id = :userId AND t.startDate <= :end AND t.endDate >= :start AND (:excludeTripId IS NULL OR t.id != :excludeTripId)")
    List<TripCalendar> findOverlappingTrips(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("excludeTripId") Long excludeTripId);
}