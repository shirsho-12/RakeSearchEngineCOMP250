package src;

import src.finalproject.SearchEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class RunEngine {
    public static void main(String[] args) throws Exception {
        String url;
        String keyword;
        int depth;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter root url to be traversed from (INCLUDE HTTP/HTTPS IN LINK): ");
        url = scanner.next();
        System.out.println("Enter root keyword to be considered during traversal (i.e. the websites types " +
                "the crawler should keep " +
                "\ne.g. only keep McGill links in the tree if parsing through https://www.mcgill.ca: ");
        keyword = scanner.next();
        System.out.println("Enter the max depth of the tree path (larger the depth, longer the tree creation time: ");
        depth = scanner.nextInt();
        System.out.println("TREE GENERATION INITIATED");
        SearchEngine engine = new SearchEngine(url, keyword, depth);
        engine.crawlAndIndex(url);
        engine.assignPageRanks(0.0001);
        System.out.println("TREE GENERATION COMPLETE");

        String searchTerm;
        ArrayList<String> searchResults;
        while (true) {
            System.out.println("Enter word into search engine (Enter -1 to exit): ");
            searchTerm = scanner.next();
            if (searchTerm.strip().equals("-1")) break;
            searchResults = engine.getResults(searchTerm);
            try {
                int i = 0;
                System.out.println("Top results:  ");
                for (String result : searchResults) {
                    i++;
                    System.out.println("Result " + i + ": " + result);
                    if (i == 5) break;
                }
            } catch (Exception e) {
                System.out.println("No results found");
            }

        }
    }
}
