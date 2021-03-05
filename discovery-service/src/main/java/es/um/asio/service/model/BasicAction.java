package es.um.asio.service.model;

/**
 * Enumerated Class. BasicAction one of this [INSERT,UPDATE,DELETE,LINK].
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
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
