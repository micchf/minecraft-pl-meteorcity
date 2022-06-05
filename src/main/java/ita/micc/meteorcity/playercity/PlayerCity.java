package ita.micc.meteorcity.playercity;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.citytemplate.CityTemplate;
import ita.micc.meteorcity.citytemplate.Pivot;
import ita.micc.meteorcity.database.DatabaseInstance;
import ita.micc.meteorcity.database.bindclass.City;
import ita.micc.meteorcity.database.bindclass.LocationZone;
import ita.micc.meteorcity.database.bindclass.Member;
import ita.micc.meteorcity.database.bindclass.SpawnPoint;
import ita.micc.meteorcity.database.query.QueryInfo;
import ita.micc.meteorcity.enums.BuildType;
import ita.micc.meteorcity.enums.MemberRole;
import ita.micc.meteorcity.enums.SpawnPointType;
import ita.micc.meteorcity.schematic.Schematic;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/** PlayerCity for cache
 * @author Codeh
 */
@Getter
public class PlayerCity {

    private final City city;
    private final LocationZone townHall;
    private final LocationZone main;
    private final LocationZone wildZone;
    private final SpawnPoint playerSpawn;
    private final CityTemplate cityTemplate;
    private final HashMap<String, Member> members;

    private @Setter boolean disband;

    /**
     * Constructor (When create new City)
     * @param cityTemplate From which the playercity will be created.
     * @param lastPoint that there is no cities already exist.
     * @param ownerUUID UUID who created city.
     */
    public PlayerCity(CityTemplate cityTemplate, SpawnPoint lastPoint, String ownerUUID) {
        members = new HashMap<>();
        disband = false;
        City city = new City(0, cityTemplate.getName());
        Member member = new Member(ownerUUID, MemberRole.PRESIDENTE.value());

        Pivot playerSpawnPivot = cityTemplate.getPlayerSpawn();
        SpawnPoint playerSpawn = SpawnPoint.builder()
                .X(lastPoint.getX() - playerSpawnPivot.getX())
                .Y(lastPoint.getY() + playerSpawnPivot.getY())
                .Z(lastPoint.getZ() + playerSpawnPivot.getZ())
                .world(lastPoint.getWorld())
                .type(SpawnPointType.PLAYER_SPAWN.value())
                .build();

        Pivot[] townHallPivot = cityTemplate.getTownHall();
        LocationZone townHall = LocationZone.builder()
                .minX(lastPoint.getX() - townHallPivot[0].getX())
                .minY(lastPoint.getY() + townHallPivot[0].getY())
                .minZ(lastPoint.getZ() + townHallPivot[0].getZ())
                .maxX(lastPoint.getX() - townHallPivot[1].getX())
                .maxY(lastPoint.getY() + townHallPivot[1].getY())
                .maxZ(lastPoint.getZ() + townHallPivot[1].getZ())
                .level(1)
                .world(lastPoint.getWorld())
                .type(BuildType.TOWN_HALL.value())
                .build();

        Pivot mainPivot = cityTemplate.getMain();
        LocationZone main = LocationZone.builder()
                .minX(lastPoint.getX())
                .minY(lastPoint.getY())
                .minZ(lastPoint.getZ())
                .maxX(lastPoint.getX() - mainPivot.getX())
                .maxY(lastPoint.getY() + mainPivot.getY())
                .maxZ(lastPoint.getZ() + mainPivot.getZ())
                .world(lastPoint.getWorld())
                .type(BuildType.MAIN.value())
                .build();

        Pivot[] wildZonePivot = cityTemplate.getWildZone();
        LocationZone wildZone = LocationZone.builder()
                .minX(lastPoint.getX() - wildZonePivot[0].getX())
                .minY(lastPoint.getY() + wildZonePivot[0].getY())
                .minZ(lastPoint.getZ() + wildZonePivot[0].getZ())
                .maxX(lastPoint.getX() - wildZonePivot[1].getX())
                .maxY(lastPoint.getY() + wildZonePivot[1].getY())
                .maxZ(lastPoint.getZ() + wildZonePivot[1].getZ())
                .world(lastPoint.getWorld())
                .type(BuildType.WILD_ZONE.value())
                .build();

        this.city = city;
        this.playerSpawn = playerSpawn;
        this.townHall = townHall;
        this.main = main;
        this.wildZone = wildZone;
        this.cityTemplate = cityTemplate;

        Member owner = new Member(ownerUUID, MemberRole.PRESIDENTE.value());
        members.put(ownerUUID, owner);
    }

