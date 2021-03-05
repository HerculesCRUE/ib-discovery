package es.um.asio.service.model.appstate;

import lombok.Getter;

/**
 * Enum for State. One of [NOT_INITIALIZED, CACHED_DATA, UPLOAD_DATA]
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
public enum State {
    NOT_INITIALIZED(0),
    CACHED_DATA(1),
    UPLOAD_DATA(2);

    private int order;

    /**
     * Constructor
     * @param order. int. The order of State
     */
    State(int order) {
        this.order = order;
    }

}
