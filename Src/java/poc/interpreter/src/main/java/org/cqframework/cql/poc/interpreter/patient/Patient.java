package org.cqframework.cql.poc.interpreter.patient;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple patient bean supporting only basic patient data.  Not very useful in the real world, but useful for a
 * proof of concept!
 */
public class Patient {
    private final int id;
    private final String name;
    private final Date birthdate;

    public Patient(int id, String name, Date birthdate) {
        this.id = id;
        this.name = name;
        this.birthdate = birthdate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public int getAgeAt(Date d) {
        Calendar date = Calendar.getInstance();
        date.setTime(d);
        Calendar dob = Calendar.getInstance();
        dob.setTime(birthdate);

        int age = date.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (date.get(Calendar.MONTH) < dob.get(Calendar.MONTH))
            age --;
        else if (date.get(Calendar.MONTH) == dob.get(Calendar.MONTH) && date.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH))
            age --;

        return age;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthdate=" + birthdate +
                '}';
    }
}
