package es.um.asio.service.model.relational;

public enum Action {

    INSERT("INSERT"),UPDATE("UPDATE"),DELETE("DELETE");

    private String text;

    Action(String text) {
        this.text = text;
    }


    public static Action fromString(String a) {
        for (Action action : Action.values()) {
            if (action.text.equalsIgnoreCase(a)) {
                return action;
            }
        }
        return null;
    }
}
