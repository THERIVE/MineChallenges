package com.minelume.minechallenges.database.callables;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.database.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GetDocumentsCallable implements Callable<List<Document>> {

    private MongoConnection mongoConnection;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;

    public GetDocumentsCallable(String database, String collection) {
        this.mongoConnection = MineChallenges.getInstance().mongoConnection;
        this.mongoDatabase = this.mongoConnection.getDatabase(database);
        this.mongoCollection = this.mongoDatabase.getCollection(collection);
    }

    @Override
    public List<Document> call() throws Exception {
        List<Document> documents = new ArrayList<>();
        for (Document document : this.mongoCollection.find())
            documents.add(document);

        return documents;
    }
}
