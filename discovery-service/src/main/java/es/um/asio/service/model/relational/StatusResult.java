package es.um.asio.service.model.relational;

/**
 * Enumerated Class. In relational model the Status Result.
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public enum StatusResult {
    PENDING,COMPLETED,FAIL,ABORTED;

    static StatusResult getFromString(String status) {
        if (status == null)
            return null;
        switch (status) {
            case "PENDING":
                return StatusResult.PENDING;
            case "COMPLETED":
                return StatusResult.COMPLETED;
            case "FAIL":
                return StatusResult.FAIL;
            case "ABORTED":
                return StatusResult.ABORTED;
            default:
                return null;
        }
    }
}
