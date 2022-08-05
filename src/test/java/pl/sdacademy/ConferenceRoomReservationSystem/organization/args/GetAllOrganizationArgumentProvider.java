package pl.sdacademy.ConferenceRoomReservationSystem.organization.args;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import pl.sdacademy.ConferenceRoomReservationSystem.organization.Organization;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class GetAllOrganizationArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(
                                new Organization("Intive", "IT company"),
                                new Organization("Google", "IT company"),
                                new Organization("Uber", "Delivery company")
                        ),
                        Arrays.asList(
                                new Organization(1L, "Intive", "IT company"),
                                new Organization(2L, "Google", "IT company"),
                                new Organization(3L, "Uber", "Delivery company")
                        )
                ),
                Arguments.of(Collections.emptyList(), Collections.emptyList())
        );
    }
}
