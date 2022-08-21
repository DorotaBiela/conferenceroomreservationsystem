package pl.sdacademy.ConferenceRoomReservationSystem.reservation;

import org.springframework.stereotype.Service;
import pl.sdacademy.ConferenceRoomReservationSystem.conferenceRoom.ConferenceRoom;
import pl.sdacademy.ConferenceRoomReservationSystem.conferenceRoom.ConferenceRoomRepository;

import java.time.LocalDateTime;
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
        validateConferenceRoom(conferenceRoom);
        validateReservationDuration(reservation);
        validateReservationTime(conferenceRoom, reservation);
        return reservationTransformer.toDto(reservationRepository.save(reservation));
    }

    private void validateReservationTime(ConferenceRoom conferenceRoom, Reservation reservation) {
        reservationRepository.findByConferenceRoom_IdAndStartDateLessThanAndEndDateGreaterThan(
                conferenceRoom.getId(),
                reservation.getEndDate(),
                reservation.getStartDate()
        ).ifPresent(r->{
            throw new IllegalArgumentException("Reservation during provided time already exits");
        });
    }

    private void validateReservationDuration(Reservation reservation) {
        if (reservation.getEndDate().isBefore(reservation.getStartDate())){
            throw new IllegalArgumentException("end date is before start date!");
        }
        if (ChronoUnit.MINUTES.between(reservation.getStartDate(), reservation.getEndDate()) <MIN_DURATION_OF_THE_MEETING){
            throw new IllegalArgumentException("meeting can't be shorter than " + MIN_DURATION_OF_THE_MEETING + " min!");
        }
    }

    private void validateConferenceRoom(ConferenceRoom conferenceRoom) {
        if (!conferenceRoom.getAvailable()) {
            throw new IllegalArgumentException("Conference room is not available!");
        }
    }

    ReservationDto updateReservation(String id, ReservationDto reservationDto) {
        Reservation reservation = reservationTransformer.fromDto(reservationDto);
        Reservation reservationFromDb = reservationRepository.getReferenceById(id);
        updateReservationName(reservation, reservationFromDb);
        updateReservationConferenceRoom(reservation, reservationFromDb);
        updateReservationStartDateAndEndData(reservation, reservationFromDb);
        return reservationTransformer.toDto(reservationRepository.save(reservationFromDb));
    }

    private void updateReservationStartDateAndEndData(Reservation reservation, Reservation reservationFromDb) {
        LocalDateTime startDate = reservation.getStartDate();
        LocalDateTime endDate = reservation.getEndDate();
        validateReservationDuration(reservation);
        boolean isChange = false;
        if (startDate != null) {
            isChange = true;
            reservationFromDb.setStartDate(startDate);
        }
        if (endDate != null) {
            isChange = true;
            reservationFromDb.setEndDate(endDate);
        }

        //TODO: naprawa błędu aktualizacji nachodzącej
        //aktualna rezerwajca: 10 - 11
        //update: 10-10:30
        if (isChange) {
            validateReservationTime(reservationFromDb.getConferenceRoom(), reservationFromDb);
        }
    }

    private void updateReservationName(Reservation reservation, Reservation reservationFromDb) {
        String newReservationName = reservation.getReservationName();
        if (reservation.getReservationName() != null) {
            reservationFromDb.setReservationName(newReservationName);
        }
    }

    private void updateReservationConferenceRoom(Reservation reservation, Reservation reservationFromDb) {
        String conferenceRoomId = reservation.getConferenceRoom().getId();
        if (conferenceRoomId != null) {
            ConferenceRoom conferenceRoom = conferenceRoomRepository.findById(conferenceRoomId)
                    .orElseThrow(() -> {
                        throw new NoSuchElementException("Can't find conference room!");
                    });
            validateConferenceRoom(conferenceRoom);
            reservationFromDb.setConferenceRoom(conferenceRoom);
        }
    }
}