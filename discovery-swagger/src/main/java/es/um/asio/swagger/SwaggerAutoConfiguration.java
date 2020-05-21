package es.um.asio.swagger;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import es.um.asio.swagger.annotation.AnnotationProxy;
import es.um.asio.swagger.properties.SwaggerProperties;
import io.swagger.annotations.ApiParam;
import springfox.documentation.builders.AlternateTypeBuilder;
import springfox.documentation.builders.AlternateTypePropertyBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger configuration.
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = "app.swagger", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(SwaggerProperties.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class SwaggerAutoConfiguration {

    /**
     * Configuration properties.
     */
    @Autowired
    private SwaggerProperties properties;

    /**
     * Swagger API.
     *
     * @return the Swagger API.
     */
    @Bean
    public Docket api(@Autowired final TypeResolver typeResolver) {
        // @formatter:off

        final Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();

        // @formatter:on

        if (this.properties.isSecured()) {
            docket.securitySchemes(Arrays.asList(this.securityScheme()));
            docket.securityContexts(Arrays.asList(this.securityContext()));
        }

        return docket;
    }

    /**
     * Configures type rule convention for {@link Pageable}.
     *
     * @param webProperties
     *            Spring Data web properties.
     * @return Type rule convention for {@link Pageable}.
     */
    @Bean
    public AlternateTypeRuleConvention springDataWebPropertiesConvention(final SpringDataWebProperties webProperties) {
        return new AlternateTypeRuleConvention() {
            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }

            @Override
            public List<AlternateTypeRule> rules() {
                return Collections
                        .singletonList(AlternateTypeRules.newRule(Pageable.class, SwaggerAutoConfiguration.this
                                .pageableDocumentedType(webProperties.getPageable(), webProperties.getSort())));
            }
        };
    }

    /**
     * Configures springfox security.
     *
     * @return {@link SecurityConfiguration}
     */
    @Bean
    @ConditionalOnProperty(prefix = "app.swagger", name = "secured", havingValue = "true", matchIfMissing = false)
    public SecurityConfiguration security() {
        // @formatter:off

        return SecurityConfigurationBuilder.builder()
                .clientId(this.properties.getAuthServerClientId())
                .clientSecret(this.properties.getAuthServerClientSecret())
                .scopeSeparator(" ")
                .useBasicAuthenticationWithAccessCodeGrant(true)
                .build();

        // @formatter:on
    }

    /**
     * Gets security scheme.
     *
     * @return {@link SecurityScheme}.
     */
    private OAuth securityScheme() {

        final List<AuthorizationScope> authorizationScopeList = Lists.newArrayList();
        authorizationScopeList.add(new AuthorizationScope("read", "read all"));
        authorizationScopeList.add(new AuthorizationScope("write", "access all"));

        final List<GrantType> grantTypes = Lists.newArrayList();
        final GrantType passwordCredentialsGrant = new ResourceOwnerPasswordCredentialsGrant(
                this.properties.getAuthServerUrl() + "/token");
        grantTypes.add(passwordCredentialsGrant);

        return new OAuth("oauth2", authorizationScopeList, grantTypes);
    }

    /**
     * Gets security context.
     *
     * @return {@link SecurityContext}.
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(this.defaultAuth()).build();
    }

    /**
     * Gets default authentication.
     *
     * @return Default authentication.
     */
    private List<SecurityReference> defaultAuth() {

        final AuthorizationScope[] authorizationScopes = new AuthorizationScope[3];
        authorizationScopes[0] = new AuthorizationScope("read", "read all");
        authorizationScopes[1] = new AuthorizationScope("trust", "trust all");
        authorizationScopes[2] = new AuthorizationScope("write", "write all");

        return Collections.singletonList(new SecurityReference("oauth2", authorizationScopes));
    }

    /**
     * Gets pageable documented type.
     *
     * @param pageable
     *            Pageable propreties.
     * @param sort
     *            Sort properties.
     * @return Pageable documented type.
     */
    private Type pageableDocumentedType(final SpringDataWebProperties.Pageable pageable,
            final SpringDataWebProperties.Sort sort) {
        final String firstPage = pageable.isOneIndexedParameters() ? "1" : "0";
        // @formatter:off

        return new AlternateTypeBuilder()
                .fullyQualifiedClassName(this.fullyQualifiedName(Pageable.class))
                .property(this.property(pageable.getPageParameter(), Integer.class, ImmutableMap.of(
                        VALUE, String.format("Page %s", (pageable.isOneIndexedParameters() ? "Number" : "Index")),
                        DEFAULT_VALUE, firstPage,
                        ALLOWABLE_VALUES, String.format("range[%s, %s]", firstPage, Integer.MAX_VALUE),
                        EXAMPLE, firstPage
                )))
                .property(this.property(pageable.getSizeParameter(), Integer.class, ImmutableMap.of(
                        VALUE, "Page Size",
                        DEFAULT_VALUE, String.valueOf(pageable.getDefaultPageSize()),
                        ALLOWABLE_VALUES, String.format("range[1, %s]", pageable.getMaxPageSize()),
                        EXAMPLE, "5"
                )))
                .property(this.property(sort.getSortParameter(), String[].class, ImmutableMap.of(
                        VALUE, "Page Multi-Sort: fieldName, (asc|desc)"
                )))
                .build();

        // @formatter:on
    }

    /**
     * Gets fully qualified name.
     *
     * @param convertedClass
     *            Converted class.
     * @return fully qualified name
     */
    private String fullyQualifiedName(final Class<?> convertedClass) {
        return String.format("%s.generated.%s", convertedClass.getPackage().getName(), convertedClass.getSimpleName());
    }

    /**
     * Generates a property
     *
     * @param name
     *            Property name.
     * @param type
     *            Property type.
     * @param parameters
     *            Parameter.s
     * @return Alternate type property.
     */
    private AlternateTypePropertyBuilder property(final String name, final Class<?> type,
            final Map<String, Object> parameters) {
     // @formatter:off
        return new AlternateTypePropertyBuilder()
                .withName(name)
                .withType(type)
                .withCanRead(true)
                .withCanWrite(true)
                .withAnnotations(Collections.singletonList(AnnotationProxy.of(ApiParam.class, parameters)));
     // @formatter:on
    }

    /**
     * Value property name.
     */
    private static final String VALUE = "value";
    /**
     * Default value property name.
     */
    private static final String DEFAULT_VALUE = "defaultValue";
    /**
     * Allowable values property name.
     */
    private static final String ALLOWABLE_VALUES = "allowableValues";
    /**
     * Example property name.
     */
    private static final String EXAMPLE = "example";
}
