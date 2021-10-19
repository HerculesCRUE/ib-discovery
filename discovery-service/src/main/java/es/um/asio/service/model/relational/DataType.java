package es.um.asio.service.model.relational;

/**
 * Enumerated Class. In relational model the type of the value.
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public enum DataType {
    DOUBLE, FLOAT, INTEGER, LONG, BOOLEAN, DATE, STRING, OBJECT;

    public static DataType getFromString(String value) {
        if (value == null)
            return null;
        switch (value) {
            case "DOUBLE":
                return DataType.DOUBLE;
            case "FLOAT":
                return DataType.FLOAT;
            case "INTEGER":
                return DataType.INTEGER;
            case "LONG":
                return DataType.LONG;
            case "BOOLEAN":
                return DataType.BOOLEAN;
            case "DATE":
                return DataType.DATE;
            case "STRING":
                return DataType.STRING;
            case "OBJECT":
                return DataType.OBJECT;
            default:
                return null;
        }
    }
}
