package es.um.asio.service.model;

//;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.google.common.collect.Lists;
//import es.um.asio.audit.model.Auditable;
//import es.um.asio.service.util.JpaConstants;
//import es.um.asio.service.util.ValidationConstants;
//import lombok.*;
//import org.hibernate.annotations.GenericGenerator;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import javax.persistence.*;
//import javax.validation.constraints.Size;
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//// import com.izertis.libraries.audit.model.Auditable;
//
//
///**
// * Application user.
// */
//@Entity
//@Table(name = User.TABLE, indexes = { @Index(columnList = User.Columns.NAME) }, uniqueConstraints = {
//        @UniqueConstraint(columnNames = User.Columns.EMAIL), @UniqueConstraint(columnNames = User.Columns.USERNAME) })
//@Getter
//@Setter
//@ToString(includeFieldNames = true)
//@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
//public class User extends Auditable implements UserDetails {
//
//    /**
//     * Version ID.
//     */
//    private static final long serialVersionUID = -8605786237765754616L;
//
//    /**
//     * The id.
//     */
//    @Id
//    @GeneratedValue(generator = JpaConstants.HIBERNATE_UUID_GENERATOR_NAME)
//    @GenericGenerator(name = JpaConstants.HIBERNATE_UUID_GENERATOR_NAME, strategy = JpaConstants.HIBERNATE_UUID_GENERATOR_STRATEGY)
//    @Column(name = Columns.ID)
//    @EqualsAndHashCode.Include
//    private String id;
//
//    /**
//     * User real name.
//     */
//    @Size(min = 1, max = ValidationConstants.MAX_LENGTH_DEFAULT)
//    @Column(name = Columns.NAME, nullable = true, length = ValidationConstants.MAX_LENGTH_DEFAULT)
//    private String name;
//
//    /**
//     * Email.
//     */
//    @Column(name = Columns.EMAIL)
//    private String email;
//
//    /**
//     * Flag that indicates whether user is enabled or not.
//     */
//    @Column(name = Columns.ENABLED)
//    private boolean enabled;
//
//    /**
//     * Flag that indicates whether credentials are expired or not.
//     */
//    @Column(name = Columns.CREDENTIALS_NON_EXPIRED)
//    private boolean credentialsNonExpired;
//
//    /**
//     * Flag that indicates whether account are expired or not.
//     */
//    @Column(name = Columns.ACCOUNT_NON_EXPIRED)
//    private boolean accountNonExpired;
//
//    /**
//     * Flag that indicates whether account is expired or not.
//     */
//    @Column(name = Columns.ACCOUNT_NON_LOCKED)
//    private boolean accountNonLocked;
//
//    /**
//     * User password.
//     */
//    @Column(name = Columns.PASSWORD)
//    private String password;
//
//    /**
//     * Password recovery hash.
//     */
//    @Column(name = Columns.PASSWORD_RECOVERY_HASH)
//    private String passwordRecoveryHash;
//
//    /**
//     * User name
//     */
//    @Column(name = Columns.USERNAME)
//    private String username;
//
//    /**
//     * Country
//     */
//    @Column(name = Columns.COUNTRY)
//    private String country;
//
//    /**
//     * City
//     */
//    @Column(name = Columns.CITY)
//    private String city;
//
//    /**
//     * Language
//     */
//    @Column(name = Columns.LANGUAGE)
//    private String language;
//
//    /**
//     * Address
//     */
//    @Column(name = Columns.ADDRESS)
//    private String address;
//
//    /**
//     * Role list.
//     */
//    @ElementCollection(fetch = FetchType.EAGER)
//    @JoinTable(name = UserRole.TABLE, joinColumns = { @JoinColumn(name = UserRole.Columns.USER_ID) })
//    @Column(name = Columns.ROLE, nullable = false)
//    @Enumerated(EnumType.STRING)
//    private Set<Role> roles;
//
//    /**
//     * Version
//     */
//    @Version
//    private Integer version;
//
//    /*
//     * (non-Javadoc)
//     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
//     */
//    @JsonIgnore
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return this.transform(
//                Collections.unmodifiableList(Lists.newArrayList(this.roles == null ? new HashSet<Role>() : this.roles)),
//                USER_AUTHORITY_TRANSFORMATION);
//    }
//
//    /**
//     * Function to transform database user authorities into spring security format.
//     */
//    private static final Function<Role, GrantedAuthority> USER_AUTHORITY_TRANSFORMATION = from -> from
//            .getGrantedAuthority();
//
//    private <K, V, Q extends K> List<V> transform(final List<Q> input, final Function<K, V> tfunc) {
//        if (null == input) {
//            return Collections.emptyList();
//        }
//        return input.stream().map(tfunc).collect(Collectors.toList());
//    }
//
//    /**
//     * Table name.
//     */
//    public static final String TABLE = "APPLICATION_USER";
//
//    /**
//     * Column name constants.
//     */
//    @NoArgsConstructor(access = AccessLevel.PRIVATE)
//    static final class Columns {
//
//        /**
//         * User name column.
//         */
//        protected static final String USERNAME = "USERNAME";
//
//        /**
//         * Country column.
//         */
//        protected static final String COUNTRY = "COUNTRY";
//
//        /**
//         * City column.
//         */
//        protected static final String CITY = "CITY";
//
//        /**
//         * Language column.
//         */
//        protected static final String LANGUAGE = "LANGUAGE";
//
//        /**
//         * Address column.
//         */
//        protected static final String ADDRESS = "ADDRESS";
//
//        /**
//         * User type column.
//         */
//        protected static final String USER_TYPE = "USER_TYPE";
//
//        /**
//         * ID column.
//         */
//        protected static final String ID = "ID";
//
//        /**
//         * Name column.
//         */
//        protected static final String NAME = "NAME";
//
//        /**
//         * eMail column.
//         */
//        protected static final String EMAIL = "EMAIL";
//
//        /**
//         * Password column.
//         */
//        @SuppressWarnings("squid:S2068")
//        protected static final String PASSWORD = "PASSWORD";
//
//        /**
//         * Enabled column.
//         */
//        protected static final String ENABLED = "ENABLED";
//
//        /**
//         * Credentials non expired column.
//         */
//        protected static final String CREDENTIALS_NON_EXPIRED = "CREDENTIALS_NON_EXPIRED";
//
//        /**
//         * Accoumnt non expired column.
//         */
//        protected static final String ACCOUNT_NON_EXPIRED = "ACCOUNT_NON_EXPIRED";
//
//        /**
//         * Account non locked column.
//         */
//        protected static final String ACCOUNT_NON_LOCKED = "ACCOUNT_NON_LOCKED";
//
//        /**
//         * Password recovery hash column.
//         */
//        @SuppressWarnings("squid:S2068")
//        protected static final String PASSWORD_RECOVERY_HASH = "PASSWORD_RECOVERY_HASH";
//
//        /**
//         * Roles column.
//         */
//        protected static final String ROLE = "ROLE";
//    }
//}
