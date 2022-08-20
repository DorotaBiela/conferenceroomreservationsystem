package pl.sdacademy.ConferenceRoomReservationSystem.reservation.args;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class FindReservationByDateArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(
                        LocalDateTime.of(2022, 8, 20, 10, 30, 0),
                        LocalDateTime.of(2022, 8, 20, 11, 0, 0),
                        true
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 20, 9, 30, 0),
                        LocalDateTime.of(2022, 8, 20, 12, 0, 0),
                        true
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 20, 9, 30, 0),
                        LocalDateTime.of(2022, 8, 20, 10, 30, 0),
                        true
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 20, 10, 30, 0),
                        LocalDateTime.of(2022, 8, 20, 12, 0, 0),
                        true
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 20, 9, 30, 0),
                        LocalDateTime.of(2022, 8, 20, 10, 0, 0),
                        false
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 20, 11, 0, 0),
                        LocalDateTime.of(2022, 8, 20, 12, 0, 0),
                        false
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 20, 10, 0, 0),
                        LocalDateTime.of(2022, 8, 20, 11, 0, 0),
                        true
                )
        );
    }
}

//                10:00_______________11:00 (istnieje w systemie)
//                        10:30_______11:00 (10:00 < 11:00 && 11:00 >10:30) -> true
//     09:30_______________________________________12:00 (10:00<12:00 && 11:00 > 09:30) -> true
//     09:30______________10:30 (10:00 < 10:30 && 11:00 > 09:30) -> true
//                        10:30____________________12:00 (10:00 < 12:00 && 11:00 > 10:30) -> true
//     09:30______10:00 (10:00 < 10:00 && 11:00 > 09:30) -> false
//                                    11:00________12:00 (10:00 < 12:00 && 11:00 > 11:00) ->false
//               10:00_______________11:00 (10:00 < 11:00 && 11:00 > 10:00) -> true