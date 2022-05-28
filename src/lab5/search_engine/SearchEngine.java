package lab5.search_engine;

import lab5.b_tree.BTree;
import lab5.b_tree.IBTree;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SearchEngine implements ISearchEngine {

    // IBTree<id, IBTree<Word, its_rank>>
    private final IBTree<String, IBTree<String, Integer>> tree;
    private final List<String> ids; // to save all ids all the time

    public SearchEngine(int t) {
        this.tree = new BTree<>(t);
        this.ids = new LinkedList<>();
    }

    /**
     * How to read XML file
     *
     * @param path file path that you need to read
     * @return list of WikiDoc (id, data)
     */
    private List<WikiDoc> readFile(String path) {
        File file = new File(path);
        if (!file.exists()) return null;

        try {
            // List of data
            List<WikiDoc> wikiDocs = new LinkedList<>();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document document = builder.parse(file);
            NodeList nodeList = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String id = node.getAttributes().getNamedItem("id").getNodeValue();
                    String data = node.getTextContent();
                    wikiDocs.add(new WikiDoc(id, data));
                }
            }
            return wikiDocs;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return null;
        }
    }

    @Override
    public void indexWebPage(String filePath) {
        // read XML file and get data of WikiDoc (id, data)
        List<WikiDoc> wikiDoc = readFile(filePath);
        if (wikiDoc == null) return;

        for (WikiDoc doc : wikiDoc) {
            String[] words = doc.getData().replaceAll("\n", " ").toLowerCase().split(" ");
            IBTree<String, Integer> word_rank = new BTree<>(10);
            for (String word : words) {

                if (word.equals("")) continue;
                Integer value = word_rank.search(word);
                if (value == null) { // first added rank = 1
                    word_rank.insert(word, 1);
                } else { // las rank incremented by 1
                    int rank = value + 1;
                    word_rank.delete(word);
                    word_rank.insert(word, rank);
                }
            }
            tree.insert(doc.getId(), word_rank);
            ids.add(doc.getId());
        }
    }



    @Override
    public void indexDirectory(String directoryPath) {
        //Looping through the files and doing the same as indexWebPage for each file.
        File dir = new File(directoryPath);
        File[] listFiles = dir.listFiles();

        assert listFiles != null;
        for (File file : listFiles) {
            if (file.isDirectory()) // Recursion
                indexDirectory(file.getPath());
            else
                indexWebPage(file.getPath());
        }
    }



    @Override
    public void deleteWebPage(String filePath) {
        //Removing the file with the given path,
        // by reading this file and call function delete for each word id.
        File file = new File(filePath);
        if (!file.exists()) return;

        List<WikiDoc> wikiDocs = readFile(filePath);
        if (wikiDocs != null) {
            for (WikiDoc wikiDoc : wikiDocs) {
                tree.delete(wikiDoc.getId());
            }
        }
    }

    @Override
    public List<ISearchResult> searchByWordWithRanking(String word) {
        //Loops through every document in this file,
        // and apply B-tree search to each word of the documents.
        List<ISearchResult> results = new LinkedList<>();
        for (String id : ids) {
            Integer rank = tree.search(id).search(word.toLowerCase());
            if (rank != null) {
                results.add(new SearchResult(id, rank));
            }
        }
        return results;
    }

    @Override
    public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
        //Same as searchByWordWithRanking but splitting the sentence into multiple words,
        // and getting the minimum of them if the sentence is found in the specific id.
        if (sentence == null) {
            return null;
        }

        String[] words = sentence.toLowerCase().split(" ");
        List<ISearchResult> res = new LinkedList<>();
        for (String id : ids) {
            boolean notFound = false;
            int minValue = Integer.MAX_VALUE;
            for (String word : words) {
                Integer value = tree.search(id).search(word);
                if (value == null) {
                    notFound = true;
                    break;
                } else {
                    minValue = Math.min(value, minValue);
                }
            }
            if (!notFound) {
                res.add(new SearchResult(id, minValue));
            }
        }
        return res;
    }


}
