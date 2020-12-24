package src;

import java.io.IOException;

public class Tester {
    String root = "https://css.csail.mit.edu/";
    HTMLParser parser = new HTMLParser(root, 3);

    public Tester() throws IOException {
    }

    public void traversalTest(){
        System.out.println(parser.urlMap.size());
        for (String link: parser.urlMap.keySet())
            System.out.println("URL:" + link + "\n" + parser.urlMap.get(link));


    }
    public void contentTest() throws IOException {
        HTMLParser.testMethod(root);
    }

    public static void main(String[] args) throws IOException {
        Tester tester = new Tester();
        tester.contentTest();
    }
}
