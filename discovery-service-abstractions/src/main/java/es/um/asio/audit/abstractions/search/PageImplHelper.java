package es.um.asio.audit.abstractions.search;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Page implementation helper to be use in mappers.
 *
 * @param <T>
 *            generic type.
 */
public class PageImplHelper<T> extends PageImpl<T> {
    /**
     * Version ID.
     */
    private static final long serialVersionUID = -2662764836338549387L;

    /**
     * Instantiates a new page.
     */
    public PageImplHelper() {
        super(new ArrayList<>());
    }

    /**
     * Instantiates a new page.
     *
     * @param content
     *            the content of this page, must not be {@literal null}.
     * @param pageable
     *            the paging information, must not be {@literal null}.
     * @param total
     *            the total amount of items available. The total might be adapted considering the length of the content
     *            given, if it is going to be the content of the last page. This is in place to mitigate
     *            inconsistencies.
     */
    public PageImplHelper(final List<T> content, final Pageable pageable, final long total) {
        super(content, pageable, total);
    }

    /**
     * Instantiates a new page.
     *
     * @param content
     *            must not be {@literal null}.
     */
    public PageImplHelper(final List<T> content) {
        super(content);
    }

    /**
     * Adds the element.
     *
     * @param element
     *            the element
     */
    public void add(final T element) {
        this.getContent().add(element);
    }

    /**
     * Sets the size.
     *
     * @param size
     *            the new size
     */
    public void setSize(final int size) {
        // empty
    }
}
