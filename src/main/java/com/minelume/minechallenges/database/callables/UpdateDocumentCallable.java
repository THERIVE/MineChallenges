package com.minelume.minechallenges.database.callables;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.database.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.concurrent.Callable;

public class UpdateDocumentCallable implements Callable<Document> {

    private MongoConnection mongoConnection;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;

    private String key;
    private Document document;

    public UpdateDocumentCallable(String database, String collection, String key, Document document) {
        this.mongoConnection = MineChallenges.getInstance().mongoConnection;
        this.mongoDatabase = this.mongoConnection.getDatabase(database);
        this.mongoCollection = this.mongoDatabase.getCollection(collection);

        this.key = key;
        this.document = document;
    }

    @Override
    public Document call() throws Exception {
        Document find = this.mongoCollection.find(Filters.eq(this.key, this.document.getString(this.key))).first();

        if (find == null) {
            this.mongoCollection.insertOne(this.document);
            return this.document;
        }

        this.mongoCollection.replaceOne(Filters.eq(this.key, this.document.getString(this.key)), this.document);
        return this.document;
    }
}
