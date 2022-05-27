package ds2.lab5.search_engine;

public class WikiDoc {

    private final String id;
    private final String url;
    private final String title;
    private final String data;

    public WikiDoc(String id, String url, String title, String data) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }
}
