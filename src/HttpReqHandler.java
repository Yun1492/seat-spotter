import com.google.gson.Gson;

import models.UploadData;
import io.javalin.Javalin;

public class HttpReqHandler {

    private static DBHandler dbHandler;
    
    public HttpReqHandler() {

        try {
            dbHandler = MyController.getDBHandler();
        } catch (Exception e) {
            System.err.println(e);
        }

        Javalin app = Javalin.create().start(8000);

        // Define a POST route
        app.post("/api/data", ctx -> {
            String requestData = ctx.body();
            ctx.result("Received POST request with data: " + requestData);
        });

        app.post("/login", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
    
            String message = "Received POST login request with username=" + username+ ", password=" + password;
            System.out.println(message);
            try {
                int status = 404;
                if (dbHandler.checkUsernameExists(username) == 200) {
                    status = dbHandler.checkLoginData(username, password);
                    System.out.println("Status: " + String.valueOf(status));
                }
                //username not found
                System.out.println("Status: " + String.valueOf(status));
                ctx.status(status);
            } catch (Exception e) {
                ctx.status(500);
            }
        });

        app.post("/sign-up", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
    
            String message = "Received POST sign up request with username=" + username+ ", password=" + password;
            System.out.println(message);
            try {
                int status = 409;
                if (dbHandler.checkUsernameExists(username) == 200) { // username exists
                    status = 409;
                    System.out.println("Status: " + String.valueOf(status));
                    ctx.status(status);
                } else {
                    status = dbHandler.addNewUser(username, password);
                    System.out.println("Status: " + String.valueOf(status));
                    ctx.status(status);
                }
            } catch (Exception e) {
                //ctx.result( message + "\nUpload FAILED.");
                ctx.status(500);
            }
        });

        app.get("/data/locations/{loc}/crowd-infos", ctx -> {
            String requestLoc = ctx.pathParam("loc");
            String message = "Received GET request with loc=" + requestLoc + '\n';
            System.out.print(message);
            switch (requestLoc) {
                case "library":
                    //ctx.result(message + MyController.getR5LibraryCrowdInfos() + '\n');
                    ctx.json(MyController.getR5LibraryCrowdInfos());
                    break;
                case "chi-wah":
                    ctx.json(MyController.getR5ChiWahCrowdInfos());
                    break;
                case "cym-can":
                    ctx.json(MyController.getR5CymCanCrowdInfos());
                    break;
                case "union":
                    ctx.json(MyController.getR5UnionCrowdInfos());
                    break;
                case "starbucks":
                    ctx.json(MyController.getR5StarbucksCrowdInfos());
                    break;
                case "coffee-academics":
                    ctx.json(MyController.getR5CoffeeAcademicsCrowdInfos());
                    break;
                default:
                    ctx.result(message + "Unknwon loc PARAM. Request FAILED." + '\n');
                    break;
            }
        });

        app.post("/data/locations/{loc}/crowd-infos", ctx -> {
            String requestLoc = ctx.pathParam("loc");
    
            // Parse JSON data using Gson
            Gson gson = new Gson();
            UploadData data = gson.fromJson(ctx.body(), UploadData.class);
            String message = "Received POST request with loc=" + requestLoc + ", body=" + ctx.body();
            System.out.println(message);
            try {
                dbHandler.uploadCrowdInfoByLocation(requestLoc, data.getCrowdedness());
                ctx.json("SUCCESS");
            } catch (Exception e) {
                ctx.json("FAIL");
            }
        });

        app.get("/data/posts", ctx -> {
            String message = "Received GET posts request";
            System.out.println(message);
            ctx.json(MyController.getAllPosts());          
        });

        app.post("/data/posts", ctx -> {    
            String username = ctx.formParam("username");
            String content = ctx.formParam("content");
            String image = ctx.formParam("image");

            String message = "Received POST request with username=" + username + " content=" + content + " image=";
            if (!image.isEmpty()) {
                message = message + "true";
            } else { message = message + "false"; }
            System.out.println(message);
    
            try {
                if (image.isEmpty()) {
                    dbHandler.uploadPost(username, content, false);
                } else {
                    String documentId = dbHandler.uploadPost(username, content, true);
                    ImageIOHandler.saveBase64StringToFile(image, documentId);
                }
                ctx.json("SUCCESS");
            } catch (Exception e) {
                ctx.json("FAIL");
            }
        });
        
        // Define a custom error handler
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.result("Internal server error");
        });
    }

}