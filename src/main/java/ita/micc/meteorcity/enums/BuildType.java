package ita.micc.meteorcity.enums;

/** All city build's enumeration
 * @author Codeh
 */
public enum BuildType {
    EMPTY("EMPTY"),

    TOWN_HALL("TOWN_HALL"),
    MAIN("MAIN"),
    WILD_ZONE("WILD_ZONE"),

    BROKER("BROKER"),
    PENTAGONO("PENTAGONO"),
    CATASTO("CATASTO"),

    TERRICCIO("TERRICCIO"),
    HOUSE("HOUSE"),
    VILLA("VILLA"),
    CONDOMINIO("CONDOMINIO"),
    HOTEL("HOTEL");

    private final String type;

    BuildType(String type) {
        this.type = type;
    }

    public String value() {
        return type;
    }
}