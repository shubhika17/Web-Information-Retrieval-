package org.lemurproject.galago.core.retrieval.iterator;
import org.lemurproject.galago.core.retrieval.RequiredStatistics;
import org.lemurproject.galago.core.retrieval.processing.ScoringContext;
import org.lemurproject.galago.core.retrieval.query.NodeParameters;

import java.io.IOException;

/**
 * Created by Shubhika on 11/21/2016.
 */
@RequiredStatistics(statistics = {"collectionLength", "documentCount", "nodeFrequency", "nodeDocumentCount"})
public class TFIDFScoringIterator extends ScoringFunctionIterator{

    public TFIDFScoringIterator(NodeParameters np, LengthsIterator lengths, CountIterator iterator) throws IOException {
        super(np, lengths, iterator);
    }

    @Override
    public double score(ScoringContext sc){
        double total;
        double score = countIterator.count(sc);
        double documentCount = np.getDouble("documentCount");
        double nodeDocumentCount = np.getDouble("nodeDocumentCount");
        double df = Math.log(documentCount/nodeDocumentCount);
        total = score*df;
        return total;
    }
}
