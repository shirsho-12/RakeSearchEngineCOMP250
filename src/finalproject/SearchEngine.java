package src.finalproject;

import src.HTMLParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

public class SearchEngine {
	public HashMap<String, ArrayList<String> > wordIndex;
	public MyWebGraph internet;
	public HTMLParser parser;

	public SearchEngine(String rootUrl) throws IOException {
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new HTMLParser(rootUrl);
	}
	public SearchEngine(String rootUrl, int depth) throws IOException {
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new HTMLParser(rootUrl, depth);
	}
	public SearchEngine(String rootUrl, String keyword, int depth) throws IOException {
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new HTMLParser(rootUrl,keyword, depth, false);
	}

	public void crawlAndIndex(String url) throws Exception {

		internet.addVertex(url);
		internet.setVisited(url, true);
		ArrayList<String> links = parser.getLinks(url);
		ArrayList<String> text;
		ArrayList<String> newList;
		internet.setPageRank(url, 1.0);
		text = parser.getContent(url);
		if (text == null) text = new ArrayList<>();
		for (String word: text){
			word = word.toLowerCase();
			if (!wordIndex.containsKey(word)){
				ArrayList<String> urlList = new ArrayList<>();
				urlList.add(url);
				wordIndex.put(word, urlList);
			}
			else{
				if (! wordIndex.get(word).contains(url)) {
					newList = wordIndex.get(word);
					newList.add(url);
					wordIndex.replace(word, newList);
				}
			}
		}
		if (!wordIndex.containsKey(url)) {

			for (int j = 0; j < text.size(); j++) {
				text.set(j, text.get(j).toLowerCase());
				wordIndex.put(url, text);
			}
			wordIndex.put(url, text);
		}
		if (links == null) links = new ArrayList<>();
		for (String link: links){
			internet.addVertex(link);
			internet.addEdge(url, link);
			if (!internet.getVisited(link)) {
				crawlAndIndex(link);
			}
		}
	}

	public void assignPageRanks(double epsilon) {
		if (epsilon < 0) epsilon = - epsilon;
		boolean loopFlag, flag = true;
		double val;
		while (flag) {
			ArrayList<Double> prevRanks = new ArrayList<>();
			for (String url: internet.getVertices())
			{
				prevRanks.add(internet.getPageRank(url));
			}
			ArrayList<Double> newRanks = computeRanks(internet.getVertices());
			loopFlag = true;
			for (int i = 0; i <  prevRanks.size(); i++) {
				val = (prevRanks.get(i) - newRanks.get(i));
				if (val < 0) val = - val;
				if (val > epsilon) loopFlag = false;
			}
			if (loopFlag) flag = false;
		}
	}

	public ArrayList<Double> computeRanks(ArrayList<String> vertices) {
		ArrayList<Double> values = new ArrayList<>();
		double val;
		for (String url: vertices)
		{
			val = innerCompute(url);
			values.add(val);
		}
		for (int i = 0; i < values.size(); i++)
			internet.setPageRank(vertices.get(i), values.get(i));
		return values;
	}
	private Double innerCompute(String url) {
		ArrayList <String> incomingUrls = internet.getInDegree(url);
		double val = 0.5;
		for (String outUrl: incomingUrls)
			val += 0.5 * internet.getPageRank(outUrl) / internet.getOutDegree(outUrl);
		return val;
	}

	public ArrayList<String> getResults(String query) {
		query = query.toLowerCase();
		HashMap<String, Double> results = new HashMap<>();
		if (wordIndex.containsKey(query))
		{
			ArrayList<String> urlList = wordIndex.get(query);
			for (String url: urlList)
				results.put(url, internet.getPageRank(url));
		}
		ArrayList<String> sortedUrls = Sorting.fastSort(results);
		return sortedUrls;
	}
}
