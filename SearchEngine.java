package finalproject;

import java.util.HashMap;
import java.util.ArrayList;

public class SearchEngine {
	public HashMap<String, ArrayList<String> > wordIndex;   // this will contain a set of pairs (String, LinkedList of Strings)	
	public MyWebGraph internet;
	public XmlParser parser;
	public SearchEngine(){
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
	}
	public SearchEngine(String filename) throws Exception{
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new XmlParser(filename);
	}
	
	/* 
	 * This does a graph traversal of the web, starting at the given url.
	 * For each new page seen, it updates the wordIndex, the web graph,
	 * and the set of visited vertices.
	 * 
	 * 	This method will fit in about 30-50 lines (or less)
	 */
	public void crawlAndIndex(String url) throws Exception {
		// TODO : Add code here
//		if (! internet.addVertex(url)){
//			if (!internet.addEdge())
//		}
		boolean vertexFlag = internet.addVertex(url);
//		System.out.print(url + " " + vertexFlag + "    ");
		internet.setVisited(url, true);
		ArrayList<String> links = parser.getLinks(url);
//        for (String link: links)
//        {
            if (! wordIndex.containsKey(url)){
                ArrayList<String> text = parser.getContent(url);
                for (int j = 0; j < text.size(); j++){
                    text.set(j, text.get(j).toLowerCase());
                }
                wordIndex.put(url, text);
            }
//        }
		for (String link: links){
			internet.addVertex(link);
//			java.util.Set<String> set = new java.util.HashSet<>();

			internet.addEdge(url, link);
			if (!internet.getVisited(link)) {
				crawlAndIndex(link);
			}
		}
//		resetVisits(url);
	}
	private void resetVisits(String url){
		ArrayList<String> links = parser.getLinks(url);
		for (String link: links){
			if (internet.getVisited(url)){
				internet.setVisited(url, false);
				resetVisits(link);
			}
		}
	}
	
	
	
	/* 
	 * This computes the pageRanks for every vertex in the web graph.
	 * It will only be called after the graph has been constructed using
	 * crawlAndIndex(). 
	 * To implement this method, refer to the algorithm described in the 
	 * assignment pdf. 
	 * 
	 * This method will probably fit in about 30 lines.
	 */
	public void assignPageRanks(double epsilon) {
		// TODO : Add code here
		if (epsilon < 0) epsilon = - epsilon;
		for (String url : internet.getVertices()) internet.setPageRank(url, 1);
		boolean loopFlag, flag = true;
		int i;
		while (flag) {
			ArrayList<Double> newRanks = computeRanks(internet.getVertices());
			loopFlag = true;
			i = 0;
			for (String url : internet.getVertices()) {
				double val = (internet.getPageRank(url) - newRanks.get(i));
				if (val < 0) val = - val;
//				System.out.println(val);
				if (val > epsilon){
					loopFlag = false;
				}
				internet.setPageRank(url, newRanks.get(i));
				i++;
			}
			if (loopFlag) flag = false;
		}
	}
	/*
	 * The method takes as input an ArrayList<String> representing the urls in the web graph 
	 * and returns an ArrayList<double> representing the newly computed ranks for those urls. 
	 * Note that the double in the output list is matched to the url in the input list using 
	 * their position in the list.
	 */
	public ArrayList<Double> computeRanks(ArrayList<String> vertices) {
		ArrayList<Double> values = new ArrayList<>();
		for (String url: vertices) values.add(innerCompute(url));
		System.out.println(values.toString());
		return values;
	}
	private Double innerCompute(String url) {
		// TODO : Add code here
		ArrayList <String> incomingUrls = internet.getEdgesInto(url);
		double val = 0.5;
		for (String outUrl: incomingUrls)
			val += 0.5 * internet.getPageRank(outUrl) / internet.getOutDegree(outUrl);
		return val;
	}
	/* Returns a list of urls containing the query, ordered by rank
	 * Returns an empty list if no web site contains the query.
	 * 
	 * This method should take about 25 lines of code.
	 */
	public ArrayList<String> getResults(String query) {
		// TODO: Add code here
		query = query.toLowerCase();
		HashMap<String, Double> results = new HashMap<>();
		for (String url: internet.getVertices()){
			if (wordIndex.containsKey(url))
			if (wordIndex.get(url).contains(query)) results.put(url, internet.getPageRank(url));
		}
		return Sorting.fastSort(results);
	}
}
