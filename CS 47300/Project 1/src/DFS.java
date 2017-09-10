import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;

import static java.lang.Thread.sleep;

/**
 * Created by Shubhika on 9/14/2016.
 */
public class DFS {
    Stack<String> urlsToVisit = new Stack<>();
    Set<String> visitedUrls = new HashSet<>();
    int depth = 0;
    int MAX_URLS = 1000;
    int duplicate = 0;
    int prevpages = 0;
    int largestDepth = 0;
    Hashtable<String, Integer> listOfWords = new Hashtable<>();
    public int crawl(String url) {
        try {
            int i = 0;
            if(url.length() < 5 || 'h'!= url.charAt(0) || 't'!= url.charAt(1) || 't'!= url.charAt(2) || 'p'!= url.charAt(3) ) {

            }else {
                org.jsoup.Connection connection = Jsoup.connect(url);
                org.jsoup.nodes.Document document = connection.get();
                org.jsoup.select.Elements links = document.select("a[href]");
                tokenize(document);
                for (org.jsoup.nodes.Element link : links) {
                    if (!(visitedUrls.contains(link.absUrl("href"))) && !(urlsToVisit.contains(link.absUrl("href")))) {
                        urlsToVisit.push(link.absUrl("href"));
                        //System.out.println(link.absUrl("href"));
                        i++;
                    } else
                        duplicate++;

                }
            }
            return i;
        } catch (IOException e) {
            return 0;
        }
    }
    public void tokenize(Document document){
        String text = document.body().text();
        String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        for(int i = 0; i < words.length; i++){
            if(listOfWords.containsKey(words[i])){
                int number = listOfWords.get(words[i]);
                listOfWords.replace(words[i], number, number+1);
            }else{
                listOfWords.put(words[i],1);
            }
        }
    }

    public void iterator(String url) {
        prevpages = crawl(url);
        visitedUrls.add(url);
        String curr = null;
        int currDepth =0;
        currDepth++;
        largestDepth++;
        while(!urlsToVisit.isEmpty()){
            if(visitedUrls.size() == MAX_URLS){
                break;
            }
            if(prevpages == 0){
                System.out.println("Bitch!!!");
                System.out.println(curr);
                currDepth--;
            } else if(currDepth < largestDepth) {
                //System.out.println("Bitch!!!");
                currDepth++;
            }else{
                //System.out.println("Bitch!!!");
                currDepth++;
                largestDepth++;
            }
            curr = urlsToVisit.pop();
            prevpages = crawl(curr);
            visitedUrls.add(curr);
        }
        depth = largestDepth;
        System.out.println("Link: " + urlsToVisit.size());
        System.out.println("Depth: " + depth);
        System.out.println("Duplicate: " + duplicate);
    }
    public static void main(String args[]){
        DFS d = new DFS();
        d.iterator("http://www.purdue.edu/");
    }
}
