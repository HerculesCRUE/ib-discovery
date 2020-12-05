package es.um.asio.service.model.relational;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;
import java.util.Random;

public enum Action {

    INSERT("INSERT"),UPDATE("UPDATE"),DELETE("DELETE"),LINK("LINK");

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