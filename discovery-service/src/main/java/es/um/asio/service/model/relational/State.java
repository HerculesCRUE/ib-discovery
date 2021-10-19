package es.um.asio.service.model.relational;

public enum State {
    CLOSED,OPEN,DISCARDED;

    public static State getFromString(String state) {
        if (state == null)
            return null;
        switch (state) {
            case "CLOSED":
                return State.CLOSED;
            case "OPEN":
                return State.OPEN;
            case "DISCARDED":
                return State.DISCARDED;
            default:
                return null;
        }
    }
}
