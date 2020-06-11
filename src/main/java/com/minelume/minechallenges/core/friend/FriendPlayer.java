package com.minelume.minechallenges.core.friend;

import com.minelume.minechallenges.core.CorePlayer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendPlayer {

    private UUID uuid;
    private CorePlayer corePlayer;

    private List<String> friends;
    private List<String> requests;
    private Document settings;

    public FriendPlayer(CorePlayer corePlayer) {
        this.corePlayer = corePlayer;

        this.uuid = corePlayer.getUuid();

        if (corePlayer.getMongoDocument() != null) {
            if (corePlayer.getMongoDocument().get("friendPlayer") != null) {
                this.friends = ((Document) corePlayer.getMongoDocument().get("friendPlayer")).getList("friends", String.class);
                this.requests = ((Document) corePlayer.getMongoDocument().get("friendPlayer")).getList("requests", String.class);
                this.settings = (Document) ((Document) corePlayer.getMongoDocument().get("friendPlayer")).get("settings");
                return;
            }
        }

        this.friends = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.settings = new Document();

        Document document = new Document();
        document.append("friends", this.friends);
        document.append("requests", this.requests);
        document.append("settings", this.settings);

        this.corePlayer.getMongoDocument().append("friendPlayer", document);
        this.corePlayer.setMongoDocument(this.corePlayer.getMongoDocument());
    }

    public void addFriend(UUID friend) {
        List<String> friends = this.getFriends();
        if (!friends.contains(friend.toString())) friends.add(friend.toString());

        Document document = (Document) this.corePlayer.getMongoDocument().get("friendPlayer");
        document.append("friends", friends);
        this.corePlayer.getMongoDocument().append("friendPlayer", document);
        this.corePlayer.setMongoDocument(this.corePlayer.getMongoDocument());
        this.corePlayer.uploadToDatabase(f -> {});
    }

    public void removeFriend(UUID friend) {
        List<String> friends = this.getFriends();
        if (friends.contains(friend.toString())) friends.remove(friend.toString());

        Document document = (Document) this.corePlayer.getMongoDocument().get("friendPlayer");
        document.append("friends", friends);
        this.corePlayer.getMongoDocument().append("friendPlayer", document);
        this.corePlayer.setMongoDocument(this.corePlayer.getMongoDocument());
        this.corePlayer.uploadToDatabase(f -> {});
    }

    public void addRequests(UUID request) {
        List<String> requests = this.getRequests();
        if (!requests.contains(request.toString())) requests.add(request.toString());

        Document document = (Document) this.corePlayer.getMongoDocument().get("friendPlayer");
        document.append("requests", requests);
        this.corePlayer.getMongoDocument().append("friendPlayer", document);
        this.corePlayer.setMongoDocument(this.corePlayer.getMongoDocument());
        this.corePlayer.uploadToDatabase(f -> {});
    }

    public void removeRequests(UUID request) {
        List<String> requests = this.getRequests();
        if (requests.contains(request.toString())) requests.remove(request.toString());

        Document document = (Document) this.corePlayer.getMongoDocument().get("friendPlayer");
        document.append("requests", requests);
        this.corePlayer.getMongoDocument().append("friendPlayer", document);
        this.corePlayer.setMongoDocument(this.corePlayer.getMongoDocument());
        this.corePlayer.uploadToDatabase(f -> {});
    }

    public void setSetting(String setting, boolean value) {
        this.settings.append(setting, value);

        Document document = (Document) this.corePlayer.getMongoDocument().get("friendPlayer");
        document.append("settings", this.settings);
        this.corePlayer.getMongoDocument().append("friendPlayer", document);
        this.corePlayer.setMongoDocument(this.corePlayer.getMongoDocument());
        this.corePlayer.uploadToDatabase(f -> {});
    }

    public boolean isFriend(UUID friend) {
        return this.friends.contains(friend.toString());
    }

    public boolean hasRequest(UUID request) {
        return this.requests.contains(request.toString());
    }

    public boolean getSetting(String setting) {
        if (!this.settings.containsKey(setting)) return true;
        return this.settings.getBoolean(setting);
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<String> getFriends() {
        return friends;
    }

    public List<String> getRequests() {
        return requests;
    }

    public Document getSettings() {
        return settings;
    }

    public boolean isOnline() {
        if (!this.corePlayer.getMongoDocument().containsKey("online")) return false;
        return this.corePlayer.getMongoDocument().getBoolean("online");
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public void setRequests(List<String> requests) {
        this.requests = requests;
    }

    public void setSettings(Document settings) {
        this.settings = settings;
    }

    public void setOnline(boolean online) {
        this.corePlayer.getMongoDocument().append("online", online);
        this.corePlayer.uploadToDatabase((f -> {}));
    }
}
