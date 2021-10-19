package es.um.asio.service.model.relational;

/**
 * Enumerated Class. In relational model the Merge Action to do.
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public enum MergeAction {
    UPDATE,INSERT,DELETE;

    public static MergeAction getFromString(String mergeAction) {
        if (mergeAction == null)
            return null;
        switch (mergeAction) {
            case "UPDATE":
                return MergeAction.UPDATE;
            case "INSERT":
                return MergeAction.INSERT;
            case "DELETE":
                return MergeAction.DELETE;
            default:
                return null;
        }
    }
}
