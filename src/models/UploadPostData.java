package models;

public class UploadPostData {

    private String username;
    private String content;

    public UploadPostData() {}

    public UploadPostData(String u, String c) {
        this.username = u;
        this.content = c;
    }

    public void setUsername(String u) { this.username = u; }

    public String getUsername() { return this.username; }
    
    public void setContent(String c) { this.content = c; }

    public String getContent() { return this.content; }

}
