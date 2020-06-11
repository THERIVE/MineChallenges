package com.minelume.minechallenges.database.callables;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.database.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.concurrent.Callable;

public class FindDocumentCallable implements Callable<Document> {

    private MongoConnection mongoConnection;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;

    private String key;
    private String value;

    public FindDocumentCallable(String database, String collection, String key, String value) {
        this.mongoConnection = MineChallenges.getInstance().mongoConnection;
        this.mongoDatabase = this.mongoConnection.getDatabase(database);
        this.mongoCollection = this.mongoDatabase.getCollection(collection);

        this.key = key;
        this.value = value;
    }

    @Override
    public Document call() throws Exception {
        return this.mongoCollection.find(Filters.eq(this.key, this.value)).first();
    }
}
