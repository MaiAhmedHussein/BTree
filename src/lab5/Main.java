package lab5;

import lab5.search_engine.ISearchEngine;
import lab5.search_engine.ISearchResult;
import lab5.search_engine.SearchEngine;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        ISearchEngine searchEngine = new SearchEngine(10);
        searchEngine.indexWebPage("src/data_sample/wiki_00");
        List<ISearchResult> wiki = searchEngine.searchByWordWithRanking("is");
        wiki.forEach(System.out::println);

    }
}
