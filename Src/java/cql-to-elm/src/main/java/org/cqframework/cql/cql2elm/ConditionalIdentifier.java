package org.cqframework.cql.cql2elm;

import org.apache.commons.lang3.tuple.Pair;
import org.cqframework.cql.cql2elm.model.Match;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionalIdentifier {

    private Map<Match, Object> conditionals;

    private ConditionalIdentifier(){

    }

    public ConditionalIdentifier(List<Pair<Match, Object>> conditionals){
        this.conditionals = new HashMap<>();
        for (Pair<Match, Object> pairs : conditionals){
            this.conditionals.put(pairs.getLeft(), pairs.getRight());
        }
    }

    public Object getConditionalIdentifier(Match matchCase) {
        if (this.conditionals.containsKey(matchCase)){
            return this.conditionals.get(matchCase);
        }

        return null;
    }

}
