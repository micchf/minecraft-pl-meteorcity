package ita.micc.meteorcity.enums;

/** All city's member role
 * @author Codeh
 */
public enum MemberRole {
    PRESIDENTE("PRESIDENTE"),
    FUNZIONARIO("FUNZIONARIO"),
    CITTADINO("CITTADINO");

    private final String type;

    MemberRole(String type) {
        this.type = type;
    }

    public String value() {
        return type;
    }
}