    /**
     * (Constructor) When load City from database.
     * @param IDCity city id
     */
    public PlayerCity(int IDCity, MeteorCity plugin) {
        DatabaseInstance databaseInstance = plugin.getDatabaseInstance();
        members = new HashMap<>();
        disband = false;
        QueryInfo queryInfo = new QueryInfo("SELECT * FROM cities WHERE ID = :ID", null);
        queryInfo.addParameter("ID", IDCity);
        this.city = databaseInstance.fetchClassData(City.class, queryInfo).get(0);

        queryInfo = new QueryInfo("SELECT * FROM locations WHERE IDCity = :IDCity AND type = :type", null);
        queryInfo.addParameter("IDCity", IDCity);
        queryInfo.addParameter("type", BuildType.MAIN.value());
        this.main = databaseInstance.fetchClassData(LocationZone.class, queryInfo).get(0);

        queryInfo = new QueryInfo("SELECT * FROM locations WHERE IDCity = :IDCity AND type = :type", null);
        queryInfo.addParameter("IDCity", IDCity);
        queryInfo.addParameter("type", BuildType.TOWN_HALL.value());
        this.townHall = databaseInstance.fetchClassData(LocationZone.class, queryInfo).get(0);

        queryInfo = new QueryInfo("SELECT * FROM locations WHERE IDCity = :IDCity AND type = :type", null);
        queryInfo.addParameter("IDCity", IDCity);
        queryInfo.addParameter("type", BuildType.WILD_ZONE.value());
        this.wildZone = databaseInstance.fetchClassData(LocationZone.class, queryInfo).get(0);

        queryInfo = new QueryInfo("SELECT * FROM spawns WHERE IDCity = :IDCity AND type = :type", null);
        queryInfo.addParameter("IDCity", IDCity);
        queryInfo.addParameter("type", SpawnPointType.PLAYER_SPAWN.value());
        this.playerSpawn = databaseInstance.fetchClassData(SpawnPoint.class, queryInfo).get(0);

        List<Member> membersGet;
        queryInfo = new QueryInfo("SELECT * FROM members WHERE IDCity = :IDCity", null);
        queryInfo.addParameter("IDCity", IDCity);
        membersGet = databaseInstance.fetchClassData(Member.class, queryInfo);

        membersGet.forEach(member -> members.put(member.getUUID(), member));
        cityTemplate = plugin.getCityTemplates().get(city.getCityTemplate());
    }

    /**
     * Paste city schematic by template
     * @return true if pasted, false if not.
     */
    public boolean pasteCitySchematic() {
        return new Schematic(main.getMinX(), main.getMinY(), main.getMinZ(), main.getWorld(), cityTemplate.getSchematicFile()).paste();
    }

    /**
     * add new city into database.
     * @param databaseInstance database instance.
     * @return true if city was committed, false if not then rollback.
     */
    public boolean insertCityIntoDatabase(DatabaseInstance databaseInstance) {
        List<QueryInfo> queries = new ArrayList<>();
        int IDCity = databaseInstance.insertObjectDataInt( new QueryInfo("INSERT INTO cities (PIL, cityTemplate) VALUES (:PIL,:cityTemplate)", city));

        city.setID(IDCity);
        playerSpawn.setIDCity(IDCity);
        main.setIDCity(IDCity);
        wildZone.setIDCity(IDCity);
        townHall.setIDCity(IDCity);

        queries.add(new QueryInfo("INSERT INTO spawns (X,Y,Z,world,type,IDCity) VALUES (:x, :y, :z, :world, :type, :IDCity)", playerSpawn));
        queries.add(new QueryInfo("INSERT INTO locations (minX,minY,minZ,maxX,maxY,maxZ,world,type,IDCity) VALUES" +
                " (:minX,:minY,:minZ,:maxX,:maxY,:maxZ,:world,:type, :IDCity)", main));
        queries.add(new QueryInfo("INSERT INTO locations (minX,minY,minZ,maxX,maxY,maxZ,world,type,level,IDCity) VALUES " +
                "(:minX,:minY,:minZ,:maxX,:maxY,:maxZ,:world,:type, :level, :IDCity)", townHall));
        queries.add(new QueryInfo("INSERT INTO locations (minX,minY,minZ,maxX,maxY,maxZ,world,type,IDCity) VALUES " +
                "(:minX,:minY,:minZ,:maxX,:maxY,:maxZ,:world,:type,:IDCity)", wildZone));

        for (Member member : members.values()) {
            member.setIDCity(IDCity);
            queries.add(new QueryInfo("INSERT INTO members (UUID,role,IDCity) VALUES (:UUID,:role,:IDCity)", member));
        }
        if (!databaseInstance.executeQuery(queries.toArray(new QueryInfo[0])) || IDCity == 0) {
            databaseInstance.executeQuery(new QueryInfo("DELETE FROM cities WHERE ID = :ID", city));
            return false;
        }
        return true;
    }

