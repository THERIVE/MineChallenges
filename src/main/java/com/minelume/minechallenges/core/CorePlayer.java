package com.minelume.minechallenges.core;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.friend.FriendPlayer;
import com.minelume.minechallenges.core.permission.PermissionPlayer;
import com.minelume.minechallenges.database.MongoDB;
import org.bson.Document;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public class CorePlayer {

    private MineChallenges core;

    private UUID uuid;
    private Document mongoDocument;

    private PermissionPlayer permissionPlayer;
    private FriendPlayer friendPlayer;

    private int coins;
    private boolean internalMessage;


    private CorePlayer(MineChallenges core, UUID uuid) {
        this.core = core;

        this.uuid = uuid;
        this.permissionPlayer = new PermissionPlayer(this.uuid);

        this.coins = 0;
        this.internalMessage = true;
    }

    public void uploadToDatabase(Consumer<CorePlayer> finish) {
        if (this.mongoDocument == null) this.mongoDocument = new Document("uuid", uuid.toString());

        this.mongoDocument.append("coins", this.coins);
        this.mongoDocument.append("internalMessages", this.internalMessage);

        MongoDB mongoDB = new MongoDB(this.core.getInstance().mongoConnection,
                this.core.getInstance().mongoConnection.getDatabase("Core"),
                this.core.getInstance().mongoConnection.getCollection("Core", "Players"));
        mongoDB.getDocumentAsync("uuid", uuid.toString(), (a -> {
            if (a == null) {
                mongoDB.insertDocumentAsync(this.mongoDocument, (i -> finish.accept(this)));
                return;
            }
            mongoDB.updateDocumentAsync("uuid", this.mongoDocument, (u -> finish.accept(this)));
            return;
        }));
    }

    public static void findByUUID(UUID uuid, Consumer<CorePlayer> consumer) {
        if (!MineChallenges.getInstance().storage.getCorePlayers().containsKey(uuid)) {
            MongoDB mongoDB = new MongoDB(MineChallenges.getInstance().mongoConnection,
                    MineChallenges.getInstance().mongoConnection.getDatabase("Core"),
                    MineChallenges.getInstance().mongoConnection.getCollection("Core", "Players"));

            mongoDB.getDocumentAsync("uuid", String.valueOf(uuid), (document -> {
                if (document == null) {
                    CorePlayer corePlayer = new CorePlayer(MineChallenges.getInstance(), uuid);

                    document = new Document("uuid", uuid.toString());
                    document.append("coins", 0);
                    document.append("internalMessages", true);

                    Document friendDocument = new Document();
                    friendDocument.append("friends", new ArrayList<>());
                    friendDocument.append("requests", new ArrayList<>());
                    friendDocument.append("settings", new Document());

                    Document nickDocument = new Document("autoNick", "false");
                    nickDocument.append("nickName", "null");

                    document.append("nick", nickDocument);
                    document.append("friendPlayer", friendDocument);
                    corePlayer.setMongoDocument(document);
                    corePlayer.setFriendPlayer(new FriendPlayer(corePlayer));
                    corePlayer.setCoins(0);
                    MineChallenges.getInstance().storage.getCorePlayers().put(uuid, corePlayer);


                    consumer.accept(corePlayer);
                    mongoDB.insertDocumentAsync(document, (i -> {}));
                    return;
                }

                CorePlayer corePlayer = new CorePlayer(MineChallenges.getInstance(), uuid);
                corePlayer.setInternalMessage(document.getBoolean("internalMessages"));
                corePlayer.setMongoDocument(document);
                corePlayer.setFriendPlayer(new FriendPlayer(corePlayer));
                corePlayer.setCoins(document.getInteger("coins"));

                MineChallenges.getInstance().storage.getCorePlayers().put(uuid, corePlayer);
                consumer.accept(corePlayer);
            }));
        } else consumer.accept(MineChallenges.getInstance().storage.getCorePlayers().get(uuid));
    }

    public void addCoins(int coins) {
        this.coins += coins;
        this.uploadToDatabase(corePlayer -> {});
    }

    public void removeCoins(int coins) {
        this.coins -= coins;
        if (this.coins < 0) this.coins = 0;
        this.uploadToDatabase(corePlayer -> {});
    }

    public void setFriendPlayer(FriendPlayer friendPlayer) {
        this.friendPlayer = friendPlayer;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setInternalMessage(boolean internalMessage) {
        this.internalMessage = internalMessage;
    }

    public void setPermissionPlayer(PermissionPlayer permissionPlayer) {
        this.permissionPlayer = permissionPlayer;
    }

    public void setMongoDocument(Document mongoDocument) {
        this.mongoDocument = mongoDocument;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getCoins() {
        return coins;
    }

    public boolean internalMessage() {
        return internalMessage;
    }

    public FriendPlayer getFriendPlayer() {
        return friendPlayer;
    }

    public PermissionPlayer getPermissionPlayer() {
        return permissionPlayer;
    }

    public Document getMongoDocument() {
        return mongoDocument;
    }

}
