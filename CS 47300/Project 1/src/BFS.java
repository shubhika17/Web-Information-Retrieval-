/**
 * Created by Shubhika on 9/13/2016.
 */
import java.io.FileWriter;
import java.util.Queue;
import java.util.Set;

import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.*;
import static java.lang.Thread.sleep;

public class BFS {
    Queue<String> urlsToVisit = new LinkedList<>();
    Set<String> visitedUrls = new HashSet<>();
    Hashtable<String, Integer> listOfWords = new Hashtable<>();
    ArrayList<String> stopWordList = new ArrayList<>();
    int depth = 0;
    int MAX_URLS = 250;
    int duplicate = 0;
    int prevPages = 0;
    String StopWordurl ="http://ir.dcs.gla.ac.uk/resources/linguistic_utils/stop_words";
    Document stopword;
    public int crawl(String url) {
        try {
            int i = 0;
            if(url.length() < 5 || 'h'!= url.charAt(0) || 't'!= url.charAt(1) || 't'!= url.charAt(2) || 'p'!= url.charAt(3) ) {

            }else {
                org.jsoup.Connection connection = Jsoup.connect(url);
                org.jsoup.nodes.Document document = connection.get();
                org.jsoup.select.Elements links = document.select("a[href]");
                if(url.equals(StopWordurl)){
                    stopword = document;
                    return 0;
                }
                tokenize(document);
                //if(url.equals(StopWordurl)){
                    //return 0;
                //}
                for (org.jsoup.nodes.Element link : links) {
                    if (!(visitedUrls.contains(link.absUrl("href"))) && !(urlsToVisit.contains(link.absUrl("href")))) {
                            urlsToVisit.add(link.absUrl("href"));
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
    public void stopwords(){
        crawl(StopWordurl);
        String text = stopword.body().text();
        String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        for(int i = 0; i < words.length; i++){
            stopWordList.add(words[i]);
        }
    }
    public void writer() throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter("histogramDataNotStop.csv"), ',');
        Hashtable<String, Integer> temp = new Hashtable<>(listOfWords);
        String [] words = new String[100];
        int [] frequency = new int[100];
        int Stop = 0;
        for (int i = 0; i < 100 && !temp.isEmpty(); i++) {
            int max = 0;
            String maxWord = "";
            for (String word: temp.keySet()) {
                    if(stopWordList.contains(word)){
                        Stop++;
                    }
                    int count = temp.get(word);
                    if (max < count) {
                        max = count;
                        maxWord = word;
                    }
            }
            words[i] = new String(maxWord);
            frequency[i] = max;
            temp.remove(maxWord);
        }
        System.out.println(listOfWords.size() - (Stop/100));
        System.out.println((listOfWords.size() - (Stop/100))/250);
//        Hashtable<String, Integer> temp2 = new Hashtable<>(listOfWords);
//        String [] words2 = new String[319];
//        int [] frequency2 = new int[319];
//        for (int i = 0; i < 319 && !temp2.isEmpty(); i++) {
//            int max = 0;
//            String maxWord = "";
//            for (String word: temp2.keySet()) {
//
//                Integer count = temp2.get(word);
//                if (max < count) {
//                    max = count;
//                    maxWord = word;
//                }
//            }
//            words2[i] = maxWord;
//            frequency2[i] = max;
//            temp2.remove(maxWord);
//        }
//        int NotStop = 0;
//        int number = 0;
//        for(int i = 0; i < words2.length; i++){
//            if(!stopWordList.contains(words2[i])){
//                NotStop++;
//                if(number < 10){
//                    System.out.println(i + words2[i]);
//                    number++;
//                }
//            }
//        };

        for (int i = 0; i <frequency.length; i++) {
            String[] record = {words[i], "" + frequency[i]};
            writer.writeNext(record);
        }
        writer.close();
    }

    public void iterator(String url) {
        int count = 0;
        int[] pages = new int[1000];
        prevPages = crawl(url);
        pages[0] = prevPages;
        visitedUrls.add(url);
        String curr;
        depth++;
        while(!urlsToVisit.isEmpty()){
            //try {
                //sleep(5000);
            //} catch (InterruptedException e) {
                //e.printStackTrace();
            //}
            if(visitedUrls.size() == MAX_URLS){
                break;
            }
            if(prevPages == count){
                prevPages = pages[prevPages];
                depth++;
                count = 0;
            }
            curr = urlsToVisit.remove();
            pages[visitedUrls.size()] = crawl(curr);
            visitedUrls.add(curr);
            count++;
            System.out.println("link :" + curr + " "+ visitedUrls.size());
        }
        System.out.println("Link: " + urlsToVisit.size());
        System.out.println("Depth: " + depth);
        System.out.println("Duplicate: " + duplicate);
    }
    public static void main(String args[]){
        BFS d = new BFS();
        d.stopwords();
        d.iterator("http://www.cs.purdue.edu/");
        try {
            d.writer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

