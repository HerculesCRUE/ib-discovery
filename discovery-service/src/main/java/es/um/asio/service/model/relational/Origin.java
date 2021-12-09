package es.um.asio.service.model.relational;

public enum Origin {
    ASIO, LOD;

    public static Origin getFromString(String origin) {
        if (origin == null)
            return null;
        switch (origin) {
            case "ASIO":
                return Origin.ASIO;
            case "LOD":
                return Origin.LOD;
            default:
                return null;
        }
    }
}
