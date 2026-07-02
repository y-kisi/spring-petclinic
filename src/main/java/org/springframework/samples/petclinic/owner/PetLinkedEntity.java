package org.springframework.samples.petclinic.owner;

/**
 * Common interface for entities that are linked to a {@link Pet}.
 * <p>
 * Implemented by {@link HotelStay}, {@link HospitalReservation}, and
 * {@link TrimmingAppointment}.
 */
public interface PetLinkedEntity {

	Pet getPet();

	void setPet(Pet pet);

}
