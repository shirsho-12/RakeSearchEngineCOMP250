package src;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.regex.*;

public class HTMLParser {
    public HashMap<String, LinkObject> urlMap;
    private int depth = 3;           // Max depth of recursion
    String root;                    // Root url of recursion

    HTMLParser(String root){
        this.root = root;
        DFS(root, 0);
    }
    HTMLParser(String root, int depth){
        this.root = root;
        this.depth = depth;
        DFS(root, 0);
    }

    private ArrayList<String> getUrls(@NotNull Document document) {
        Elements links = document.select("a");
        ArrayList<String> linkArray = new ArrayList<>();
        for (Element link: links){
            String rawLink = link.outerHtml();
            String refinedLink = rawLink.split("\"")[1];
            if (refinedLink.contains(".") && !refinedLink.contains("@"))
                if (! linkArray.contains(refinedLink))
                    linkArray.add(refinedLink);
        }
        return linkArray;
    }

    private void DFS(String url, int depth)
    {
        if (depth > this.depth) return;
        for (String link: this.urlMap.get(url).links)
        {
            updateMap(link);
            DFS(link, depth + 1);         // Flag should be used when depth is increased
        }
    }

    private ArrayList<String> extractContent(@NotNull Document document)
    {
        return null;
    }
    private void updateMap(String link)
    {
        if (!urlMap.containsKey(link))
        {
            // Create new link object
            LinkObject object = new LinkObject();
            object.document = Jsoup.parse(link);
            object.links = getUrls(object.document);
            object.content = extractContent(object.document);
            urlMap.put(link, object);
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
    }
}
