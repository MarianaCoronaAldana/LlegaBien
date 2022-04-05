package com.example.llegabien;

/*
import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
*/


public class QuickStart {

    public QuickStart() {
      //  Realm.init(this); // context, usually an Activity or Application
    }

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }*/

    public static void a() {
        /*
        String appID = "llegabien-fcvux"; // replace this with your App ID

        App app = new App(new AppConfiguration.Builder(appID)
                .build());

      //  App app = new App(new AppConfiguration.Builder(appID)
      //          .build());
        Credentials anonymousCredentials = Credentials.anonymous();
        AtomicReference<User> user = new AtomicReference<User>();
        app.loginAsync(anonymousCredentials, it -> {
            if (it.isSuccess()) {
                Log.v("AUTH", "Successfully authenticated anonymously.");
                user.set(app.currentUser());
            } else {
                Log.e("AUTH", it.getError().toString());
            }
        });

/*
        ConnectionString connectionString = new ConnectionString("mongodb+srv://aplicacion:aE1nMsdc4zNuuoNK@cluster0.urtdo.mongodb.net/LlegaBien?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("LlegaBien");

        MongoCollection<Document> collection = database.getCollection("favoritos");

        Document doc = collection.find(eq("nombre", "ex")).first();
        System.out.println(doc.toJson());
*/


        /*

        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "mongodb+srv://marianaca:mFip@630@cluster0.urtdo.mongodb.net/LlegaBien?retryWrites=true&w=majority";

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
            MongoCollection<Document> collection = database.getCollection("movies");

            Document doc = collection.find(eq("title", "Back to the Future")).first();
            System.out.println(doc.toJson());
        }
        catch (NumberFormatException e){
            System.out.println("Bue, no se pudo");
        }*/
    }
}

