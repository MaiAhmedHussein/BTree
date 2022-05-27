package ds2.lab5.search_engine;

public class WikiDoc {

    private final String id;
    private final String data;

    public WikiDoc(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }
}
