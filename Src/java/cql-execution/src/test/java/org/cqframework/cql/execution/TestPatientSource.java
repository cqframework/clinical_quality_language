package org.cqframework.cql.execution;

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
    public static final int maxPatients = 5;

    private int id = 1;
    private Random random = new Random(0l);
    private String[] surnames = {"Smith", "Johnson", "Lee", "Jackson", "Jones", "Gonzalez"};

    private String currentPatient = generatePatient();

    private JsonParser parser = null;

    public void initialize(Context context, Scriptable scope)
    {
        parser = new JsonParser(context, scope);
    }

    public String currentPatient()
    {
        return currentPatient;
    }

    public String nextPatient()
    {
        this.id++;

        if(id > maxPatients) {
            currentPatient = null;
        } else {
            currentPatient = generatePatient();
        }

        return currentPatient;
    }

    @Override
    public NativeObject shift()
    {
        if(currentPatient == null) return null;

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

        return String.format(" {%n" +
                "  \"resourceType\": \"Bundle\",%n" +
                "  \"id\": \"example%s\",%n" +
                "  \"meta\": {%n" +
                "    \"versionId\": \"1\",%n" +
                "    \"lastUpdated\": \"2014-08-18T01:43:30Z\"%n" +
                "  },%n" +
                "  \"base\": \"http://example.com/base\",%n" +
                "  \"entry\" : [{%n" +
                "        \"resource\": {%n" +
                "        \"id\" : \"%s\",%n" +
                "        \"meta\" :{ \"profile\" : [\"cqf-patient\"]},%n" +
                "        \"resourceType\" : \"Patient\",%n" +
                "        \"identifier\": [{ \"value\": \"1\" }],%n" +
                "        \"name\": {\"given\":[\"%s\"], \"family\": [\"%s\"]},%n" +
                "        \"gender\": \"%s\",%n" +
                "        \"birthDate\" : \"%04d-%02d-%02dT%02d:%02d\"}%n" +
                "        }%n" +
                "  ]%n" +
                "}", id, id, initial, surname, gender, year, month, day, hour, minute);
    }

    /**
     * Pad integers to always have two characters. Numbers under 10 will have a leading zero (e.g. "08")
     */
    private String pad(int value) {
        if(value < 10) {
            return "0" + value;
        } else {
            return "" + value;
        }
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
}
