package org.lemurproject.galago.core.retrieval.iterator;
import org.lemurproject.galago.core.retrieval.processing.ScoringContext;
import org.lemurproject.galago.core.retrieval.query.NodeParameters;

/**
 * Created by Shubhika on 11/21/2016.
 */
import org.lemurproject.galago.core.retrieval.RequiredStatistics;
import java.io.IOException;

@RequiredStatistics(statistics = {"collectionLength", "documentCount", "nodeFrequency", "nodeDocumentCount"})
public class logTFIDFScoringIterator extends ScoringFunctionIterator{
    public logTFIDFScoringIterator(NodeParameters np, LengthsIterator lengths, CountIterator iterator) throws IOException {
        super(np, lengths, iterator);
    }
    @Override
    public double score(ScoringContext sc){
        double total;
        double score = Math.log(countIterator.count(sc) + 1);
        double documentCount = np.getDouble("documentCount");
        double nodeDocumentCount = np.getDouble("nodeDocumentCount");
        double df = Math.log(documentCount/nodeDocumentCount);
        total = score*df;
        return total;
    }
}
