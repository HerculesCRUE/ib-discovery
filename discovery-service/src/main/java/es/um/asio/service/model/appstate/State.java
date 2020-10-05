package es.um.asio.service.model.appstate;

import lombok.Getter;

@Getter
public enum State {
    NOT_INITIALIZED(0),
    CACHED_DATA(1),
    UPLOAD_DATA(2);

    private int order;

    State(int order) {
        this.order = order;
    }

    private int compare(State other) {
        return this.getOrder()- other.getOrder();
    }
}
