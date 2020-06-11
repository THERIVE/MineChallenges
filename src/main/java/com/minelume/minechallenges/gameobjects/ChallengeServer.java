package com.minelume.minechallenges.gameobjects;

import com.destroystokyo.paper.Title;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.CorePlayer;
import com.minelume.minechallenges.database.MongoDB;
import com.mongodb.client.model.Filters;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.*;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChallengeServer {

    private MongoDB mongoDB;

    private String serverId;

    private UUID owner;
    private List<UUID> deadPlayers;

    private HashMap<String, Boolean> challengeScenarios;
    private HashMap<String, Boolean> challengeGamerules;
    private Integer playedTime;
    private Difficulty difficulty;
    private Integer serverSlots;

    public ChallengeServer(UUID owner) {
        this.mongoDB = new MongoDB(MineChallenges.getInstance().mongoConnection,
                MineChallenges.getInstance().mongoConnection.getDatabase("Challenges"),
                MineChallenges.getInstance().mongoConnection.getCollection("Challenges", "Servers"));

        File dir = new File("/home/minecraft/Cloud/local/services");
        List<File> dirs = new ArrayList<>();
        for (File file : dir.listFiles()) if (file.getName().contains("Challenges")) dirs.add(file);
        Collections.sort(dirs, Comparator.comparing(File::getName));


        this.serverId = ""+Integer.valueOf(dirs.size() + 1);
        this.owner = owner;
        this.deadPlayers = new ArrayList<>();

        this.challengeScenarios = new HashMap<>();
        for (ChallengeScenario challengeScenario : ChallengeScenario.values())
            this.challengeScenarios.put(challengeScenario.name(), challengeScenario.isActive());
        this.challengeGamerules = new HashMap<>();
        for (ChallengeGamerule challengeGamerule : ChallengeGamerule.values())
            this.challengeGamerules.put(challengeGamerule.name(), challengeGamerule.isActive());
        this.playedTime = 0;
        this.difficulty = Difficulty.EASY;

        this.serverSlots = 5;
    }

    public ChallengeServer(String serverId, UUID owner, List<UUID> deadPlayers, HashMap<String, Boolean> challengeScenarios,
                           HashMap<String, Boolean> challengeGamerules, Integer playedTime, Difficulty difficulty, Integer serverSlots) {
        this.mongoDB = new MongoDB(MineChallenges.getInstance().mongoConnection,
                MineChallenges.getInstance().mongoConnection.getDatabase("Challenges"),
                MineChallenges.getInstance().mongoConnection.getCollection("Challenges", "Servers"));

        this.serverId = serverId;
        this.owner = owner;
        this.deadPlayers = deadPlayers;

        this.challengeScenarios = challengeScenarios;
        this.challengeGamerules = challengeGamerules;
        this.playedTime = playedTime;
        this.difficulty = difficulty;
        this.serverSlots = serverSlots;
    }

    public void startServer() {
        CorePlayer.findByUUID(this.owner, corePlayer -> {
            if (!CloudNetDriver.getInstance().getServiceTaskProvider().isServiceTaskPresent("Challenges"))
                CloudNetDriver.getInstance().getServiceTaskProvider().addPermanentServiceTask(new ServiceTask(
                        new ArrayList<>(), //includes
                        new ArrayList<>(Collections.singletonList(
                                new ServiceTemplate(
                                        "Challenges",
                                        "default",
                                        "local"
                                )
                        )), //templates
                        new ArrayList<>(), //deployments
                        "Challenges", //name
                        null, //runtime can be null for the default jvm wrapper or "jvm"
                        true, //autoDeleteOnStop => if the service stops naturally it will be automatic deleted
                        true, //The service won't be deleted fully and will store in the configured directory. The default is /local/services
                        new ArrayList<>(), //node ids
                        new ArrayList<>(Collections.singletonList("Challenges")), //groups
                        new ProcessConfiguration(
                                ServiceEnvironmentType.MINECRAFT_SERVER, //environement type
                                1024, //max heap memory size
                                new ArrayList<>()
                        ),
                        4000, //start port
                        0 //min services count with auto creation
                ));

            boolean restart = false;

            if (MineChallenges.driver.getCloudServiceProvider().getCloudServiceByName("Challenges-"+this.serverId) != null) {
                if (MineChallenges.driver.getCloudServiceProvider().getCloudServiceByName("Challenges-"+this.serverId).isConnected()) {
                    CloudNetDriver.getInstance().getNodeInfoProvider().
                            sendCommandLineAsync("service Challenges-"+this.serverId+" restart");
                    restart = true;
                } else CloudNetDriver.getInstance().getNodeInfoProvider().
                        sendCommandLineAsync("service Challenges-"+this.serverId+" start");
            } else CloudNetDriver.getInstance().getNodeInfoProvider().
                    sendCommandLineAsync("service Challenges-"+this.serverId+" start");

            final String[] animation = {"▪"};
            AtomicInteger wait = new AtomicInteger(restart ? 5 : 0);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (wait.get() == 0) {
                        ServiceInfoSnapshot serviceInfoSnapshot = MineChallenges.driver.getCloudServiceProvider().
                                getCloudServiceByName("Challenges-"+serverId);

                        if (serviceInfoSnapshot != null) {
                            if (serviceInfoSnapshot.isConnected()) {
                                try {
                                    if (serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_ONLINE).get()) {
                                        MineChallenges.playerManager.getPlayerExecutor(corePlayer.getUuid()).connect(serviceInfoSnapshot.getName());
                                        MineChallenges.playerManager.getPlayerExecutor(corePlayer.getUuid()).
                                                sendChatMessage(MineChallenges.getInstance().prefix+"§7Baue Verbindung zu §e"+serviceInfoSnapshot.getName()+
                                                        "§7 auf...");
                                        cancel();
                                    }
                                } catch (NoSuchElementException e) {
                                }
                            }
                        }
                    } else wait.set(wait.get() - 1);

                    int count = 0;
                    for (int i = 0; i < animation[0].length(); i++) {
                        if (animation[0].charAt(i) == '▪') count++;
                    }

                    if (count < 3) {
                        animation[0] += "▪";
                    } else animation[0] = "▪";

                    Player player = Bukkit.getPlayer(corePlayer.getUuid());
                    if (player != null) {
                        player.sendTitle(Title.builder()
                                .fadeIn(0)
                                .stay(30)
                                .fadeOut(0)
                                .subtitle("§9Challenges")
                                .title("§e"+ animation[0])
                                .build());
                    }
                }
            }.runTaskTimerAsynchronously(MineChallenges.getInstance(), 0L, 20L);
        });
    }

    public HashMap<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        List<String> dead = new ArrayList<>();
        for (UUID uuid : this.deadPlayers) dead.add(uuid.toString());

        map.put("serverId", this.serverId);
        map.put("owner", this.owner.toString());
        map.put("deadPlayers", dead);
        map.put("challengeScenarios", this.challengeScenarios);
        map.put("playedTime", this.playedTime);
        map.put("challengeGamerules", this.challengeGamerules);
        map.put("difficulty", this.difficulty.name());
        map.put("serverSlots", this.serverSlots);

        return map;
    }

    public static ChallengeServer unserialize(HashMap<String, Object> map) {
        ChallengeServer challengeServer = new ChallengeServer(UUID.fromString((String) map.get("owner")));
        challengeServer.setServerId((String) map.get("serverId"));
        List<UUID> deadPlayers = new ArrayList<>();

        for (String uuid : (ArrayList<String>) map.get("deadPlayers"))
            deadPlayers.add(UUID.fromString(uuid));

        challengeServer.setDeadPlayers(deadPlayers);

        LinkedTreeMap<String, Boolean> linkedScenarios = (LinkedTreeMap<String, Boolean>) map.get("challengeScenarios");
        HashMap<String, Boolean> scenarios = new HashMap<>();
        for (String name : linkedScenarios.keySet()) scenarios.put(name, linkedScenarios.get(name));
        challengeServer.setChallengeScenarios(scenarios);

        LinkedTreeMap<String, Boolean> linkedGamerules = (LinkedTreeMap<String, Boolean>) map.get("challengeGamerules");
        HashMap<String, Boolean> gamerules = new HashMap<>();
        for (String name : linkedGamerules.keySet()) gamerules.put(name, linkedGamerules.get(name));
        challengeServer.setChallengeGamerules(gamerules);

        double pT = ((Double) map.get("playedTime"));
        int playedTime = (int) pT;
        challengeServer.setPlayedTime(playedTime);
        challengeServer.setDifficulty(Difficulty.valueOf((String) map.get("difficulty")));

        double sS = ((Double) map.get("serverSlots"));
        int serverSlots = (int) sS;
        challengeServer.setServerSlots(serverSlots);

        return challengeServer;
    }

    public void save(String uuid) {
        org.bson.Document document = new org.bson.Document("uuid", uuid);
        document.append("challengeServer",new Gson().toJson(this.serialize()));

        if (this.mongoDB.getDocumentSync("uuid", uuid) != null) {
            this.mongoDB.getCollection().deleteOne(Filters.eq("uuid", uuid));

            Bukkit.getScheduler().runTaskLaterAsynchronously(MineChallenges.getInstance(), () ->
                    this.mongoDB.insertDocumentSync(document), 20L);
            return;
        }

        this.mongoDB.insertDocumentAsync(document, i -> {});
    }

    public UUID getOwner() {
        return owner;
    }

    public HashMap<String, Boolean> getChallengeScenarios() {
        return challengeScenarios;
    }

    public List<UUID> getDeadPlayers() {
        return deadPlayers;
    }

    public Integer getPlayedTime() {
        return playedTime;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Integer getServerSlots() {
        return serverSlots;
    }

    public HashMap<String, Boolean> getChallengeGamerules() {
        return challengeGamerules;
    }

    public String getServerId() {
        return serverId;
    }

    public void setChallengeScenarios(HashMap<String, Boolean> challengeScenarios) {
        this.challengeScenarios = challengeScenarios;
    }

    public void setDeadPlayers(List<UUID> deadPlayers) {
        this.deadPlayers = deadPlayers;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setPlayedTime(Integer playedTime) {
        this.playedTime = playedTime;
    }

    public void setServerSlots(Integer serverSlots) {
        this.serverSlots = serverSlots;
    }

    public void setChallengeGamerules(HashMap<String, Boolean> challengeGamerules) {
        this.challengeGamerules = challengeGamerules;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
}
