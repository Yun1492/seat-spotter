import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import models.CrowdInfo;

public final class DBHandler {

    final Firestore db;

    private final String LIBRARY = "library";

    public DBHandler() throws IOException, InterruptedException, ExecutionException {

        FileInputStream serviceAccount =
            new FileInputStream("/Users/yun/Downloads/ss_config.json");

        @SuppressWarnings("deprecation")
        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();
        
        System.out.println("Connected.");
        System.out.println(db);
        System.out.println();

        testing();
    }

    public void testing() throws InterruptedException, ExecutionException {

        //Testing 
        //Only append to Library ONLY
        //uploadCrowdInfoByLocation(LIBRARY,3);

        //getCrowdInfoByLocation(LIBRARY);
        //getR5CrowdInfoByLocation(LIBRARY);
        //System.out.println();
    }

    public int checkUsernameExists(String username) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = 
            db.collection("users").whereEqualTo("username", username).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (documents.isEmpty()) {
            return 404;
        }
        return 200;
    }

    public int checkLoginData(String username, String password) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = 
            db.collection("users").whereEqualTo("username", username).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            Map<String, Object> rawData = document.getData();
            if (rawData.get("password").equals(password)) {
                return 200;
            }
            return 401; //Wrong password
        }
        return 500; //Username not found
    }

    public int addNewUser(String username, String password) throws  InterruptedException, ExecutionException {
        Map<String, Object> newData = new HashMap<>();
        newData.put("username", username);
        newData.put("password", password);
        ApiFuture<DocumentReference> addedDocRef = 
            db.collection("users").add(newData);
        System.out.println("Added document with ID: " + addedDocRef.get().getId());
        return 200;
    }
    
    //Upload to Locations DIRECTLY NOT PATH CONTROL!!!
    //!!!!!!!!!!!!!!!
    public void archivedUploadCrowdInfo() throws InterruptedException, ExecutionException {
        Map<String, Object> newData = new HashMap<>();
        newData.put("crowdedness", 5);
        newData.put("datetime", Timestamp.now());
        ApiFuture<WriteResult> arrayUnion =
            db.collection("locations")
                .document("library")
                .update("crowd-infos", FieldValue.arrayUnion(newData));
        System.out.println("Update time : " + arrayUnion.get());
    }
    
    public void uploadCrowdInfoByLocation(String loc, int crowdedness) throws InterruptedException, ExecutionException {
        // Add document data with auto-generated id.
        Map<String, Object> newData = new HashMap<>();
        newData.put("crowdedness", crowdedness);
        newData.put("datetime", Timestamp.now());
        ApiFuture<DocumentReference> addedDocRef = 
            db.collection("locations").document(loc).collection("crowd-infos").add(newData);
        System.out.println("Added document with ID: " + addedDocRef.get().getId());
    }

    //void => return List TO BE IMPLEMENTED
    //TESTING METHOD
    public void archivedGetCrowdInfoByLocation(String loc) throws InterruptedException, ExecutionException {  
        ApiFuture<QuerySnapshot> future = 
            db.collection("locations").document(loc).collection("crowd-infos").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            System.out.println(document.getId() + " => " + document.toObject(CrowdInfo.class));
        }
    }

    public List<Map<String,Object>> getR5CrowdInfoByLocationFromDB(String loc) throws InterruptedException, ExecutionException {
        Instant threeHrsAgo = Instant.now().minus(3, ChronoUnit.HOURS);
        ApiFuture<QuerySnapshot> future = 
            db.collection("locations").document(loc).collection("crowd-infos")
            .whereGreaterThan("datetime", Timestamp.ofTimeSecondsAndNanos(threeHrsAgo.getEpochSecond(), threeHrsAgo.getNano()))
            .orderBy("datetime", Direction.DESCENDING).limit(5).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Map<String, Object> rawData = document.getData();
            Map<String, Object> dataMap = new HashMap<>();
            dataMap. put("crowdedness", rawData.get("crowdedness"));
            dataMap. put("datetime", ((Timestamp) rawData.get("datetime")).getSeconds());
            resultList.add(dataMap);
        }
        return resultList;
    }

    // TESTING METHOD
    public void getCrowdInfoByLocation(String loc) throws InterruptedException, ExecutionException {
        List<Map<String,Object>> info = getR5CrowdInfoByLocationFromDB(loc);   
    }

    public String uploadPost(String username, String content, boolean image) throws InterruptedException, ExecutionException {
        // Add document data with auto-generated id.
        Map<String, Object> newData = new HashMap<>();
        newData.put("username", username);
        newData.put("datetime", Timestamp.now());
        newData.put("content", content);
        newData.put("likes", 0);
        newData.put("image", image);
        ApiFuture<DocumentReference> addedDocRef = 
            db.collection("posts").add(newData);
        System.out.println("Added document with ID: " + addedDocRef.get().getId());
        return addedDocRef.get().getId();
    }

    public List<Map<String,Object>> getAllPostsFromDB() throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = 
            db.collection("posts").orderBy("datetime", Direction.DESCENDING).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Map<String, Object> rawData = document.getData();
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("username", rawData.get("username"));
            dataMap.put("datetime", ((Timestamp) rawData.get("datetime")).getSeconds());
            dataMap.put("content",rawData.get("content"));
            dataMap.put("likes",rawData.get("likes"));
            
            if ((Boolean) rawData.get("image")) {
                String docId = document.getId();
                try {
                    dataMap.put("image", ImageIOHandler.getImageAsBase64String(docId));
                } catch (Exception e) {
                    dataMap.put("image", null);
                }
            } else {
                dataMap.put("image", null);
            }

            resultList.add(dataMap);
        }
        return resultList;
    }
}