    /**
     * remove city from database.
     * @param databaseInstance database instance.
     * @return true if city was committed, false if not then rollback.
     */
    public boolean removeCityFromDatabase(DatabaseInstance databaseInstance) {
        List<QueryInfo> queries = new ArrayList<>();
        queries.add(new QueryInfo("DELETE FROM locations WHERE IDCity = :ID", city));
        queries.add(new QueryInfo("DELETE FROM spawns WHERE IDCity = :ID", city));
        queries.add(new QueryInfo("DELETE FROM members WHERE IDCity = :ID", city));
        queries.add(new QueryInfo("DELETE FROM cities WHERE ID = :ID", city));
        return databaseInstance.executeQuery(queries.toArray(new QueryInfo[0]));
    }

    /**
     * Method for add new member to the city.
     * @param UUID of player will be a member.
     * @param memberRole member's role.
     */
    public void addMember(String UUID, MemberRole memberRole, MeteorCity plugin) {
        Member member = new Member(UUID, memberRole.value());
        member.setIDCity(city.getID());
        members.put(UUID, member);
        plugin.getCities().put(UUID, this);
    }

    /**
     * Check if a player is already a member of the city.
     * @param UUID of player that will be checked.
     * @return true if player is a member, false if not.
     */
    public boolean isMember(String UUID) {
        return members.containsKey(UUID);
    }

    /**
     * Get member's role.
     * @param UUID of player that will be checked.
     * @return member role.
     */
    public Member getMemberByUUID(String UUID) {
        return members.get(UUID);
    }

    /**
     * Update member's role
     * @param UUID of player
     * @param memberRole new role
     */
    public void updateMemberRole(String UUID, MemberRole memberRole) {
        for (Member member : members.values()) {
            if (member.getUUID().equals(UUID)) {
                member.setRole(memberRole.value());
                return;
            }
        }
    }

    /**
     * Send a message to all member's city
     * @param message which you want to send
     */
    public void sendMessageAllMembers(String message) {
        for (Player member : getAllMembersOnline()) {
            member.sendMessage(message);
        }
    }

    /**
     * Remove member from city
     * @param UUID member
     */
    public void removeMember(String UUID, MeteorCity plugin) {
        for (Member member : members.values()) {
            if (member.getUUID().equals(UUID)) {
                members.remove(UUID);
                plugin.getCities().remove(UUID);
            }
        }
    }

    /**
     * Get all member's online in the server.
     * @return a list with all member's online
     */
    public List<Player> getAllMembersOnline() {
        List<Player> membersOnline = new ArrayList<>();
        for (Member member : members.values()) {
            Player playerMember = Bukkit.getPlayer(UUID.fromString(member.getUUID()));
            if (playerMember != null) {
                membersOnline.add(playerMember);
            }
        }
        return membersOnline;
    }

    /**
     * Get member role's member
     * @param UUID member
     * @return MemberRole enum
     */
    public MemberRole getMemberRole(String UUID) {
        return EnumUtils.getEnum(MemberRole.class, members.get(UUID).getRole());
    }

    /**
     * Get a random member, with can a member excluded
     * @param UUID of member to be excluded
     * @return random member, exclude UUID
     */
    public Member getRandomMemberExcludeUUID(String UUID) {
        for (Member member : members.values()) {
            if (!member.getUUID().equals(UUID)) {
                return member;
            }
        }
        return null;
    }

    /**
     * Set new player by location
     * @param location new location
     */
    public void setNewPlayerSpawn(Location location) {
        playerSpawn.setX(location.getBlockX());
        playerSpawn.setY(location.getBlockY());
        playerSpawn.setZ(location.getBlockZ());
    }
}
