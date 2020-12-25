package src;

import org.jetbrains.annotations.NotNull;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import src.rake.RakeModel;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;

public class HTMLParser {
    public HashMap<String, LinkObject> urlMap = new HashMap<>();
    private int depth = 3;           // Max depth of recursion
    String root;                    // Root url of recursion
    String keyword = "mit.edu";      // Keyword so that unnecessary links are ignored
    RakeModel keywordExtractor = new RakeModel(); // Text keyword extractor

    public HTMLParser(String root) throws IOException {
        this.root = root;
        updateMap(root);
        DFS(root, 0);
    }
    public HTMLParser(String root, int depth) throws IOException {
        this.root = root;
        this.depth = depth;
        updateMap(root);
        DFS(root, 0);
    }

    private ArrayList<String> getUrls(@NotNull Document document) {
        Elements links = document.select("a");
        ArrayList<String> linkArray = new ArrayList<>();
        for (Element link: links){
            String rawLink = link.outerHtml();
            String refinedLink = rawLink.split("\"")[1];
            if (refinedLink.contains(keyword) && !refinedLink.startsWith("mailto"))
                if (! linkArray.contains(refinedLink))
                    linkArray.add(refinedLink);
        }
        return linkArray;
    }

    private void DFS(String url, int depth) throws IOException {
        if (this.urlMap.get(url) == null) System.out.println(url);
        if (depth >= this.depth || this.urlMap.get(url) == null) return;
        for (String link: this.urlMap.get(url).links)
        {
            updateMap(link);
            DFS(link, depth + 1); // Flag should be used when depth is increased to avoid self-loops
        }
    }

    private ArrayList<String> extractContent(@NotNull Document document)
    {
        // TODO: RAKE-NLTK or TF-IDF -> RAKE Model implemented
        // Other way: String content = document.text();      content.text();
        Elements content = document.getElementsByTag("p");   // Get <p> tagged content from link
//        for (Element element: content)
//            contentArray.add(element.text());                        // Get text line by line: only <p> tags
        return keywordExtractor.run(content.text());
    }
    private void updateMap(String link) throws IOException, HttpStatusException {
        if (!urlMap.containsKey(link))
        {
            try{
            // Create new link object
            LinkObject object = new LinkObject();
            object.document = Jsoup.connect(link).get();        // Jsoup.parse for HTML Files, Jsoup.connect for links
            object.links = getUrls(object.document);
            object.content = extractContent(object.document);
            urlMap.put(link, object);
            }
            catch (HttpStatusException e) {
                System.out.println(e);
            }
            finally {
                return;
            }
        }
    }

    public ArrayList<String> getLinks(String link){
        if (urlMap.containsKey(link)) return urlMap.get(link).links;
        return null;
    }

    public ArrayList<String> getContent(String link) {
        if (urlMap.containsKey(link)) return urlMap.get(link).content;
        return null;
    }

    private class LinkObject{
        Document document;
        ArrayList<String> links;
        ArrayList<String> content;
//        boolean visited = false;

        @Override
        public String toString() {
            return "links=" + links.toString() +
                    "\ncontent=" + content +
                    '}';
        }
    }
    public static void testMethod(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.getElementsByTag("p");
        for (int i = 0; i < elements.size(); i++)
        System.out.println(elements.get(i).text());
        System.out.println(doc.text());

    }
}
