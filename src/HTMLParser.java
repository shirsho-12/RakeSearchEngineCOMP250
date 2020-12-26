package src;

import org.jetbrains.annotations.NotNull;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import src.rake.RakeModel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.regex.Pattern;

public class HTMLParser {
    public HashMap<String, LinkObject> urlMap = new HashMap<>();
    private int depth = 3;           // Max depth of recursion
    String root;                    // Root url of recursion
    String keyword = "mit.edu";      // Keyword so that unnecessary links are ignored
    HashMap<String, Exception> exceptionList = new HashMap<>();
    RakeModel keywordExtractor = new RakeModel(); // Text keyword extractor
    boolean debug;

    public HTMLParser(String root) throws IOException {
        this.root = root;
        updateMap(root);
        DFS(root, 0);
        debug = true;
    }
    public HTMLParser(String root, int depth) throws IOException {
        this.root = root;
        this.depth = depth;
        this.debug = true;
        updateMap(root);
        DFS(root, 0);
    }
    public HTMLParser(String root, String keyword, int depth, boolean debug) throws IOException {
        this.root = root;
        this.depth = depth;
        this.keyword = keyword;
        updateMap(root);
        DFS(root, 0);
        this.debug = debug;
    }

    private ArrayList<String> getUrls(@NotNull Document document) {
        Elements links = document.select("a");

        ArrayList<String> linkArray = new ArrayList<>();
        for (Element link: links){
            String rawLink = link.outerHtml();
            String[] rawLinkArray = rawLink.split("\"");
            if (rawLinkArray.length <= 1) continue;
            String refinedLink = rawLinkArray[1];
            if (refinedLink.contains(keyword) && !refinedLink.startsWith("mailto")) {
                if (!linkArray.contains(refinedLink)) {
                    linkArray.add(refinedLink);
                }
            }
        }
        return linkArray;
    }

    private void DFS(String url, int depth) throws IOException {
//        if (this.urlMap.get(url) == null) System.out.println("NULL : " + url);
        if (depth >= this.depth || this.urlMap.get(url) == null) return;
        for (String link: this.urlMap.get(url).links)
        {
            updateMap(link);
            DFS(link, depth + 1); // Flag should be used when depth is increased to avoid self-loops
        }
    }

    private ArrayList<String> extractContent(@NotNull Document document) throws UnsupportedEncodingException {
        // TODO: RAKE-NLTK or TF-IDF -> RAKE Model implemented
        // Other way: String content = document.text();      content.text();
//        return null;
        String content = document.getElementsByTag("p").text();   // Get <p> tagged content from link
        ArrayList<String> contentArray = keywordExtractor.run(content);

        if (contentArray.size() == 0)  return null;
        return contentArray;
    }
    private void updateMap(String link) throws IOException, HttpStatusException {
        if (!urlMap.containsKey(link) && !exceptionList.containsKey(link))
        {
            try{
            // Create new link object
            LinkObject object = new LinkObject();
            object.document = Jsoup.connect(link).get();        // Jsoup.parse for HTML Files, Jsoup.connect for links
            object.links = getUrls(object.document);
            object.content = extractContent(object.document);
            urlMap.put(link, object);
            }
            catch (Exception e) {
                if (debug) System.out.println(e);
                exceptionList.put(link, e);
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
    public void printExceptions(){
        for (String link: exceptionList.keySet()){
            System.out.println(exceptionList.get(link));
        }
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

}
