package es.um.asio.swagger.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Annotation proxy for defining annotations into a class.
 */
@Accessors(fluent = true)
public class AnnotationProxy implements Annotation, InvocationHandler {
    /**
     * Annotation type.
     */
    @Getter
    private final Class<? extends Annotation> annotationType;
    /**
     * Values map.
     */
    private final Map<String, Object> values;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return this.values.getOrDefault(method.getName(), method.getDefaultValue());
    }

    /**
     * Creates a new annotation proxy.
     *
     * @param <A>
     *            Annotation type.
     * @param annotation
     *            Annotation.
     * @param values
     *            Parameter values.
     * @return Annotation proxy.
     */
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A of(final Class<A> annotation, final Map<String, Object> values) {
        return (A) Proxy.newProxyInstance(annotation.getClassLoader(), new Class[] { annotation },
                new AnnotationProxy(annotation, new HashMap<String, Object>(values)));
    }

    /**
     * Private constructor
     *
     * @param annotationType
     *            Annotation type
     * @param values
     *            Values
     */
    private AnnotationProxy(final Class<? extends Annotation> annotationType, final Map<String, Object> values) {
        super();
        this.annotationType = annotationType;
        this.values = values;
        // Required because getDefaultValue() returns null for this call
        values.put("annotationType", annotationType);
    }
}
