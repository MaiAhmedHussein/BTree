package lab5.search_engine;

public interface ISearchResult {

    /**
     * Return the document ID which is an attribute provided with each Wikipedia document,
     * please check the sample data files for more info.
     *
     * @return
     */
    String getId();

    void setId(String id);

    /**
     * Return the frequency of the word in the given document.
     *
     * @return
     */
    int getRank();

    void setRank(int rank);
}
