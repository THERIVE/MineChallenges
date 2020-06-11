package com.minelume.minechallenges.database.callables;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.database.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.concurrent.Callable;

public class InsertDocumentCallable implements Callable<Document> {

    private MongoConnection mongoConnection;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;

    private Document document;

    public InsertDocumentCallable(String database, String collection, Document document) {
        this.mongoConnection = MineChallenges.getInstance().mongoConnection;
        this.mongoDatabase = this.mongoConnection.getDatabase(database);
        this.mongoCollection = this.mongoDatabase.getCollection(collection);

        this.document = document;
    }

    @Override
    public Document call() throws Exception {
        this.mongoCollection.insertOne(this.document);
        return this.document;
    }
}
