package es.um.asio.abstractions.filter;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Abstract implementation for JPA entity {@link Specification}.
 */
public abstract class AbstractJpaSpecification<T> implements Specification<T> {
    /**
     * Version ID.
     */
    private static final long serialVersionUID = -1185942703616574128L;

    /**
     * Create an equals {@link Predicate}.
     *
     * @param root
     *            Query root
     * @param criteriaBuilder
     *            Criteria builder
     * @param entityProperty
     *            Entity's property (attribute) which forms left part of the comparision
     * @param filter
     *            Right part of the comparision.
     * @return Predicate.
     */
    protected Predicate createEquals(final Root<T> root, final CriteriaBuilder criteriaBuilder,
            final String entityProperty, final Object filter) {
        return criteriaBuilder.equal(root.get(entityProperty), filter);
    }

    /**
     * Create an equals ignore case {@link Predicate}.
     *
     * @param root
     *            Query root
     * @param criteriaBuilder
     *            Criteria builder
     * @param entityProperty
     *            Entity's property (attribute) which forms left part of the comparision.
     * @param filter
     *            Right part of the comparision.
     * @return Predicate.
     */
    protected Predicate createEqualsIgnoreCase(final Root<T> root, final CriteriaBuilder criteriaBuilder,
            final String entityProperty, final String filter) {
        return criteriaBuilder.equal(criteriaBuilder.lower(root.get(entityProperty)), filter.toLowerCase());
    }

    /**
     * Create an contains ignore case {@link Predicate}. Gets the rows containing the filter string in some part of the
     * column specified by entity proprety.
     *
     * @param root
     *            Query root
     * @param criteriaBuilder
     *            Criteria builder
     * @param entityProperty
     *            Entity's property (attribute) which forms left part of the comparision.
     * @param filter
     *            Right part of the comparision.
     * @return Predicate.
     */
    protected Predicate createContainsIgnoreCase(final Root<T> root, final CriteriaBuilder criteriaBuilder,
            final String entityProperty, final String filter) {
        return criteriaBuilder.like(criteriaBuilder.lower(root.get(entityProperty)),
                String.format("%%%s%%", filter.toLowerCase()));
    }
}
