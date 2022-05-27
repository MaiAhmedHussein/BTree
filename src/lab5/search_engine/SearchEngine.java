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

    public SearchEngine(int t) {
        this.tree = new BTree<>(t);
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
            IBTree<String, Integer> word_rank = new BTree<>(100);
            for (String word : words) {
                if (word.equals("")) continue;

                if (word_rank.search(word) == null) { // first added rank = 1
                    word_rank.insert(word, 1);
                } else { // las rank incremented by 1
                    int rank = word_rank.search(word) + 1;
                    word_rank.delete(word);
                    word_rank.insert(word, rank);
                }
            }
            tree.insert(doc.getId(), word_rank);
        }
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

        List<WikiDoc> wikiDocs = readFile(filePath);
        if (wikiDocs != null) {
            for (WikiDoc wikiDoc : wikiDocs) {
                tree.delete(wikiDoc.getId());
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