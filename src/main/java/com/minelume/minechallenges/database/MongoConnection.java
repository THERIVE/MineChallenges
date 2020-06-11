package com.minelume.minechallenges.database;


import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoConnection {


    private MongoClient mongoClient;

    public MongoConnection() {
        this.mongoClient = null;
    }

    public void connect(String ip, String user, String password, String authSource) {
        this.mongoClient = MongoClients.create(new ConnectionString("mongodb://"+user+":"+
                password+"@"+ip+"/?authSource="+authSource));
    }

    public MongoClient getClient() {
        return this.mongoClient;
    }

    public MongoDatabase getDatabase(String database) {
        return this.mongoClient.getDatabase(database);
    }

    public MongoCollection<Document> getCollection(String database, String collection) {
        return this.getDatabase(database).getCollection(collection);
    }
}
