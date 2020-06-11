package com.minelume.minechallenges.database;

import com.minelume.minechallenges.database.callables.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class MongoDB {

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    private MongoConnection mongoConnection;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;

    public MongoDB(MongoConnection mongoConnection, MongoDatabase mongoDatabase, MongoCollection mongoCollection) {
        this.mongoConnection = mongoConnection;
        this.mongoDatabase = mongoDatabase;
        this.mongoCollection = mongoCollection;
    }

    public void setMongoConnection(MongoConnection mongoConnection) {
        this.mongoConnection = mongoConnection;
    }

    public void setDatabase(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public void setCollection(MongoCollection<Document> mongoCollection) {
        this.mongoCollection = mongoCollection;
    }

    public MongoConnection getMongoConnection() {
        return this.mongoConnection;
    }

    public MongoDatabase getDatabase() {
        return this.mongoDatabase;
    }

    public MongoCollection<Document> getCollection() {
        return this.mongoCollection;
    }

    public void getDocumentsAsync(Consumer<List<Document>> consumer) {
        /**try {
            List<Document> documents = executorService.submit(new GetDocumentsCallable
                    (this.mongoDatabase.getName(), this.mongoCollection.getNamespace().getCollectionName())).get();
            consumer.accept(documents);
        } catch (Exception e) {
            e.printStackTrace();
            consumer.accept(null);
        }**/

        //CompletableFuture.runAsync(() -> this.getCollection().find().forEach(document -> documents.add(document)))
        //       .thenRun(() -> consumer.accept(documents));

        consumer.accept(this.getDocumentsSync());
    }

    public void getDocumentAsync(String key, String value, Consumer<Document> consumer) {
        /**try {
            Document document = executorService.submit(new FindDocumentCallable(this.mongoDatabase.getName(),
                    this.mongoCollection.getNamespace().getCollectionName(), key, value)).get();
            consumer.accept(document);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            consumer.accept(null);
            return;
        }**/

        consumer.accept(this.getDocumentSync(key, value));
    }

    public void insertDocumentAsync(Document document, Consumer consumer) {
        try {
            Document inserted = executorService.submit(new InsertDocumentCallable(this.mongoDatabase.getName(),
                    this.mongoCollection.getNamespace().getCollectionName(), document)).get();
            consumer.accept(inserted);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            consumer.accept(null);
            return;
        }

        //this.insertDocumentSync(document);
        //consumer.accept(document);
    }

    public void updateDocumentAsync(String key, Document document, Consumer consumer) {
        try {
            Document updated = executorService.submit(new UpdateDocumentCallable(this.mongoDatabase.getName(), this.mongoCollection.getNamespace().getCollectionName(),
                    key, document)).get();
            consumer.accept(updated);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            consumer.accept(null);
            return;
        }
        //this.updateDocumentSync(key, document);
        //consumer.accept(document);
    }

    public List<Document> getDocumentsSync() {
        List<Document> documents = new ArrayList<>();

        for (Document document : this.mongoCollection.find())
            documents.add(document);

        return documents;
    }

    public Document getDocumentSync(String key, String value) {
        return this.mongoCollection.find(Filters.eq(key, value)).first();
    }

    public void insertDocumentSync(Document document) {
        this.mongoCollection.insertOne(document);
        return;
    }

    public void updateDocumentSync(String key, Document document) {
        Document find = this.mongoCollection.find(Filters.eq(key, document.getString(key))).first();

        if (find == null) {
            this.mongoCollection.insertOne(document);
            return;
        }

        this.mongoCollection.replaceOne(Filters.eq(key, document.getString(key)), document);
        return;
    }
}
