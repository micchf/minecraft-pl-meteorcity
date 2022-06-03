package ita.micc.meteorcity.enums;

/** SpawnPoint's enumeration
 * @author Codeh
 */
public enum SpawnPointType {
    PLAYER_SPAWN("PLAYER_SPAWN"),
    LAST_POINT("LAST_POINT");

    private final String type;

    SpawnPointType(String type) {
        this.type = type;
    }

    public String value() {
        return type;
    }
}