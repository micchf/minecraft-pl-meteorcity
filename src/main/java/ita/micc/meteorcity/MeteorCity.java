package ita.micc.meteorcity;

import ita.micc.meteorcity.benchmark.Benchmark;
import ita.micc.meteorcity.buildsettings.BuildSettings;
import ita.micc.meteorcity.citytemplate.CityTemplate;
import ita.micc.meteorcity.citytemplate.Pivot;
import ita.micc.meteorcity.citytemplate.utils.CityTemplateUtils;
import ita.micc.meteorcity.commands.admincommands.cityadmin.CityAdminCommand;
import ita.micc.meteorcity.commands.usercommands.city.CityCommand;
import ita.micc.meteorcity.database.DatabaseInstance;
import ita.micc.meteorcity.database.bindclass.SpawnPoint;
import ita.micc.meteorcity.database.config.MySQL;
import ita.micc.meteorcity.database.query.QueryInfo;
import ita.micc.meteorcity.enums.BuildType;
import ita.micc.meteorcity.enums.SpawnPointType;
import ita.micc.meteorcity.listener.EventsOnClickBuild;
import ita.micc.meteorcity.listener.PlayerJoinInits;
import ita.micc.meteorcity.placeholder.Test;
import ita.micc.meteorcity.playercity.PlayerCity;
import ita.micc.meteorcity.buildsettings.blockpaste.BlockPaste;
import ita.micc.meteorcity.playercity.invite.PlayerCityInvite;
import ita.micc.meteorcity.world.EmptyChunkGenerator;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**  Fare custom events, on shutdown save all database to database city leave fare settings city setvisit, city kick, city vist, city acceptvisit, sistemare i message, vedere se mettere nei metodo update member
 * MeteorCity's main               la variabile con la lista di tutte le città, remove in playerinitsevent asincrono se tolgo async in playerinitsevent posso togliere city in load
 * @author Codeh.
 * @version 1.0
 */
@Getter
public final class MeteorCity extends JavaPlugin {

    private DatabaseInstance databaseInstance;
    private HashMap<String, CityTemplate> cityTemplates;
    private HashMap<String, PlayerCity> cities;
    private HashMap<String, PlayerCityInvite> invites;
    private HashMap<BuildType, BuildSettings> buildSettings;
    private SpawnPoint lastPoint;
    private String cityWorldName;

    @Override
    public void onEnable() {
        cityTemplates = new HashMap<>();
        cities = new HashMap<>();
        invites = new HashMap<>();
        buildSettings = new HashMap<>();
        cityWorldName = "Cities";
        try {
            saveDefaultConfig();
            importAllTemplates();
            loadBuildSettings();
            loadCitiesWorld();
            startBenchmark();
            initDatabase();
            loadLastPoint();
            showLastPointInLog();
            registerEvents();
            registerCommands();
            loadPlaceHolders();
            getLogger().info("Plugin avviato con successo.");
        } catch (SQLException | NullPointerException | IllegalArgumentException e) {
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
    private void registerCommands() throws NullPointerException {
        CityCommand cityCommand = new CityCommand(null, true, this);
        CityAdminCommand cityAdminCommand = new CityAdminCommand("cityadmin.admin", false, this);
        Objects.requireNonNull(getCommand("city")).setExecutor(cityCommand);
        Objects.requireNonNull(getCommand("cityadmin")).setExecutor(cityAdminCommand);
    }

    /**
     * register events
     */
    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinInits(this), this);
        Bukkit.getPluginManager().registerEvents(new EventsOnClickBuild(this), this);
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
    private void loadCitiesWorld() {
        if (!(Bukkit.getWorld(cityWorldName) == null)) {
            return;
        }
        WorldCreator wc = new WorldCreator(cityWorldName);
        wc.generator(new EmptyChunkGenerator());
        Bukkit.getWorlds().add(wc.createWorld());
    }

    /**
     * Register placeholders for PlaceholderAPI
     */
    private void loadPlaceHolders() {
        new Test(this).register();
    }

    /**
     * import all templates from config.
     */
    private void importAllTemplates() throws NullPointerException {
        Set<String> configCityTemplates = Objects.requireNonNull(getConfig().getConfigurationSection("templates")).getKeys(false);
         if (configCityTemplates.isEmpty()) {
            return;
        }
         getLogger().info("====================================");
         getLogger().info("Caricamento dei template in corso..");
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

                ConfigurationSection zonesSection =
                        Objects.requireNonNull(section.getConfigurationSection("zones"));

                for (String count : zonesSection.getKeys(false)) {
                    Pivot[] pivotTemp = CityTemplateUtils.calculateMultiplePoint(zonesSection, count);
                    cityTemplate.getZones().add(pivotTemp);
                }

                cityTemplates.put(templateName.toUpperCase(), cityTemplate);
                getLogger().info(templateName + " è stato caricato.");
            } catch (NullPointerException | FileNotFoundException e){
                getLogger().info("Errore durante il caricamento del template " + templateName);
                e.printStackTrace();
            }
        }
        getLogger().info("Caricamento template completato.");
        getLogger().info("====================================");
    }

    /**
     * Load buildsettings for city from config
     * @throws NullPointerException if section is null
     * @throws IllegalArgumentException is enum is null
     */
    private void loadBuildSettings() throws NullPointerException, IllegalArgumentException {
        getLogger().info("====================================");
        getLogger().info("Caricamento BuildSettings in corso..");
        Set<String> configBlockPastes = Objects.requireNonNull(getConfig().getConfigurationSection("settings")).getKeys(false);
        for (String blockPasteSection : configBlockPastes) {
            ConfigurationSection section =
                    Objects.requireNonNull(getConfig().getConfigurationSection("settings." + blockPasteSection));

            String displayName = Objects.requireNonNull(section.getString("blockPaste.displayName"));
            List<String> lore = Objects.requireNonNull(section.getStringList("blockPaste.lore"));
            BuildType buildType = BuildType.valueOf(section.getName());
            Material material = Material.valueOf(section.getString("blockPaste.material"));

            String guiName = Objects.requireNonNull(getConfig().getString("settings." + blockPasteSection + ".guiName"));
            BlockPaste blockPaste = new BlockPaste(displayName, lore, buildType, material);

            BuildSettings buildSetting = new BuildSettings(blockPaste, guiName);
            buildSettings.put(buildType, buildSetting);
            getLogger().info("Settings " + buildType.value() + " è stato caricato.");
        }
        getLogger().info("====================================");
    }

    /**
     * Start benchmark
     */
    private void startBenchmark() {
        getLogger().info("====================================");
        getLogger().info("Avvio Benchmark..");
        getLogger().info("Tempo di esecuzione: " + new Benchmark().start(100_000));
        getLogger().info("====================================");
    }

    /**
     * Init database connection.
     * @throws SQLException if cannot establish connection with database.
     */
    private void initDatabase() throws SQLException {
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
    private void loadLastPoint() throws SQLException, NullPointerException {
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
