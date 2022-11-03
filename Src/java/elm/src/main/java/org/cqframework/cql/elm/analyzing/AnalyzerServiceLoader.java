package org.cqframework.cql.elm.analyzing;

import java.util.Iterator;
import java.util.ServiceLoader;

public class AnalyzerServiceLoader {

    public static Iterator<Analyzer> getAnalyzers(boolean reload) {
        ServiceLoader<Analyzer> serviceLoader = ServiceLoader.load(Analyzer.class);
        if(reload) {
            serviceLoader.reload();
        }
        return serviceLoader.iterator();
    }
}
