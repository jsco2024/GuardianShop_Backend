package com.ms_security.ms_security.utilities;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Utility class for handling pagination and sorting operations.
 * Provides a method to create a Pageable object for querying paginated data.
 */
@Component
public class PaginationUtilities {

    /**
     * Creates a Pageable object for pagination and sorting.
     *
     * @param page the page number (zero-based index)
     * @param size the number of items per page
     * @param sortBy the field by which to sort
     * @param direction the direction of sorting, either 'ASC' (ascending) or 'DESC' (descending)
     * @return a Pageable object configured with the specified pagination and sorting parameters
     */
    public static Pageable createPageable(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        return PageRequest.of(page, size, sort);
    }
}
