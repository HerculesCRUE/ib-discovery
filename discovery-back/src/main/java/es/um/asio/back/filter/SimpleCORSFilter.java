package es.um.asio.back.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class SimpleCORSFilter extends OncePerRequestFilter {
    /** ACCESS_CONTROL_MAX_AGE header name. */
    private static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    /** ACCESS_CONTROL_ALLOW_METHODS header name. */
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

    /** OPTIONS header name. */
    private static final String OPTIONS = "OPTIONS";

    /** ACCESS_CONTROL_REQUEST_METHOD header name. */
    private static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";

    /** ACCESS_CONTROL_ALLOW_ORIGIN header name. */
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    /** Allowed methods for CORS. */
    private final String allowedMethods;

    /** Allowed origin for CORS. */
    private final String allowedOrigin;

    /** Allowed header values for CORS. */
    private final String[] allowedHeaders;

    /** Preflight max age. */
    private final String maxAge;

    /** Enable or disable this filter. */
    private final boolean enabled;

    /**
     * Constructor.
     *
     * @param enabled
     *            enable / disable filter execution
     * @param allowedOrigin
     *            Allowed origin for CORS
     * @param allowedMethods
     *            Allowed methods for CORS
     * @param allowedHeaders
     *            Allowed header values for CORS
     * @param maxAge
     *            Preflight max age
     */
    public SimpleCORSFilter(final boolean enabled, final String allowedOrigin, final String allowedMethods,
            final String[] allowedHeaders, final String maxAge) {
        super();
        this.allowedHeaders = allowedHeaders.clone();
        this.allowedOrigin = allowedOrigin;
        this.allowedMethods = allowedMethods;
        this.maxAge = maxAge;
        this.enabled = enabled;
    }

    /**
     * Execute filter logic.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @param filterChain
     *            the filter chain
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {
        if (!this.enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!request.getServletPath().contains("gs-guide-websocket")) {
            response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, this.allowedOrigin);
        }

        if ((request.getHeader(ACCESS_CONTROL_REQUEST_METHOD) != null) && OPTIONS.equals(request.getMethod())) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Sending Header....");
            }

            // CORS "pre-flight" request
            response.addHeader(ACCESS_CONTROL_ALLOW_METHODS, this.allowedMethods);

            for (final String header : this.allowedHeaders) {
                response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, header);
            }

            response.addHeader(ACCESS_CONTROL_MAX_AGE, this.maxAge);
        }

        if (!OPTIONS.equals(request.getMethod())) {
            filterChain.doFilter(request, response);
        }
    }
}
