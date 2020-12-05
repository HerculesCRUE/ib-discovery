package es.um.asio.service.model.relational;

import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.util.Utils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;
import java.util.Random;
import java.util.Set;

public enum MergeAction {
    UPDATE,INSERT,DELETE
}
