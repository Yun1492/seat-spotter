import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyController {

    private static List<Map<String,Object>> r5LibraryCrowdInfos = new CopyOnWriteArrayList<> ();
    private static List<Map<String,Object>> r5ChiWahCrowdInfos = new CopyOnWriteArrayList<> ();
    private static List<Map<String,Object>> r5CymCanCrowdInfos = new CopyOnWriteArrayList<> ();
    private static List<Map<String,Object>> r5UnionCrowdInfos = new CopyOnWriteArrayList<> ();
    private static List<Map<String,Object>> r5StarbucksCrowdInfos = new CopyOnWriteArrayList<> ();
    private static List<Map<String,Object>> r5CoffeeAcademicsCrowdInfos = new CopyOnWriteArrayList<> ();
    private static List<Map<String,Object>> allPosts = new CopyOnWriteArrayList<>();

    private static DBHandler dbHandler;
    private final String LIBRARY = "library";
    private final String CHIWAH = "chi-wah";
    private final String CYMCAN = "cym-can";
    private final String UNION = "union";
    private final String STARBUCKS = "starbucks";
    private final String COFFEEACA = "coffee-academics";

    private static HttpReqHandler httpHandler;

    public static List<Map<String,Object>> getR5LibraryCrowdInfos() {
        return r5LibraryCrowdInfos;
    }

    public static List<Map<String,Object>> getR5ChiWahCrowdInfos() {
        return r5ChiWahCrowdInfos;
    }

    public static List<Map<String,Object>> getR5CymCanCrowdInfos() {
        return r5CymCanCrowdInfos;
    }

    public static List<Map<String,Object>> getR5UnionCrowdInfos() {
        return r5UnionCrowdInfos;
    }

    public static List<Map<String,Object>> getR5StarbucksCrowdInfos() {
        return r5StarbucksCrowdInfos;
    }

    public static List<Map<String,Object>> getR5CoffeeAcademicsCrowdInfos() {
        return r5CoffeeAcademicsCrowdInfos;
    }

    public static List<Map<String,Object>> getAllPosts() {
        return allPosts;
    }

    public MyController() throws IOException, InterruptedException, ExecutionException {
        dbHandler = new DBHandler();
        httpHandler = new HttpReqHandler();
        startScheduler();
    }

    private void startScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            try {
                r5LibraryCrowdInfos = dbHandler.getR5CrowdInfoByLocationFromDB(LIBRARY);
                System.out.println("Recent Library CrowdInfos Fetched.");
                
                r5ChiWahCrowdInfos = dbHandler.getR5CrowdInfoByLocationFromDB(CHIWAH);
                System.out.println("Recent ChiWah CrowdInfos Fetched.");

                r5CymCanCrowdInfos = dbHandler.getR5CrowdInfoByLocationFromDB(CYMCAN);
                System.out.println("Recent CYM Canteen CrowdInfos Fetched.");
                
                r5UnionCrowdInfos = dbHandler.getR5CrowdInfoByLocationFromDB(UNION);
                System.out.println("Recent Union Restaurant CrowdInfos Fetched.");

                r5StarbucksCrowdInfos = dbHandler.getR5CrowdInfoByLocationFromDB(STARBUCKS);
                System.out.println("Recent Starbucks CrowdInfos Fetched.");
                
                r5CoffeeAcademicsCrowdInfos = dbHandler.getR5CrowdInfoByLocationFromDB(COFFEEACA);
                System.out.println("Recent Coffee Academics CrowdInfos Fetched.");

                allPosts = dbHandler.getAllPostsFromDB();
                System.out.println("All posts Fetched.");


            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        // Schedule the task to run every n minutes with an initial delay of 0
        scheduler.scheduleAtFixedRate(task, 0, 60000, TimeUnit.SECONDS);
    }

    public static DBHandler getDBHandler()  throws IOException, InterruptedException, ExecutionException {
        if (dbHandler == null) {
            dbHandler = new DBHandler();
        }
        return dbHandler;
    }

    public static void main( String[] args ) throws IOException, InterruptedException, ExecutionException {
        MyController app = new MyController();
    }
}
