package ita.micc.meteorcity.database.bindclass;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/** LocationZone class for database table (locations)
 * @author Codeh
 */
@Getter
@Setter
@Builder
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
        return (minX >= location.getBlockX()
                && minZ <= location.getBlockZ()
                && maxX <= location.getBlockX()
                && maxZ >= location.getBlockZ());
    }
}
