package ita.micc.meteorcity.database.bindclass;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Getter
@Setter
@Builder
public class SpawnPoint {

    private int ID;
    private int X;
    private int Y;
    private int Z;
    private String world;
    private String type;
    private int IDCity;

    /**
     * Convert SpawnPoint coordinates to Location for Minecraft
     * @return Location
     */
    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), X + 0.5, Y, Z + 0.5);
    }

    /**
     * Clone this
     * @return SpawnPoint clone
     */
    @Override
    public SpawnPoint clone() {
        return SpawnPoint.builder()
                .X(this.X)
                .Y(this.Y)
                .Z(this.Z)
                .world(this.world)
                .type(this.type)
                .build();
    }
}
