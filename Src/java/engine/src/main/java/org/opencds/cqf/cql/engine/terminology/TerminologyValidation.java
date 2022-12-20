package org.opencds.cqf.cql.engine.terminology;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TerminologyValidation {
    protected static Set<String> systems = new HashSet<>(Arrays.asList("http://snomed.info/sct", "http://loinc.org", "http://unitsofmeasure.org",
            "http://www.nlm.nih.gov/research/umls/rxnorm", "http://ncimeta.nci.nih.gov", "http://www.ama-assn.org/go/cpt",
            "http://hl7.org/fhir/ndfrt", "http://fdasis.nlm.nih.gov", "http://hl7.org/fhir/sid/ndc", "http://hl7.org/fhir/sid/cvx",
            "http://www.nubc.org/patient-discharge", "http://www.radlex.org", "http://hl7.org/fhir/sid/icd-10", "http://hl7.org/fhir/sid/icd-10-de",
            "http://hl7.org/fhir/sid/icd-10-nl", "http://hl7.org/fhir/sid/icd-10-us", "http://www.icd10data.com/icd10pcs", "http://hl7.org/fhir/sid/icd-9-cm",
            "http://hl7.org/fhir/sid/icd-9-cm/diagnosis", "http://hl7.org/fhir/sid/icd-9-cm/procedure", "http://hl7.org/fhir/sid/icpc-1",
            "http://hl7.org/fhir/sid/icpc-1-nl", "http://hl7.org/fhir/sid/icpc-2", "http://hl7.org/fhir/sid/icf-nl",
            "http://www.whocc.no/atc", "http://nema.org/dicom/dicm", "http://hl7.org/fhir/sid/ca-hc-din", "http://nucc.org/provider-taxonomy",
            "http://www.genenames.org", "http://www.ensembl.org", "http://www.ncbi.nlm.nih.gov/nuccore", "http://www.ncbi.nlm.nih.gov/clinvar",
            "http://sequenceontology.org", "http://www.hgvs.org/mutnomen", "http://www.ncbi.nlm.nih.gov/projects/SNP",
            "http://cancer.sanger.ac.uk/cancergenome/projects/cosmic", "http://www.lrg-sequence.org", "http://www.omim.org",
            "http://www.ncbi.nlm.nih.gov/pubmed", "http://www.pharmgkb.org", "http://clinicaltrials.gov", "http://www.ebi.ac.uk/ipd/imgt/hla/"));

    private TerminologyValidation() {
    }

    public static void addSystem(String system) {
        systems.add(system);
    }

    public static Set<String> getSystems() {
        return systems;
    }

    public static void setSystems(List<String> newSystems) {
        systems = new HashSet<>(newSystems);
    }

    public static boolean hasSystem(String system) {
        return systems.contains(system);
    }
}
