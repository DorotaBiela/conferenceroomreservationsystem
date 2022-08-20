package pl.sdacademy.ConferenceRoomReservationSystem.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    Optional<Reservation> findByConferenceRoom_IdAndStartDateLessThanAndEndDateGreaterThan(
            String conferenceRoomId,
            LocalDateTime endDate,
            LocalDateTime startDate
    );
}


//                10:00_______________11:00 (istnieje w systemie)
//                        10:30_______11:00 (10:00 < 11:00 && 11:00 >10:30) -> true
//     09:30_______________________________________12:00 (10:00<12:00 && 11:00 > 09:30) -> true
//     09:30______________10:30 (10:00 < 10:30 && 11:00 > 09:30) -> true
//                        10:30____________________12:00 (10:00 < 12:00 && 11:00 > 10:30) -> true
//     09:30______10:00 (10:00 < 10:00 && 11:00 > 09:30) -> false
//                                    11:00________12:00 (10:00 < 12:00 && 11:00 > 11:00) ->false
//               10:00_______________11:00 (10:00 < 11:00 && 11:00 > 10:00) -> true