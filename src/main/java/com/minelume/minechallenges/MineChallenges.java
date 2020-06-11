package com.minelume.minechallenges;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.minelume.minechallenges.commands.*;
import com.minelume.minechallenges.core.friend.commands.FriendCommand;
import com.minelume.minechallenges.core.friend.listener.FriendListener;
import com.minelume.minechallenges.core.instances.ScoreboardCreator;
import com.minelume.minechallenges.database.MongoConnection;
import com.minelume.minechallenges.database.MongoDB;
import com.minelume.minechallenges.gameobjects.ChallengeGamerule;
import com.minelume.minechallenges.gameobjects.ChallengeScenario;
import com.minelume.minechallenges.gameobjects.ChallengeServer;
import com.minelume.minechallenges.listener.BlockListener;
import com.minelume.minechallenges.listener.EntityListener;
import com.minelume.minechallenges.listener.InventoryListener;
import com.minelume.minechallenges.listener.PlayerListener;
import com.minelume.minechallenges.utils.Storage;
import com.minelume.minechallenges.utils.UUIDFetcher;
import com.mongodb.client.model.Filters;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.BridgeHelper;
import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.wrapper.Wrapper;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MineChallenges extends JavaPlugin {

    public static ExecutorService executorService;

    public static IPlayerManager playerManager;
    public static CloudNetDriver driver;

    public File configFile;
    public FileConfiguration config;

    public String prefix;

    private static MineChallenges plugin;
    public MongoConnection mongoConnection;
    public MongoDB mongoDB;
    public Storage storage;
    public ScoreboardCreator scoreboardCreator;

    public UUID serverOwner;
    public ChallengeServer challengeServer;
    public ServerInstance serverInstance;

    @Override
    public void onEnable() {
        executorService = Executors.newSingleThreadExecutor();

        playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        driver = CloudNetDriver.getInstance();

        this.prefix = "§9Challenges §8» §7";

        MineChallenges.plugin = this;
        this.mongoConnection = new MongoConnection();
        this.mongoConnection.connect("127.0.0.1", "admin", "T6pZUjrRqz0l3LeHx65PQsAjn2uEyF8fmz" +
                "gJHxCzvUHFtNMyqWAPlqDmScOyweSZ", "admin");
        this.mongoDB = new MongoDB(this.mongoConnection, this.mongoConnection.getDatabase("Challenges"),
                this.mongoConnection.getCollection("Challenges", "Servers"));
        this.storage = new Storage();
        this.scoreboardCreator = new ScoreboardCreator(UUID.randomUUID());

        for (Document document : this.mongoDB.getDocumentsSync()) {
            ChallengeServer cs = ChallengeServer.unserialize(new Gson().fromJson(
                    document.getString("challengeServer"), HashMap.class));

            if (Wrapper.getInstance().getServiceId().getName().split("-")[1].equals(cs.getServerId())) {
                this.serverOwner = UUID.fromString(document.getString("uuid"));
                this.challengeServer = cs;
                break;
            }
        }

        if (this.challengeServer == null) {
            if (this.serverOwner != null) {
                ICloudPlayer cloudPlayer = playerManager.getOnlinePlayer(this.serverOwner);;

                if (cloudPlayer != null) {
                    playerManager.getPlayerExecutor(cloudPlayer).
                            sendChatMessage(this.prefix+"§cDein Challenge-Server konnte nicht gestartet werden!");
                }
            }

            Bukkit.shutdown();
            return;
        }

        BukkitCloudNetHelper.setMaxPlayers(this.challengeServer.getServerSlots());
        BukkitCloudNetHelper.setApiMotd("Challenge Server von "+ UUIDFetcher.getName(this.serverOwner));
        BridgeHelper.updateServiceInfo();

        for (ChallengeScenario challengeScenario : ChallengeScenario.values()) {
            if (this.challengeServer.getChallengeScenarios().containsKey(challengeScenario.name())) {
                challengeScenario.setActive(this.challengeServer.getChallengeScenarios().get(challengeScenario.name()));
            }
        }

        for (ChallengeGamerule challengeGamerule : ChallengeGamerule.values()) {
            if (this.challengeServer.getChallengeGamerules().containsKey(challengeGamerule.name())) {
                challengeGamerule.setActive(this.challengeServer.getChallengeGamerules().get(challengeGamerule.name()));
            }
        }

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doMobLoot", ""+ ChallengeGamerule.DO_MOB_LOOT.isActive());
            world.setGameRuleValue("mobGriefing", ""+ ChallengeGamerule.DO_MOB_LOOT.isActive());
            world.setGameRuleValue("naturalRegeneration", ""+ (!ChallengeScenario.NO_NATURAL_REGENERATION.isActive()));
            world.setDifficulty(this.challengeServer.getDifficulty());
        }

        this.getDataFolder().mkdirs();
        this.configFile = new File(this.getDataFolder(), "cache.yml");
        try {
            if (this.configFile.createNewFile()) {
                this.config = YamlConfiguration.loadConfiguration(this.configFile);
                this.config.set("owner", this.serverOwner.toString());
                this.config.set("deadPlayers", new ArrayList<String>());
                this.config.set("coords", new Gson().toJson(new HashMap<String, String>()));
                this.config.set("timer", 0);
                this.config.set("reviveUses", 0);
                this.config.set("killedDragon", false);
                this.config.set("addedPlayers", new ArrayList<String>());

                //long delete = Long.valueOf(System.currentTimeMillis() + 1209600000);
                //this.config.set("delete", delete);
                this.config.save(this.configFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);

        /**if (this.config.getLong("delete") >= System.currentTimeMillis()) {
            MineChallenges.driver.getMessenger().sendChannelMessage("challenges",
                    "delete "+ Wrapper.getInstance().getServiceId().getName(), new JsonDocument());
            this.plugin.executorService.submit(() -> this.plugin.mongoDB.getCollection().
                    deleteOne(Filters.eq("uuid", serverOwner.toString())));

            ICloudPlayer cloudPlayer = playerManager.getOnlinePlayer(serverOwner);
            if (cloudPlayer != null) {
                cloudPlayer.getPlayerExecutor().sendChatMessage(this.prefix+"§cDie Serverlauftzeit ist abgelaufen," +
                        " und dein Server wurde gelöscht!");
            }

            Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.shutdown(), 3*20L);
            return;
        }**/

        LinkedTreeMap<String, String> map = new Gson().fromJson(this.config.getString("coords"), LinkedTreeMap.class);
        HashMap<String, String> coords = new HashMap<>();
        for (String name : map.keySet()) coords.put(name, map.get(name));

        this.serverInstance = new ServerInstance(this.config.getStringList("deadPlayers"),
                coords, this.config.getInt("timer"));

        this.getCommand("challenges").setExecutor(new Challenges_CMD(this));
        this.getCommand("coords").setExecutor(new Coords_CMD(this));
        this.getCommand("health").setExecutor(new Health_CMD(this));
        this.getCommand("timer").setExecutor(new Timer_CMD(this));
        this.getCommand("revive").setExecutor(new Revive_CMD(this));
        this.getCommand("time").setExecutor(new Time_CMD(this));
        this.getCommand("help").setExecutor(new Help_CMD(this));
        this.getCommand("add").setExecutor(new Whitelist_CMD(this));
        this.getCommand("remove").setExecutor(new Whitelist_CMD(this));
        this.getCommand("friend").setExecutor(new FriendCommand(this));

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new EntityListener(this), this);
        pluginManager.registerEvents(new InventoryListener(this), this);
        pluginManager.registerEvents(new FriendListener(this), this);

        super.onEnable();
    }

    public static MineChallenges getInstance() {
        return plugin;
    }
}
