package org.springframework.samples.petclinic.owner;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HospitalReservationController.class)
@DisabledInNativeImage
@DisabledInAotMode
class HospitalReservationControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	private static final int TEST_RESERVATION_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private HospitalReservationRepository reservations;

	@MockitoBean
	private PetRepository pets;

	@MockitoBean
	private VetRepository vets;

	private Pet testPet;

	private Vet testVet;

	private HospitalReservation testReservation;

	@BeforeEach
	void setup() {
		Owner owner = new Owner();
		owner.setId(TEST_OWNER_ID);
		owner.setFirstName("George");
		owner.setLastName("Franklin");

		testPet = new Pet();
		testPet.setId(TEST_PET_ID);
		testPet.setName("Leo");
		testPet.setOwner(owner);

		testVet = new Vet();
		testVet.setId(1);
		testVet.setFirstName("James");
		testVet.setLastName("Carter");

		testReservation = new HospitalReservation();
		testReservation.setId(TEST_RESERVATION_ID);
		testReservation.setPet(testPet);
		testReservation.setVet(testVet);
		testReservation.setReservationTime(LocalDateTime.of(2025, 3, 1, 10, 0));
		testReservation.setDescription("Annual checkup");

		given(this.pets.findById(TEST_PET_ID)).willReturn(Optional.of(testPet));
		given(this.vets.findAll()).willReturn(List.of(testVet));
		given(this.reservations.findById(TEST_RESERVATION_ID)).willReturn(Optional.of(testReservation));
		given(this.reservations.findAllByOrderByReservationTimeDesc()).willReturn(List.of(testReservation));
	}

	@Test
	void testShowReservationList() throws Exception {
		mockMvc.perform(get("/reservations/hospital"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("reservations"))
			.andExpect(view().name("reservations/hospitalReservationList"));
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/reservations/hospital/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("hospitalReservation"))
			.andExpect(view().name("reservations/createOrUpdateHospitalReservationForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/reservations/hospital/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("pet.id", String.valueOf(TEST_PET_ID))
				.param("vet.id", "1")
				.param("reservationTime", "2025-03-01T10:00")
				.param("description", "Annual checkup"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/reservations/hospital/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("description", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("hospitalReservation"))
			.andExpect(view().name("reservations/createOrUpdateHospitalReservationForm"));
	}

	@Test
	void testInitUpdateForm() throws Exception {
		mockMvc.perform(get("/reservations/hospital/{reservationId}/edit", TEST_RESERVATION_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("hospitalReservation"))
			.andExpect(view().name("reservations/createOrUpdateHospitalReservationForm"));
	}

	@Test
	void testProcessUpdateFormSuccess() throws Exception {
		mockMvc
			.perform(post("/reservations/hospital/{reservationId}/edit", TEST_RESERVATION_ID)
				.param("pet.id", String.valueOf(TEST_PET_ID))
				.param("vet.id", "1")
				.param("reservationTime", "2025-03-02T14:00")
				.param("description", "Follow-up visit"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/reservations/hospital"));
	}

	@Test
	void testProcessUpdateFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/reservations/hospital/{reservationId}/edit", TEST_RESERVATION_ID)
				.param("pet.id", String.valueOf(TEST_PET_ID))
				.param("description", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("hospitalReservation"))
			.andExpect(view().name("reservations/createOrUpdateHospitalReservationForm"));
	}

}
