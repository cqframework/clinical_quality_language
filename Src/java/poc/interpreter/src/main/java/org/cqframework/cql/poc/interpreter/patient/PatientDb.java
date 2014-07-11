package org.cqframework.cql.poc.interpreter.patient;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * An embarrassingly simple patient database based on a properties file.  Don't ever do this.
 */
public class PatientDb {
    private static final PatientDb INSTANCE = new PatientDb();
    private static final SimpleDateFormat BIRTHDATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final Properties properties;

    private PatientDb() {
        properties = new Properties();
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream("/patientdb.properties");
            properties.load(in);
        } catch (IOException e) {
            System.err.println("WARNING: Failed to load patient database!");
            e.printStackTrace();
        } finally {
            if (in != null) {
                try { in.close(); } catch (IOException e) { /* who cares? */ }
            }
        }
    }

    public static PatientDb instance() {
        return INSTANCE;
    }

    public List<Patient> getPatients() {
        ArrayList<Patient> patients = new ArrayList<>();
        for (int i = 1; getPatientId(i) != null; i++) {
            patients.add(new Patient(getPatientId(i), getPatientName(i), getPatientBirthdate(i)));
        }

        return patients;
    }

    private Integer getPatientId(int i) {
        String idStr = properties.getProperty(String.format("patient.%d.id", i));
        return idStr != null ? Integer.valueOf(idStr) : null;
    }

    private String getPatientName(int i) {
        return properties.getProperty(String.format("patient.%d.name", i));
    }

    private Date getPatientBirthdate(int i) {
        String bdStr = properties.getProperty(String.format("patient.%d.birthdate", i));
        try {
            return bdStr != null ? BIRTHDATE_FORMAT.parse(bdStr) : null;
        } catch (ParseException e) {
            System.err.println("Invalid date format: " + bdStr);
            return null;
        }
    }
}
