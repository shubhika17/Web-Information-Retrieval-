package org.lemurproject.galago.core.retrieval.iterator;
import org.lemurproject.galago.core.retrieval.processing.ScoringContext;
import org.lemurproject.galago.core.retrieval.query.NodeParameters;
import java.io.IOException;

/**
 * Created by Shubhika on 11/12/2016.
 */
public class TFScoringIterator  extends ScoringFunctionIterator{
    public TFScoringIterator(NodeParameters np, LengthsIterator lengths, CountIterator iterator) throws IOException {
        super(np, lengths, iterator);
    }

    @Override
    public double score(ScoringContext sc){
        double score = countIterator.count(sc);
        return score;
    }
}
