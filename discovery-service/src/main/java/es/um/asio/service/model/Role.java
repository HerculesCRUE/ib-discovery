package es.um.asio.service.model;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Role {
    /**
     * Defines administrator users.
     */
    ADMINISTRATOR(new AdministratorAuthority()),
    /**
     * Defines normal users.
     */
    USER(new UserAuthority()),
    /**
     * Defines anonymous users.
     */
    ANONYMOUS(new AnonymousAuthority());

    /**
     * Granted authority.
     */
    private final GrantedAuthority grantedAuthority;

    /**
     * Gets the authority.
     *
     * @return the authority
     */
    public GrantedAuthority getGrantedAuthority() {
        return this.grantedAuthority;
    }

    /**
     * Administrator role.
     */
    public static final String ADMINISTRATOR_ROLE = "ROLE_ADMINISTRATOR";

    /**
     * Normal user role.
     */
    public static final String USER_ROLE = "ROLE_USER";

    /**
     * Anonymous role.
     */
    public static final String ANONYMOUS_ROLE = "ROLE_ANONYMOUS";

    /**
     * Administrator authority.
     */
    public static final class AdministratorAuthority implements GrantedAuthority, Serializable {

        /**
         * Version ID.
         */
        private static final long serialVersionUID = -2025230374849824612L;

        /*
         * (non-Javadoc)
         * @see org.springframework.security.core.GrantedAuthority#getAuthority()
         */
        @Override
        public String getAuthority() {
            return ADMINISTRATOR_ROLE;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return Objects.hashCode(AdministratorAuthority.class);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {

            boolean equals = false;

            if ((this == obj) || (obj instanceof AdministratorAuthority)) {
                equals = true;
            } else if (obj instanceof GrantedAuthority) {
                equals = this.getAuthority().equals(((GrantedAuthority) obj).getAuthority());
            }

            return equals;
        }
    }

    /**
     * Basic user authority.
     */
    public static final class UserAuthority implements GrantedAuthority, Serializable {

        /**
         * Version ID.
         */
        private static final long serialVersionUID = 7028138804996471844L;

        /*
         * (non-Javadoc)
         * @see org.springframework.security.core.GrantedAuthority#getAuthority()
         */
        @Override
        public String getAuthority() {
            return USER_ROLE;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return Objects.hashCode(UserAuthority.class);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {

            boolean equals = false;

            if ((this == obj) || (obj instanceof UserAuthority)) {
                equals = true;
            } else if (obj instanceof GrantedAuthority) {
                equals = this.getAuthority().equals(((GrantedAuthority) obj).getAuthority());
            }

            return equals;
        }
    }

    /**
     * Anonymous user.
     */
    public static final class AnonymousAuthority implements GrantedAuthority, Serializable {

        /**
         * Version ID.
         */
        private static final long serialVersionUID = 258847940543090710L;

        /*
         * (non-Javadoc)
         * @see org.springframework.security.core.GrantedAuthority#getAuthority()
         */
        @Override
        public String getAuthority() {
            return ANONYMOUS_ROLE;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return Objects.hashCode(AnonymousAuthority.class);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {

            boolean equals = false;

            if ((this == obj) || (obj instanceof AnonymousAuthority)) {
                equals = true;
            } else if (obj instanceof GrantedAuthority) {
                equals = this.getAuthority().equals(((GrantedAuthority) obj).getAuthority());
            }

            return equals;
        }
    }
}
