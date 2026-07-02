package org.springframework.samples.petclinic.owner;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
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

@WebMvcTest(HotelStayController.class)
@DisabledInNativeImage
@DisabledInAotMode
class HotelStayControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	private static final int TEST_STAY_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private HotelStayRepository stays;

	@MockitoBean
	private PetRepository pets;

	private Pet testPet;

	private HotelStay testStay;

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

		testStay = new HotelStay();
		testStay.setId(TEST_STAY_ID);
		testStay.setPet(testPet);
		testStay.setCheckInDate(LocalDate.of(2025, 1, 10));
		testStay.setCheckOutDate(LocalDate.of(2025, 1, 15));
		testStay.setRoomNumber("101");

		given(this.pets.findById(TEST_PET_ID)).willReturn(Optional.of(testPet));
		given(this.stays.findById(TEST_STAY_ID)).willReturn(Optional.of(testStay));
		given(this.stays.findAllByOrderByCheckInDateDesc()).willReturn(List.of(testStay));
	}

	@Test
	void testShowHotelStayList() throws Exception {
		mockMvc.perform(get("/reservations/hotel"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("stays"))
			.andExpect(view().name("reservations/hotelStayList"));
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/reservations/hotel/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("hotelStay"))
			.andExpect(view().name("reservations/createOrUpdateHotelStayForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/reservations/hotel/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("pet.id", String.valueOf(TEST_PET_ID))
				.param("checkInDate", "2025-01-10")
				.param("checkOutDate", "2025-01-15")
				.param("roomNumber", "101"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/reservations/hotel/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("roomNumber", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("hotelStay"))
			.andExpect(view().name("reservations/createOrUpdateHotelStayForm"));
	}

	@Test
	void testInitUpdateForm() throws Exception {
		mockMvc.perform(get("/reservations/hotel/{stayId}/edit", TEST_STAY_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("hotelStay"))
			.andExpect(view().name("reservations/createOrUpdateHotelStayForm"));
	}

	@Test
	void testProcessUpdateFormSuccess() throws Exception {
		mockMvc
			.perform(
					post("/reservations/hotel/{stayId}/edit", TEST_STAY_ID).param("pet.id", String.valueOf(TEST_PET_ID))
						.param("checkInDate", "2025-01-10")
						.param("checkOutDate", "2025-01-15")
						.param("roomNumber", "102"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/reservations/hotel"));
	}

	@Test
	void testProcessUpdateFormHasErrors() throws Exception {
		mockMvc
			.perform(
					post("/reservations/hotel/{stayId}/edit", TEST_STAY_ID).param("pet.id", String.valueOf(TEST_PET_ID))
						.param("roomNumber", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("hotelStay"))
			.andExpect(view().name("reservations/createOrUpdateHotelStayForm"));
	}

}
