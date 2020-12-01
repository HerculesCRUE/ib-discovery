package es.um.asio.service.model;

public enum BasicAction {
    INSERT("INSERT"),UPDATE("UPDATE"),DELETE("DELETE"),LINK("LINK");

    private String text;

    BasicAction(String text) {
        this.text = text;
    }


    public static BasicAction fromString(String a) {
        for (BasicAction basicAction : BasicAction.values()) {
            if (basicAction.text.equalsIgnoreCase(a)) {
                return basicAction;
            }
        }
        return null;
    }


}
