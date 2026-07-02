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
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TrimmingAppointmentController.class)
@DisabledInNativeImage
@DisabledInAotMode
class TrimmingAppointmentControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	private static final int TEST_APPOINTMENT_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TrimmingAppointmentRepository appointments;

	@MockitoBean
	private PetRepository pets;

	private Pet testPet;

	private TrimmingAppointment testAppointment;

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

		testAppointment = new TrimmingAppointment();
		testAppointment.setId(TEST_APPOINTMENT_ID);
		testAppointment.setPet(testPet);
		testAppointment.setAppointmentTime(LocalDateTime.of(2025, 2, 15, 14, 0));
		testAppointment.setCourse("Full grooming");
		testAppointment.setTrimmerName("Alice");

		given(this.pets.findById(TEST_PET_ID)).willReturn(Optional.of(testPet));
		given(this.appointments.findById(TEST_APPOINTMENT_ID)).willReturn(Optional.of(testAppointment));
		given(this.appointments.findAllByOrderByAppointmentTimeDesc()).willReturn(List.of(testAppointment));
	}

	@Test
	void testShowTrimmingList() throws Exception {
		mockMvc.perform(get("/reservations/trimming"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("appointments"))
			.andExpect(view().name("reservations/trimmingAppointmentList"));
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/reservations/trimming/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("trimmingAppointment"))
			.andExpect(view().name("reservations/createOrUpdateTrimmingAppointmentForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/reservations/trimming/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("pet.id", String.valueOf(TEST_PET_ID))
				.param("appointmentTime", "2025-02-15T14:00")
				.param("course", "Full grooming")
				.param("trimmerName", "Alice"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/reservations/trimming/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("course", "")
				.param("trimmerName", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("trimmingAppointment"))
			.andExpect(view().name("reservations/createOrUpdateTrimmingAppointmentForm"));
	}

	@Test
	void testInitUpdateForm() throws Exception {
		mockMvc.perform(get("/reservations/trimming/{appointmentId}/edit", TEST_APPOINTMENT_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("trimmingAppointment"))
			.andExpect(view().name("reservations/createOrUpdateTrimmingAppointmentForm"));
	}

	@Test
	void testProcessUpdateFormSuccess() throws Exception {
		mockMvc
			.perform(post("/reservations/trimming/{appointmentId}/edit", TEST_APPOINTMENT_ID)
				.param("pet.id", String.valueOf(TEST_PET_ID))
				.param("appointmentTime", "2025-02-16T10:00")
				.param("course", "Cut only")
				.param("trimmerName", "Bob"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/reservations/trimming"));
	}

	@Test
	void testProcessUpdateFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/reservations/trimming/{appointmentId}/edit", TEST_APPOINTMENT_ID)
				.param("pet.id", String.valueOf(TEST_PET_ID))
				.param("course", "")
				.param("trimmerName", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("trimmingAppointment"))
			.andExpect(view().name("reservations/createOrUpdateTrimmingAppointmentForm"));
	}

}
