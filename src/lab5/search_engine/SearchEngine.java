package ds2.lab5.search_engine;

import ds2.lab5.b_tree.BTree;
import ds2.lab5.b_tree.IBTree;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SearchEngine implements ISearchEngine {

    private final List<Map<String, IBTree<String, Integer>>> files;
    private final List<String> paths;

    public SearchEngine() {
        this.files = new ArrayList<>();
        this.paths = new ArrayList<>();
    }

    /**
     * How to read XML file
     * @param path file path that you need to read
     * @return list of WikiDoc (id, url, title, data)
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
                    String title = node.getAttributes().getNamedItem("title").getNodeValue();
                    String url = node.getAttributes().getNamedItem("url").getNodeValue();
                    String id = node.getAttributes().getNamedItem("id").getNodeValue();
                    String data = node.getTextContent();
                    wikiDocs.add(new WikiDoc(id, url, title, data));
                }
            }
            return wikiDocs;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return null;
        }
    }

    @Override
    public void indexWebPage(String filePath) {

        // read XML file and get data of WikiDoc (id, url, title, data)
        List<WikiDoc> wikiDoc = readFile(filePath);
        if (wikiDoc == null) return;

        // <id, data_associated_to_id_represented_in_BTree>
        Map<String, IBTree<String, Integer>> docsInBTree = new HashMap<>();
        for (WikiDoc doc : wikiDoc) {
            String[] data = doc.getData().replaceAll("\n", " ").toLowerCase().split(" ");
            IBTree<String, Integer> dataTree = new BTree<>(130);
            for (String word : data) {
                if (word.equals("")) continue;
                if (dataTree.search(word) == null) { // rank 1, for inserting the first time
                    dataTree.insert(word, 1);
                } else { // last_rank + 1
                    int rank = dataTree.search(word) + 1;
                    dataTree.delete(word);
                    dataTree.insert(word, rank); // re-add it to the tree
                }
            }
            docsInBTree.put(doc.getId(), dataTree);
        }

        paths.add(filePath);
        files.add(docsInBTree);
    }

    @Override
    public void indexDirectory(String directoryPath) {
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

        File file = new File(filePath);
        if (!file.exists()) return;

        for (int i = 0; i < paths.size(); i++) {
            if (paths.get(i).equals(filePath)) {
                files.remove(i);
                paths.remove(i);
            }
        }
    }

    @Override
    public List<ISearchResult> searchByWordWithRanking(String word) {
        /*
        List<ISearchResult> searched = new ArrayList<>();
        if (word != (null)) {
            if (word.equals("")) {
                return searched;
            }
            String toBeFound = word.toLowerCase();
            for (int i = 0; i < files.size(); i++) {
                HashMap temp = files.get(i);
                Iterator iterator = temp.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    BTree tree = (BTree) entry.getValue();
                    if (tree.search(toBeFound) != null) {
                        searched.add(new SearchResult((String) entry.getKey(), (int) tree.search(toBeFound)));
                    }
                }
            }
            return searched;
        } else {
            throw new RuntimeErrorException(new Error());
        }

         */
        return null;
    }

    @Override
    public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
        /*
        List<ISearchResult> searched = new ArrayList<>();
        if (sentence != (null)) {
            if (sentence.equals("")) {
                return searched;
            }
            String[] words = sentence.replaceAll("\n", " ").toLowerCase().split(" ");
            for (int i = 0; i < words.length; i++) {
                List<ISearchResult> temp = searchByWordWithRanking(words[i]);
                for (int j = 0; j < temp.size(); j++) {
                    searched.add(temp.get(j));
                }
            }
            return searched;
        } else {
            throw new RuntimeErrorException(new Error());
        }
         */
        return null;
    }

}