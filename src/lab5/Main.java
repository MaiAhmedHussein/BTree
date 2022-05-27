package lab5;

import lab5.search_engine.ISearchEngine;
import lab5.search_engine.SearchEngine;

public class Main {

    public static void main(String[] args) {

        ISearchEngine searchEngine = new SearchEngine(10);
        searchEngine.indexWebPage("src/data_sample/wiki_00");

    }
}
