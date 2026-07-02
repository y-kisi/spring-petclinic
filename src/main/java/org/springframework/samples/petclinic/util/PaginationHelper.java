package org.springframework.samples.petclinic.util;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

/**
 * Shared utility for setting up pagination model attributes.
 */
public final class PaginationHelper {

	public static final int DEFAULT_PAGE_SIZE = 5;

	private PaginationHelper() {
	}

	/**
	 * Populates the model with standard pagination attributes.
	 * @param page the current 1-based page number
	 * @param paginated the page result
	 * @param listAttributeName the model attribute name for the content list
	 * @param viewName the view to return
	 * @param model the Spring MVC model
	 * @return the view name
	 */
	public static <T> String addPaginationModel(int page, Page<T> paginated, String listAttributeName, String viewName,
			Model model) {
		List<T> content = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute(listAttributeName, content);
		return viewName;
	}

	/**
	 * Creates a {@link Pageable} for the given 1-based page number using the default page
	 * size.
	 */
	public static Pageable createPageable(int page) {
		return PageRequest.of(page - 1, DEFAULT_PAGE_SIZE);
	}

}
