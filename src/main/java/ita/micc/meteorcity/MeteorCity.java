package ita.micc.meteorcity;

import ita.micc.meteorcity.citytemplate.CityTemplate;
import ita.micc.meteorcity.citytemplate.utils.CityTemplateUtils;
import ita.micc.meteorcity.commands.admincommands.cityadmin.CityAdminCommand;
import ita.micc.meteorcity.commands.usercommands.city.CityCommand;
import ita.micc.meteorcity.database.DatabaseInstance;
import ita.micc.meteorcity.database.bindclass.SpawnPoint;
import ita.micc.meteorcity.database.config.MySQL;
import ita.micc.meteorcity.database.query.QueryInfo;
import ita.micc.meteorcity.enums.SpawnPointType;
import ita.micc.meteorcity.listener.PlayerJoinInits;
import ita.micc.meteorcity.playercity.PlayerCity;
import ita.micc.meteorcity.playercity.PlayerCityInvite;
import ita.micc.meteorcity.worldutils.EmptyChunkGenerator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**  Controllare la class schematic, fare un oggetto. Controllare se togliere city_in_disband, fare che non si può impostare presidente,
 * MeteorCity's main
 * @author Codeh.
 * @version 1.0
 */
public final class MeteorCity extends JavaPlugin {

    private @Getter DatabaseInstance databaseInstance;
    private @Getter HashMap<String, CityTemplate> cityTemplates;
    private @Getter HashMap<String, PlayerCity> cities;
    private @Getter HashMap<String, PlayerCityInvite> invites;
    private @Getter SpawnPoint lastPoint;

    @Override
    public void onEnable() {
        cityTemplates = new HashMap<>();
        cities = new HashMap<>();
        invites = new HashMap<>();
        try {
            saveDefaultConfig();
            importAllTemplates();
            loadCitiesWorld();
            initDatabase();
            loadLastPoint();
            showLastPointInLog();
            registerEvents();
            registerCommands();
            getLogger().info("Plugin avviato con successo.");
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            getLogger().info("Errore durante il caricamento del plugin, spegnimento in corso..");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("MeteorCity è stato disattivato, kick in corso di tutti i player.."));
        getLogger().info("Plugin disabilitato con successo.");
    }

    /**
     * register commands
     */
    public void registerCommands() throws NullPointerException {
        CityCommand cityCommand = new CityCommand(null, true, this);
        CityAdminCommand cityAdminCommand = new CityAdminCommand("cityadmin.admin", false, this);
        Objects.requireNonNull(getCommand("city")).setExecutor(cityCommand);
        Objects.requireNonNull(getCommand("cityadmin")).setExecutor(cityAdminCommand);
    }

    /**
     * register events
     */
    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinInits(this), this);
    }

    /**
     * reload templates.
     */
    public void reloadTemplates() {
        saveDefaultConfig();
        cityTemplates.clear();
        importAllTemplates();
    }

    /**
     * Check if cities world exist, if not create.
     */
    public void loadCitiesWorld() {
        if (!(Bukkit.getWorld("Cities") == null)) {
            getLogger().info("world Cities caricato.");
            return;
        }
        getLogger().info("Creazione mondo Cities..");
        WorldCreator wc = new WorldCreator("Cities");
        wc.generator(new EmptyChunkGenerator());
        Bukkit.getWorlds().add(wc.createWorld());
        getLogger().info("world Cities creato.");
    }

    /**
     * import all templates from config.
     */
    public void importAllTemplates() throws NullPointerException {
        Set<String> configCityTemplates = Objects.requireNonNull(getConfig().getConfigurationSection("templates")).getKeys(false);
         if (configCityTemplates.isEmpty()) {
            return;
        }
        for (String templateName : configCityTemplates) {
            try {
                ConfigurationSection section =
                        Objects.requireNonNull(getConfig().getConfigurationSection("templates." + templateName));
                String schematicFileName = Objects.requireNonNull(section.getString("schematicFileName"));
                String permissionRequired = Objects.requireNonNull(section.getString("permissionRequired"));

                CityTemplate cityTemplate =
                        new CityTemplate(templateName.toUpperCase(), schematicFileName, getDataFolder().getAbsolutePath(),
                                section.getString("permissionRequired"));

                cityTemplate.setMain(CityTemplateUtils.calculateSinglePoint(section, "main"));
                cityTemplate.setPlayerSpawn(CityTemplateUtils.calculateSinglePoint(section, "playerSpawn"));
                cityTemplate.setTownHall(CityTemplateUtils.calculateMultiplePoint(section, "townHall"));
                cityTemplate.setWildZone(CityTemplateUtils.calculateMultiplePoint(section, "wildZone"));

                cityTemplates.put(templateName.toUpperCase(), cityTemplate);
                getLogger().info(templateName + " è stato caricato.");
            } catch (NullPointerException | FileNotFoundException e){
                getLogger().info("Errore durante il caricamento del template " + templateName);
                e.printStackTrace();
            }
        }
    }

    /**
     * Init database connection.
     * @throws SQLException if cannot establish connection with database.
     */
    public void initDatabase() throws SQLException {
        ConfigurationSection config = getConfig();
        MySQL mySQL = new MySQL(config.getString("database.hostname"),
                config.getInt("database.port"), config.getString("database.username"),
                config.getString("database.password"), config.getString("database.database"));
        databaseInstance = new DatabaseInstance(mySQL);
    }

    /**
     * get lastpoint from database.
     * @throws NullPointerException if lastpoint is null
     * @throws SQLException if cannot establish connection with database.
     */
    public void loadLastPoint() throws SQLException, NullPointerException {
        QueryInfo queryInfo = new QueryInfo("SELECT * FROM spawns WHERE type = :type", null);
        queryInfo.addParameter("type", SpawnPointType.LAST_POINT.value());

        lastPoint = Objects.requireNonNull(databaseInstance.fetchClassData(SpawnPoint.class, queryInfo).get(0));
    }

    /**
     * Increase current last point.
     * @return false if cannot increase lastpoint, true if lastpoint was increased.
     */
    public boolean increaseLastPoint() {
        lastPoint.setZ(lastPoint.getZ() + 1000);
        QueryInfo queryInfo = new QueryInfo("UPDATE spawns SET Z = :z WHERE type = :type", lastPoint);
        return databaseInstance.executeQuery(queryInfo);
    }

    /**
     * Show lastpoint in console.
     */
    public void showLastPointInLog() {
        getLogger().info("====================");
        getLogger().info("LastPoint:");
        getLogger().info("X: " + lastPoint.getX());
        getLogger().info("Y: " + lastPoint.getY());
        getLogger().info("Z: " + lastPoint.getZ());
        getLogger().info("world: " + lastPoint.getWorld());
        getLogger().info("=====================");
    }
}
