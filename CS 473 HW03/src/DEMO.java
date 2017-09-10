import org.lemurproject.galago.core.index.IndexPartReader;
import org.lemurproject.galago.core.index.KeyIterator;
import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.index.stats.FieldStatistics;
import org.lemurproject.galago.core.index.stats.IndexPartStatistics;
import org.lemurproject.galago.core.index.stats.NodeStatistics;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.iterator.CountIterator;
import org.lemurproject.galago.core.retrieval.processing.ScoringContext;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.StructuredQuery;
import org.lemurproject.galago.utility.ByteUtil;
import org.lemurproject.galago.utility.Parameters;
import java.io.File;
import java.util.*;

/**
 * Created by Shubhika on 11/12/2016.
 */
public class DEMO {
    public static void main(String args[]) throws Exception {
        String path = "C:\\Users\\Shubhika\\Desktop\\galago-3.14159\\wiki-small-index\\";
        Retrieval retrieval = RetrievalFactory.instance(path, Parameters.create());
        Node n = new Node();
        n.setOperator("lengths");
        n.getNodeParameters().set("part", "lengths");
        FieldStatistics stat = retrieval.getCollectionStatistics(n);
        String unique = "postings";
        IndexPartStatistics stat1 = retrieval.getIndexPartStatistics(unique);
        long maxLength = stat.maxLength;
        long minLength = stat.minLength;
        double avgLength = stat.avgLength;
        long documentCount = stat.documentCount;
        longestDocument(retrieval, stat);
        String query = "maximum";
        String query2 = "entropy";
        String query3 = "data";
        String query4 = "science";
        idftf(query, path);
        idftf(query2, path);
        idftf(query3, path);
        idftf(query4, path);
        System.out.printf ("Max length       : %d \n", maxLength);
        System.out.printf ("Min length       : %d \n", minLength);
        System.out.printf ("Average length   : %f \n", avgLength);
        System.out.printf ("Document Count   : %d \n", documentCount);
        long count = stat1.vocabCount;
        System.out.printf("Unique Words count : %d \n", count);
        String[] query5 = {"maximum","entropy","data","science"};
        booleanAND(query5);
        String[] query6 = {"maximum","entropy"};
        booleanAND(query6);
    }

    private static void idftf(String query, String path) throws Exception {
        Retrieval retrieval = RetrievalFactory.instance(path);
        Node node = StructuredQuery.parse(query);
        node.getNodeParameters().set( "queryType", "count" );
        node = retrieval.transformQuery(node, Parameters.create());
        NodeStatistics stat = retrieval.getNodeStatistics( node );
        long tF = stat.nodeFrequency;
        long count = stat.nodeDocumentCount;

        //double K = parameters.getDouble("K");
        System.out.println("Max Count in one document: " + stat.maximumCount);
        Node n = new Node();
        n.setOperator("lengths");
        n.getNodeParameters().set("part", "lengths");
        FieldStatistics stat2 = retrieval.getCollectionStatistics(n);
        long cl = stat2.collectionLength;
        long corpusLength  = stat2.documentCount;
        double idf = Math.log( ( corpusLength + 1 ) / ( count + 1 ) );
        double pwc = 1.0 * tF / cl;
        System.out.println(query + count +idf );
        System.out.println(query + " " + corpusLength + " " + tF + " " + pwc);
        double tf = calculateTermFreq(query, path);
        double tf_idf = idf*tf;
        System.out.println("TF*IDF: " +" "+ tf_idf);
        retrieval.close();
    }

    private static double calculateTermFreq(String term, String path) throws Exception {
        File pathPosting = new File( new File(path), "postings");
        IndexPartReader posting = DiskIndex.openIndexPart( pathPosting.getAbsolutePath() );
        KeyIterator vocabulary = posting.getIterator();
        if ( vocabulary.skipToKey( ByteUtil.fromString( term ) ) && term.equals( vocabulary.getKeyString() ) ) {
            CountIterator iterator = (CountIterator) vocabulary.getValueIterator();
            ScoringContext sc = new ScoringContext();
            int maxFrequency = 0;
            long maxDocID = 0;
            while ( !iterator.isDone() ) {
                sc.document = iterator.currentCandidate();
                int freq = iterator.count( sc );
                if (freq > maxFrequency) {
                    maxFrequency = freq;
                    maxDocID = sc.document;
                }
                iterator.movePast( iterator.currentCandidate() );
            }
            Retrieval retrieval = RetrievalFactory.instance(path, Parameters.create());
            Node n = new Node();
            n.setOperator("lengths");
            n.getNodeParameters().set("part", "lengths");
            String docID = retrieval.getDocumentName( (int) maxDocID );
            long length = retrieval.getDocumentLength(docID);
            double TF = (double) maxFrequency / (double) length;
            System.out.println("TF: " + TF);
            return TF;
        }
        return 0;
    }

    private static void longestDocument(Retrieval retrieval,FieldStatistics stat) throws Exception {
        long max = 0;
        long id = 0;
        for ( long i = stat.firstDocId; i <= stat.lastDocId; i++ ) {
            String docID = retrieval.getDocumentName( (int) i );
            long length = retrieval.getDocumentLength(docID);
            if (length > max) {
                max = length;
                id = i;
            }
        }
        System.out.println("Longest Document: Doc ID: " + id);
        System.out.println("Longest Document: DOC LENGTH: " + max);
    }


    private static void booleanAND(String[] query) throws Exception {
        Hashtable<String,String> max = getDocTerm(query[0]);
        Hashtable<String,String> entr = getDocTerm(query[1]);
        Iterator<Map.Entry<String, String>> iterator = max.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<String, String> pair = (Map.Entry<String, String>)iterator.next();
            if (entr.containsKey(pair.getKey())) {
                System.out.println(pair.getKey() + " " + pair.getValue());
            }
            iterator.remove();
        }
    }
    private static Hashtable<String,String> getDocTerm(String query) throws Exception {
        String path = "C:\\Users\\Shubhika\\Desktop\\galago-3.14159\\wiki-small-index\\";
        File postingPath = new File( new File(path), "postings" );
        DiskIndex diskIndex = new DiskIndex(path);
        IndexPartReader post = DiskIndex.openIndexPart(postingPath.getAbsolutePath());
        Hashtable<String,String> list = new Hashtable<>();
        KeyIterator vocabulary = post.getIterator();
        if ( vocabulary.skipToKey( ByteUtil.fromString(query) ) && query.equals( vocabulary.getKeyString() ) ) {
            CountIterator iterator = (CountIterator) vocabulary.getValueIterator();
            ScoringContext sc = new ScoringContext();
            while ( !iterator.isDone() ) {
                sc.document = iterator.currentCandidate();
                String docID = diskIndex.getName( sc.document );
                list.put(Long.toString(sc.document), docID);
                iterator.movePast(iterator.currentCandidate());
            }

        }
        post.close();
        diskIndex.close();
        return list;
    }
}
