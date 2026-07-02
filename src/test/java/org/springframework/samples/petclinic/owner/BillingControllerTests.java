package org.springframework.samples.petclinic.owner;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BillingController.class)
@DisabledInNativeImage
@DisabledInAotMode
class BillingControllerTests {

	private static final int TEST_STAY_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BillingRepository billings;

	@MockitoBean
	private HotelStayRepository stays;

	private HotelStay testStay;

	@BeforeEach
	void setup() {
		Owner owner = new Owner();
		owner.setId(1);
		owner.setFirstName("George");
		owner.setLastName("Franklin");

		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Leo");
		pet.setOwner(owner);

		testStay = new HotelStay();
		testStay.setId(TEST_STAY_ID);
		testStay.setPet(pet);
		testStay.setCheckInDate(LocalDate.of(2025, 1, 10));
		testStay.setCheckOutDate(LocalDate.of(2025, 1, 15));
		testStay.setRoomNumber("101");

		given(this.stays.findById(TEST_STAY_ID)).willReturn(Optional.of(testStay));
	}

	@Test
	void testShowBillingForm() throws Exception {
		mockMvc.perform(get("/stays/{stayId}/billing", TEST_STAY_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("billing"))
			.andExpect(view().name("reservations/billingForm"));
	}

	@Test
	void testProcessBillingSuccess() throws Exception {
		mockMvc
			.perform(post("/stays/{stayId}/billing", TEST_STAY_ID).param("stay.id", String.valueOf(TEST_STAY_ID))
				.param("amount", "25000"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/reservations/hotel"));
	}

	@Test
	void testShowBillingFormCalculatesAmount() throws Exception {
		mockMvc.perform(get("/stays/{stayId}/billing", TEST_STAY_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("billing"))
			.andExpect(model().attribute("billing", org.hamcrest.Matchers.hasProperty("amount",
					org.hamcrest.Matchers.comparesEqualTo(new java.math.BigDecimal("25000")))));
	}

}
