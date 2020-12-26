# Mini Search Engine
A Java 15 implementation of a miniature search engine using jSoup and the Rapid Automatic Keyword Extraction (RAKE) algorithm as described in: Rose, S., Engel, D., Cramer, N., & Cowley, W. (2010). Automatic Keyword Extraction from Individual Documents. In M. W. Berry & J. Kogan (Eds.), Text Mining: Theory and Applications: John Wiley & Sons.

The RAKE Algorithm implementation is based on the python one from https://github.com/aneesha/RAKE

The HTMLParser scrapes through webpages and stores all the keywords in RAKE score sorted order. 

```
  public void traversalTest() throws IOException {
  \\ The method has 3 configurable presets as can be seen in the HTML Parser: A URL keyphrase to ensure the search doesn't spiral out of control, a tree depth variable 
  \\ and a debug flag. The debug flag shows all the URLs that the parser failed to connect with
   
        String root = "https://css.csail.mit.edu/";
        HTMLParser parser = new HTMLParser(root, 2);

        System.out.println(parser.urlMap.size());
        for (String link: parser.urlMap.keySet())
            System.out.println("URL:" + link + "\n" + parser.urlMap.get(link));
    }
```

The SearchEngine is part of the McGill course COMP 250: Introduction to Computer Science's final project. Small modifications were made to ensure the script worked with the new parser.
