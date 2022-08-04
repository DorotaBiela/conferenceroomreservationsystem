package pl.sdacademy.ConferenceRoomReservationSystem.organization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.sdacademy.ConferenceRoomReservationSystem.SortType;
import pl.sdacademy.ConferenceRoomReservationSystem.organization.args.SortOrganizationArgumentProvider;
import pl.sdacademy.ConferenceRoomReservationSystem.organization.args.UpdateOrganizationArgumentProvider;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class OrganizationServiceTest {

    @MockBean
    OrganizationRepository organizationRepository;
    @Autowired
    OrganizationService organizationService;

    @Test
    void when_get_all_with_asc_order_then_asc_order_info_should_be_provided_to_repo() {
        //given
        SortType sortType = SortType.ASC;
        ArgumentCaptor<Sort> sortArgumentCaptor = ArgumentCaptor.forClass(Sort.class);

        //when
        organizationService.getAllOrganizations(sortType);

        //then
        Mockito.verify(organizationRepository).findAll(sortArgumentCaptor.capture());
        assertEquals(Sort.by(Sort.Direction.ASC, "name"), sortArgumentCaptor.getValue());

    }

    @ParameterizedTest
    @ArgumentsSource(SortOrganizationArgumentProvider.class)
    void when_get_all_with_arg_0_order_then_arg_1_order_info_should_be_provided_to_repo(SortType arg0, Sort arg1) {
        //given
        ArgumentCaptor<Sort> sortArgumentCaptor = ArgumentCaptor.forClass(Sort.class);

        //when
        organizationService.getAllOrganizations(arg0);

        //then

        Mockito.verify(organizationRepository).findAll(sortArgumentCaptor.capture());
        assertEquals(arg1, sortArgumentCaptor.getValue());
    }

    @Test
    void when_add_invalid_organization_the_exception_should_be_thrown() {
        //given
        String name = "Intive";
        Organization arg = new Organization(name, "It company");
        Mockito.when(organizationRepository.findById(name)).thenReturn(Optional.of(arg));

        //when

        //then
        assertThrows(IllegalArgumentException.class, () -> {
            organizationService.addOrganization(arg);
        });
    }

    @Test
    void when_add_new_organization_then_it_should_be_added_to_repo() {
        //given
        String name = "Intive";
        Organization arg = new Organization(name, "It company");
        Mockito.when(organizationRepository.findById(name)).thenReturn(Optional.empty());
        Mockito.when(organizationRepository.save(arg)).thenReturn(arg);

        //when
        Organization result = organizationService.addOrganization(arg);

        //then
        assertEquals(arg, result);
        Mockito.verify(organizationRepository).save(arg);
    }

    @Test
    void when_delete_existing_organization_then_it_should_be_removed_from_repo() {
        //given
        String name = "Intive";
        Organization arg = new Organization(name, "It company");
        Mockito.when(organizationRepository.findById(name)).thenReturn(Optional.of(arg));

        //when
        Organization result = organizationService.deleteOrganization(name);

        //then
        assertEquals(arg, result);
        Mockito.verify(organizationRepository).deleteById(name);
    }

    @Test
    void when_delete_non_existing_organization_then_exception_should_be_thrown() {
        //given
        String name = "Intive";
        Mockito.when(organizationRepository.findById(name)).thenReturn(Optional.empty());

        //when

        //then
        assertThrows(NoSuchElementException.class, () -> {
            organizationService.deleteOrganization(name);
        });
    }

    @Test
    void when_update_non_existing_organization_then_exception_should_be_thrown() {
        //given
        String name = "Intive";
        Organization arg = new Organization(name, "It company");
        Mockito.when(organizationRepository.findById(name)).thenReturn(Optional.empty());

        //when

        //then
        assertThrows(NoSuchElementException.class, () -> {
            organizationService.updateOrganization(name, arg);
        });
    }

    @Test
    void when_update_description_of_existing_organization_then_organization_should_be_updated() {
        //given
        String name = "Intive";
        Organization orgToUpdate = new Organization(name, "It company");
        Organization arg = new Organization(name, "It company");
        Organization expectedOrg = new Organization(name, "Delivery company");
        Mockito.when(organizationRepository.findById(name)).thenReturn(Optional.of(arg));
        Mockito.when(organizationRepository.save(arg)).thenReturn(expectedOrg);

        //when
        Organization result = organizationService.updateOrganization(name, orgToUpdate);

        //then
        assertEquals(expectedOrg, result);
        Mockito.verify(organizationRepository).save(arg);
    }

    @ParameterizedTest
    @ArgumentsSource(UpdateOrganizationArgumentProvider.class)
    void when_update_existing_organization_with_provided_data_then_organization_should_be_updated_to_expected(
            String name,
            Organization existing,
            Organization provided,
            Organization expected
    ) {
        //given
        Mockito.when(organizationRepository.findById(name)).thenReturn(Optional.of(existing));
        Mockito.when(organizationRepository.save(existing)).thenReturn(expected);

        //when
        Organization result = organizationService.updateOrganization(name, provided);

        //then
        assertEquals(expected, result);
        Mockito.verify(organizationRepository).save(expected);
    }

    @Test
    void when_get_non_existing_organization_then_exception_should_be_thrown() {
        //given
        String name = "Intive";
        Mockito.when(organizationRepository.findById(name)).thenReturn(Optional.empty());

        //when

        //then
        assertThrows(NoSuchElementException.class, () -> {
            organizationService.getOrganization(name);
        });
    }

    @Test
    void when_get_existing_organization_then_organization_should_be_returned() {
        //given
        String name = "Intive";
        Organization arg = new Organization(name, "It company");
        Mockito.when(organizationRepository.findById(name)).thenReturn(Optional.of(arg));

        //when
        Organization result = organizationService.getOrganization(name);

        //then
        assertEquals(arg, result);
        Mockito.verify(organizationRepository).findById(name);
    }

    @TestConfiguration
    static class OrganizationServiceTestConfig {
        @Bean
        OrganizationService organizationService(OrganizationRepository organizationRepository) {
            return new OrganizationService(organizationRepository);
        }
    }
}