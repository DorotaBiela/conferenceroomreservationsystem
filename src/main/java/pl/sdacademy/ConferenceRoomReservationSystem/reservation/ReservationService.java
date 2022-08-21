package pl.sdacademy.ConferenceRoomReservationSystem.reservation;

import org.springframework.stereotype.Service;
import pl.sdacademy.ConferenceRoomReservationSystem.conferenceRoom.ConferenceRoom;
import pl.sdacademy.ConferenceRoomReservationSystem.conferenceRoom.ConferenceRoomRepository;

import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

@Service
class ReservationService {

    private static final int MIN_DURATION_OF_THE_MEETING = 15;

    private final ReservationRepository reservationRepository;
    private final ConferenceRoomRepository conferenceRoomRepository;
    private final ReservationTransformer reservationTransformer;

    ReservationService(ReservationRepository reservationRepository,
                       ConferenceRoomRepository conferenceRoomRepository,
                       ReservationTransformer reservationTransformer) {
        this.reservationRepository = reservationRepository;
        this.conferenceRoomRepository = conferenceRoomRepository;
        this.reservationTransformer = reservationTransformer;
    }

    ReservationDto addReservation(ReservationDto reservationDto) {
        Reservation reservation = reservationTransformer.fromDto(reservationDto);
        ConferenceRoom conferenceRoom = conferenceRoomRepository.findById(reservation.getConferenceRoom().getId())
                .orElseThrow(() -> new NoSuchElementException("Can't find conference room!"));
        reservation.setConferenceRoom(conferenceRoom);
        if (!conferenceRoom.getAvailable()) {
            throw new IllegalArgumentException("Conference room is not available!");
        }
        if (reservation.getEndDate().isBefore(reservation.getStartDate())){
            throw new IllegalArgumentException("end date is before start date!");
        }
        if (ChronoUnit.MINUTES.between(reservation.getStartDate(), reservation.getEndDate()) <MIN_DURATION_OF_THE_MEETING){
            throw new IllegalArgumentException("meeting can't be shorter than " + MIN_DURATION_OF_THE_MEETING + " min!");
        }
        reservationRepository.findByConferenceRoom_IdAndStartDateLessThanAndEndDateGreaterThan(
                conferenceRoom.getId(),
                reservation.getEndDate(),
                reservation.getStartDate()
        ).ifPresent(r->{
            throw new IllegalArgumentException("Reservation during provided time already exits");
        });
        return reservationTransformer.toDto(reservationRepository.save(reservation));
    }
}