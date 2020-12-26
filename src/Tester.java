package src;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import src.finalproject.SearchEngine;
import src.rake.RakeModel;

import java.io.IOException;
import java.util.ArrayList;

public class Tester {

    public void traversalTest() throws IOException {
        String root = "https://css.csail.mit.edu/";
        HTMLParser parser = new HTMLParser(root, 2);

        System.out.println(parser.urlMap.size());
        for (String link: parser.urlMap.keySet())
            System.out.println("URL:" + link + "\n" + parser.urlMap.get(link));
    }
    public static void testMethod(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.getElementsByTag("p");
        for (int i = 0; i < elements.size(); i++)
            System.out.println(elements.get(i).text());
        System.out.println(doc.text());

    }
    public void contentTest() throws IOException {
        String root = "https://css.csail.mit.edu/";
        testMethod(root);
    }
    public void rakeCheck() throws IOException {
        String root = "https://css.csail.mit.edu/";
        RakeModel model = new RakeModel();
//        String doc = "Compatibility of systems of linear constraints over the set of natural numbers. Criteria of compatibility of a system of linear Diophantine equations, strict inequations, and nonstrict inequations are considered. Upper bounds for components of a minimal set of solutions and algorithms of construction of minimal generating sets of solutions for all types of systems are given. These criteria and the corresponding algorithms for constructing a minimal supporting set of solutions can be used in solving all the considered types of systems and systems of mixed types.";
        String doc = Jsoup.connect(root).get().getElementsByTag("p").text();
        System.out.println(doc);
        ArrayList<String> keywords = model.run(doc);
        int i = 0;
        for (String keyword: keywords) {
            System.out.println(keyword + ": " + model.candidateScores.get(keyword));
            i++;
            if (i >= 10) break;
        }
    }

    public void testScript() throws Exception {
        Tester tester = new Tester();
        tester.traversalTest();
        tester.rakeCheck();
        tester.contentTest();
        String root = "https://css.csail.mit.edu";

        SearchEngine engine = new SearchEngine(root, 2);
        engine.crawlAndIndex(root);
        engine.assignPageRanks(0.001);
        engine.parser.printExceptions();
        System.out.println(engine.getResults("mit"));
    }

    public static void main(String[] args) throws Exception {
        testMethod("https://www.csail.mit.edu/");

    }
}
