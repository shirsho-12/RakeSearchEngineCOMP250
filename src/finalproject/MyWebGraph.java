package src.finalproject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyWebGraph {
	HashMap<String, WebVertex> vertexList; 
	
	public MyWebGraph () {
		vertexList = new HashMap<String, WebVertex>();
	}

	public boolean addVertex(String s) {
		// add a vertex to the graph if it's not there yet
		if (! vertexList.containsKey(s)) {
			WebVertex v = new WebVertex(s);
			vertexList.put(s, v);
			return true;
		}
		return false;	
	}

	public boolean addEdge(String s, String t) {
		if (vertexList.containsKey(s) && vertexList.containsKey(t)) {
			WebVertex v = vertexList.get(s);
			return v.addEdge(t);
		}
		return false;
	}
    
    public ArrayList<String> getVertices() {
        ArrayList<String> urls = new ArrayList<String>();
        for (String url: vertexList.keySet()) 
        	urls.add(url);
        return urls;
    }
    public ArrayList<String> getInDegree(String v) {
        ArrayList<String> linksInto = new ArrayList<String>();
        for (String url: vertexList.keySet()) {
        	WebVertex page = vertexList.get(url);
        	if (page.containsEdge(v))
        		linksInto.add(page.url);
        }
        return linksInto;
    } 
    
    int getOutDegree(String url) {
        return vertexList.get(url).links.size();
    }        
    
    double getPageRank(String url) {
        if (vertexList.containsKey(url)) 
        	return (vertexList.get(url)).rank;
        
        return 0;
    }

    void setPageRank(String url, double pr) {
        vertexList.get(url).rank = pr;
    }

    boolean getVisited(String url) {
        if (vertexList.containsKey(url)) 
        	return (vertexList.get(url)).visited;
        
        return false;
    }

    boolean setVisited(String url, boolean b) {
        if (vertexList.containsKey(url)) {
        	(vertexList.get(url)).visited = b;
        	return true;
        }
        return false;
    }
	
	class WebVertex {
		private String url;
		private ArrayList<String> links;
		private boolean visited;
		private double rank;
		

		WebVertex (String url) {
			this.url = url;
			this.links = new ArrayList<String>();
			this.visited = false;
			this.rank = 0;
		}
		
		
		boolean addEdge(String v) {
			if (!this.links.contains(v)) {
				this.links.add(v);
				return true;
			}
			return false;
		}
	    boolean containsEdge(String e) {
	    	return this.links.contains(e);
	    }
	}
}
