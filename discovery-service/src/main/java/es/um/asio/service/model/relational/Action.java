package es.um.asio.service.model.relational;

import es.um.asio.service.config.DataProperties;

/**
 * Enumerated Class. In relational model the Action to do.
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public enum Action {

    INSERT("INSERT"),UPDATE("UPDATE"),DELETE("DELETE"),LINK("LINK"),LOD_LINK("LOD_LINK");

    private String text;

    /**
     * Constructor
     * @param text String. The value in String
     */
    Action(String text) {
        this.text = text;
    }


    /**
     * Get a Action from String
     * @param a String. The acction in String
     * @return Action. The action if match, else null
     */
    public static Action fromString(String a) {
        for (Action action : Action.values()) {
            if (action.text.equalsIgnoreCase(a)) {
                return action;
            }
        }
        return null;
    }

}