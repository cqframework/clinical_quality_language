package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.LibraryManager;
import org.fhir.ucum.UcumService;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Environment {
    private static Logger logger = LoggerFactory.getLogger(Environment.class);
    private LibraryManager libraryManager;

    private Map<String, DataProvider> dataProviders;
    private TerminologyProvider terminologyProvider;
    private UcumService ucumService;

    //External function provider

    public Environment(LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
    }
    public Environment(LibraryManager libraryManager, Map<String, DataProvider> dataProviders, TerminologyProvider terminologyProvider) {
        this.libraryManager = libraryManager;
        this.dataProviders = dataProviders;
        this.terminologyProvider = terminologyProvider;
    }

    public static Environment newInstance(LibraryManager libraryManager, Map<String, DataProvider> dataProviders, TerminologyProvider terminologyProvider) {
        return new Environment(libraryManager, dataProviders, terminologyProvider);
    }
    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    public void setLibraryManager(LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
    }

    public Map<String, DataProvider> getDataProviders() {
        return dataProviders;
    }

    public void setDataProviders(Map<String, DataProvider> dataProviders) {
        this.dataProviders = dataProviders;
    }

    public TerminologyProvider getTerminologyProvider() {
        return terminologyProvider;
    }

    public void setTerminologyProvider(TerminologyProvider terminologyProvider) {
        this.terminologyProvider = terminologyProvider;
    }

    public UcumService getUcumService() {
        return ucumService;
    }

    public void setUcumService(UcumService ucumService) {
        this.ucumService = ucumService;
    }

}
