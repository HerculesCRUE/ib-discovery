package es.um.asio.service.model.appstate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class DataState {

    private State state;
    private Date lastDate;

    public DataState() {
        state = State.NOT_INITIALIZED;
        lastDate = null;
    }

    public DataState(State state) {
        this.state = state;
        lastDate = new Date();
    }

}
