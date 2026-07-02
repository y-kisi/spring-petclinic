package org.springframework.samples.petclinic.util;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Shared utility for looking up JPA entities by ID with consistent error handling.
 */
public final class EntityLookupUtil {

	private EntityLookupUtil() {
	}

	/**
	 * Finds an entity by ID or throws {@link IllegalArgumentException}.
	 * @param repository the JPA repository to query
	 * @param id the entity identifier
	 * @param entityName human-readable entity name used in the error message
	 * @return the found entity, never {@literal null}
	 */
	public static <T> T findByIdOrThrow(JpaRepository<T, Integer> repository, int id, String entityName) {
		return repository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(
					entityName + " not found with id: " + id + ". Please ensure the ID is correct."));
	}

	/**
	 * Unwraps an {@link Optional} or throws {@link IllegalArgumentException}.
	 * @param optional the optional to unwrap
	 * @param entityName human-readable entity name used in the error message
	 * @param id the entity identifier (for the error message)
	 * @return the found entity, never {@literal null}
	 */
	public static <T> T unwrapOrThrow(Optional<T> optional, String entityName, int id) {
		return optional.orElseThrow(() -> new IllegalArgumentException(
				entityName + " not found with id: " + id + ". Please ensure the ID is correct."));
	}

}
