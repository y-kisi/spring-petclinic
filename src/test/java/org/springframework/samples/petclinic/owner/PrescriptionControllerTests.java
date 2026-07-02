package org.springframework.samples.petclinic.owner;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PrescriptionController.class)
@DisabledInNativeImage
@DisabledInAotMode
class PrescriptionControllerTests {

	private static final int TEST_RESERVATION_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PrescriptionRepository prescriptions;

	@MockitoBean
	private HospitalReservationRepository reservations;

	private HospitalReservation testReservation;

	@BeforeEach
	void setup() {
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Leo");

		testReservation = new HospitalReservation();
		testReservation.setId(TEST_RESERVATION_ID);
		testReservation.setPet(pet);
		testReservation.setReservationTime(LocalDateTime.of(2025, 3, 1, 10, 0));
		testReservation.setDescription("Annual checkup");

		given(this.reservations.findById(TEST_RESERVATION_ID)).willReturn(Optional.of(testReservation));
	}

	@Test
	void testShowPrescriptionList() throws Exception {
		mockMvc.perform(get("/reservations/hospital/{reservationId}/prescriptions", TEST_RESERVATION_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("reservation"))
			.andExpect(view().name("reservations/prescriptionList"));
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/reservations/hospital/{reservationId}/prescriptions/new", TEST_RESERVATION_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("prescription"))
			.andExpect(view().name("reservations/createOrUpdatePrescriptionForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/reservations/hospital/{reservationId}/prescriptions/new", TEST_RESERVATION_ID)
				.param("reservation.id", String.valueOf(TEST_RESERVATION_ID))
				.param("medicineName", "Aspirin")
				.param("quantity", "3"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/reservations/hospital/" + TEST_RESERVATION_ID + "/prescriptions"));
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/reservations/hospital/{reservationId}/prescriptions/new", TEST_RESERVATION_ID)
				.param("medicineName", "")
				.param("quantity", "0"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("prescription"))
			.andExpect(view().name("reservations/createOrUpdatePrescriptionForm"));
	}

}
