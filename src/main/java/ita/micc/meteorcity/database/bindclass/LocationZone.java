package ita.micc.meteorcity.database.bindclass;

import ita.micc.meteorcity.enums.BuildType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Location;

/** LocationZone class for database table (locations)
 * @author Codeh
 */
@Getter
@Setter
@Builder
@ToString
public class LocationZone {

    private int ID;
    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;
    private String world;
    private String type;
    private int level;
    private int IDCity;

    /**
     * Check if location zone's area contain specific location.
     * @param location bukkit location
     * @return true if contained, false if not.
     */
    public boolean contains(Location location) {
        int x1 = Math.min(minX, maxX);
        int z1 = Math.min(minZ, maxZ);
        int x2 = Math.max(minX, maxX);
        int z2 = Math.max(minZ, maxZ);

        return location.getBlockX() >= x1 && location.getBlockX() <= x2 && location.getBlockZ() >= z1 && location.getBlockZ() <= z2;
    }

    /**
     * Get build type in enum
     * @return enum
     */
    public BuildType toEnumType() {
        return BuildType.valueOf(type);
    }
}
