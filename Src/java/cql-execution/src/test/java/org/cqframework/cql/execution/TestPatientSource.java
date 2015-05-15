package org.cqframework.cql.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.json.JsonParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestPatientSource implements PatientSource 
{
    public static final int MAX_PATIENTS = 5;

    private int id = 1;
    private Random random = new Random(0l);
    private String[] surnames = {"Smith", "Johnson", "Lee", "Jackson", "Jones", "Gonzalez"};

    private String currentPatient = generatePatient();

    private JsonParser parser = null;

    public void initialize(Context context, Scriptable scope)
    {
        parser = new JsonParser(context, scope);
    }

    public String nextPatient()
    {
        this.id++;

        if(id > MAX_PATIENTS) {
            currentPatient = null;
        } else {
            currentPatient = generatePatient();
        }

        return currentPatient;
    }

    @Override
    public NativeObject shift()
    {
        if(currentPatient == null) {
            return null;
        }

        NativeObject obj = null;

        try {
            obj = (NativeObject) parser.parseValue(currentPatient);
        } catch (Exception e) {
            if(parser == null) {
                System.err.println("TestPatientSource must be initialized with a Context and Scope.");
            }
            e.printStackTrace();
        }

        nextPatient();

        return obj;
    }

    @Override
    public void reset()
    {
        this.id = 1;
        this.random = new Random(0l);
        this.currentPatient = generatePatient();
    }
    
    public String loadResourceAsString(String resource)
    {
        StringBuffer template = new StringBuffer();
        try {
            URL address = TestPatientSource.class.getResource( resource );
            File file = new File( address.toURI() );
            FileInputStream fis = new FileInputStream( file );
            InputStreamReader reader = new InputStreamReader( fis, "UTF-8" );
            BufferedReader buffer = new BufferedReader( reader );
            
            String line = null;
            while( (line = buffer.readLine()) != null) {
                template.append(line).append("\n");
            }
            
            reader.close();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return template.toString();
    }
    
    /*
     * Generate a JSON representation of a patient.
        {
            "identifier": { "value": "1" },
            "name": "John Smith",
            "gender": "M",
            "birthDate" : "1980-02-17T06:15",
        }
     */
    private String generatePatient()
    {
        char initial = (char) (random.nextInt(26) + 'A');
        String surname = surnames[ random.nextInt( surnames.length ) ];
        String gender = random.nextBoolean() ? "M" : "F";

        int year = 1980 + random.nextInt(30);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        
        StringBuilder entries = new StringBuilder();
        
        // Add a condition
        String i = "1";
        String code = "J02.8";
        String sys = "2.16.840.1.113883.3.464.1003.102.12.1011"; // Acute Pharyngitis
        String start = "2010-01-01T00:00:00.000Z"; //"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        String end = "2015-01-01T00:00:00.000Z"; //"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        entries.append(", ").append( generateCondition(i,code,sys,start,end) );
        
        // Add an antibiotic
        i = "2";
        code = "1013659";
        sys = "2.16.840.1.113883.3.464.1003.196.12.1001"; // Antibiotic Medications
        entries.append(", ").append( generateMedication(i,code,sys) );

        // Add a prescription for the antibiotic
        String written = "2010-01-01T00:00:00.000Z"; //"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        entries.append(", ").append( generateMedicationPrescription("3",String.format("%s",id),"2", written) );
       
        // Add an encounter
        i = "4";
        code = "99201";
        sys = "2.16.840.1.113883.3.464.1003.101.12.1061"; // Ambulatory/ED Visit
        start = "2010-01-01T00:00:00.000Z"; //"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        end = "2015-01-01T00:00:00.000Z"; //"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        entries.append(", ").append( generateEncounter(i, code, sys, start, end));
        
        // Add a diagnostic report
        i = "5";
        code = "J03.80";
        sys = "2.16.840.1.113883.3.464.1003.198.12.1012"; // Group A Streptococcus Test
        end = "2015-01-01T00:00:00.000Z"; //"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        entries.append(", ").append( generateDiagnosticReport(i, String.format("%s",id), code, sys, end) );
               
        String resource = loadResourceAsString("patient_template.json");
        resource = resource.replace("$PATIENT$", String.format("%d", id));
        resource = resource.replace("$GIVEN$", String.format("%s", initial));
        resource = resource.replace("$FAMILY$", surname);
        resource = resource.replace("$GENDER$", gender);
        resource = resource.replace("$BIRTHDATE$", String.format("%04d-%02d-%02dT%02d:%02d", year, month, day, hour, minute));
        resource = resource.replace("$ENTRIES$", entries.toString());
        return resource;
    }
    
    /**
     * Generate an encounter for this patient.
     * @param id The encounter id.
     * @param code Something like 'J02.8' or '406547006'
     * @param system Something like '2.16.840.1.113883.3.464.1003.102.12.1011'
     * @param start Something like '2011-03-15T15:00'
     * @param end Something like '2011-03-15T15:30'
     * @return
     */
    private String generateEncounter(String id, String code, String system, String start, String end)
    {
        String resource = loadResourceAsString("encounter_template.json");
        resource = resource.replace("$ID$", id);
        resource = resource.replace("$SYSTEM$", system);
        resource = resource.replace("$CODE$", code);
        resource = resource.replace("$START$", start);
        resource = resource.replace("$END$", end);
        return resource;
    }
    
    /**
     * Generate a condition for this patient.
     * @param id The condition id.
     * @param code Something like '109962001'
     * @param system Something like '2.16.840.1.113883.6.96'
     * @param onset Something like '2010-10-24'
     * @param abatement Something like '2010-10-26'
     * @return
     */
    private String generateCondition(String id, String code, String system, String onset, String abatement)
    {
        String resource = loadResourceAsString("condition_template.json");
        resource = resource.replace("$ID$", id);
        resource = resource.replace("$SYSTEM$", system);
        resource = resource.replace("$CODE$", code);
        resource = resource.replace("$ONSET$", onset);
        resource = resource.replace("$ABATEMENT$", abatement);
        return resource;
    }
    
    /**
     * Generate a medication.
     * @param id The medication id.
     * @param code Something like '1013659'
     * @param system Something like '2.16.840.1.113883.3.464.1003.196.12.1001' (antibotics)
     * @return
     */
    private String generateMedication(String id, String code, String system)
    {
        String resource = loadResourceAsString("medication_template.json");
        resource = resource.replace("$ID$", id);
        resource = resource.replace("$SYSTEM$", system);
        resource = resource.replace("$CODE$", code);
        return resource;
    }
    
    /**
     * Generate a medication prescription for this patient.
     * @param id The medication prescription id.
     * @param patientId The patient id.
     * @param medicationId The medication id.
     * @return
     */
    private String generateMedicationPrescription(String id, String patientId, String medicationId, String dateTime)
    {
        String resource = loadResourceAsString("medication_prescription_template.json");
        resource = resource.replace("$ID$", id);
        resource = resource.replace("$PATIENT$", patientId);
        resource = resource.replace("$MEDICATION$", medicationId);
        resource = resource.replace("$DATETIME$", dateTime);
        return resource;
    }
    
    /**
     * Generate a diagnostic report for the patient.
     * @param id The diagnostic report id.
     * @param patientId The patient id.
     * @param code The report code.
     * @param system The report system.
     * @param dateTime The date the report was conducted.
     * @return
     */
    private String generateDiagnosticReport(String id, String patientId, String code, String system, String dateTime)
    {
        String resource = loadResourceAsString("medication_prescription_template.json");
        resource = resource.replace("$ID$", id);
        resource = resource.replace("$PATIENT$", patientId);
        resource = resource.replace("$SYSTEM$", system);
        resource = resource.replace("$CODE$", code);
        resource = resource.replace("$DATETIME$", dateTime);
        return resource;
    }

    @Test
    public void testPatientSource()
    {
        TestPatientSource source = new TestPatientSource();
        ObjectMapper mapper = new ObjectMapper();
        String patient = null;

        while( (patient = source.nextPatient()) != null )
        {
            try {
                mapper.readTree(patient);
            } catch (Exception e) {
                Assert.fail(e.getLocalizedMessage());
                return;
            }
        }
    }
    
    /**
     * Use this TestPatientSource with the Engine. Read Patient bundles, access
     * attributes, records, dates, etc. Check that dates parse correctly.
     */
    @Test
    public void testPatientJavascript()
    {
        // Configure the engine with test data
        Engine.setPatientSource(new TestPatientSource());

        // Run a script that reads patient records 
        // and adds items into the Engine results set
        String javascript = loadResourceAsString("test_patient_record.js");
        Results results = null;
        try {
            results = Engine.executeJson(javascript);
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
        assertThat(results, is (notNullValue()));
        assertThat(results.results.size(), is (TestPatientSource.MAX_PATIENTS));
        // TODO check attributes, records, dates, etc.
    }
}
