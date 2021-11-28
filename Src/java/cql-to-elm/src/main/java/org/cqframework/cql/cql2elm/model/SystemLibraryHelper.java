package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.cql2elm.TypeBuilder;
import org.hl7.cql.model.*;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.OperandDef;
import org.hl7.elm.r1.TypeSpecifier;
import org.hl7.elm.r1.VersionedIdentifier;

public class SystemLibraryHelper {
    public static TranslatedLibrary load(SystemModel systemModel, TypeBuilder tb) {
        TranslatedLibrary system = new TranslatedLibrary();
        system.setIdentifier(new VersionedIdentifier().withId("System").withVersion("1.0"));

        // Logical Operators
        add(system, tb, new Operator("And", new Signature(systemModel.getBoolean(), systemModel.getBoolean()), systemModel.getBoolean()));
        add(system, tb, new Operator("Or", new Signature(systemModel.getBoolean(), systemModel.getBoolean()), systemModel.getBoolean()));
        add(system, tb, new Operator("Xor", new Signature(systemModel.getBoolean(), systemModel.getBoolean()), systemModel.getBoolean()));
        add(system, tb, new Operator("Not", new Signature(systemModel.getBoolean()), systemModel.getBoolean()));

        // Nullological Operators
        add(system, tb, new Operator("IsNull", new Signature(systemModel.getAny()), systemModel.getBoolean()));
        add(system, tb, new Operator("IsTrue", new Signature(systemModel.getBoolean()), systemModel.getBoolean()));
        add(system, tb, new Operator("IsFalse", new Signature(systemModel.getBoolean()), systemModel.getBoolean()));
        // Coalesce<T>(list<T>)
        // Coalesce<T>(T, T)
        // Coalesce<T>(T, T, T)
        // Coalesce<T>(T, T, T, T)
        // Coalesce<T>(T, T, T, T, T)
        add(system, tb, new GenericOperator("Coalesce", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        add(system, tb, new GenericOperator("Coalesce", new Signature(new TypeParameter("T"), new TypeParameter("T")), new TypeParameter("T"), new TypeParameter("T")));
        add(system, tb, new GenericOperator("Coalesce", new Signature(new TypeParameter("T"), new TypeParameter("T"), new TypeParameter("T")), new TypeParameter("T"), new TypeParameter("T")));
        add(system, tb, new GenericOperator("Coalesce", new Signature(new TypeParameter("T"), new TypeParameter("T"), new TypeParameter("T"), new TypeParameter("T")), new TypeParameter("T"), new TypeParameter("T")));
        add(system, tb, new GenericOperator("Coalesce", new Signature(new TypeParameter("T"), new TypeParameter("T"), new TypeParameter("T"), new TypeParameter("T"), new TypeParameter("T")), new TypeParameter("T"), new TypeParameter("T")));

        // Conversion Operators
        // ToString(Boolean) : String
        // ToString(Integer) : String
        // ToString(Long) : String
        // ToString(Decimal) : String
        // ToString(DateTime) : String
        // ToString(Date) : String
        // ToString(Time) : String
        // ToString(Quantity) : String
        // ToString(Ratio) : String
        // ToString(String) : String
        Operator booleanToString = new Operator("ToString", new Signature(systemModel.getBoolean()), systemModel.getString());
        add(system, tb, booleanToString);
        add(system, tb, new Conversion(booleanToString, false));
        Operator integerToString = new Operator("ToString", new Signature(systemModel.getInteger()), systemModel.getString());
        add(system, tb, integerToString);
        add(system, tb, new Conversion(integerToString, false));
        Operator longToString = new Operator("ToString", new Signature(systemModel.getLong()), systemModel.getString());
        add(system, tb, longToString);
        add(system, tb, new Conversion(longToString, false));
        Operator decimalToString = new Operator("ToString", new Signature(systemModel.getDecimal()), systemModel.getString());
        add(system, tb, decimalToString);
        add(system, tb, new Conversion(decimalToString, false));
        Operator dateTimeToString = new Operator("ToString", new Signature(systemModel.getDateTime()), systemModel.getString());
        add(system, tb, dateTimeToString);
        add(system, tb, new Conversion(dateTimeToString, false));
        Operator dateToString = new Operator("ToString", new Signature(systemModel.getDate()), systemModel.getString());
        add(system, tb, dateToString);
        add(system, tb, new Conversion(dateToString, false));
        Operator timeToString = new Operator("ToString", new Signature(systemModel.getTime()), systemModel.getString());
        add(system, tb, timeToString);
        add(system, tb, new Conversion(timeToString, false));
        Operator quantityToString = new Operator("ToString", new Signature(systemModel.getQuantity()), systemModel.getString());
        add(system, tb, quantityToString);
        add(system, tb, new Conversion(quantityToString, false));
        Operator ratioToString = new Operator("ToString", new Signature(systemModel.getRatio()), systemModel.getString());
        add(system, tb, ratioToString);
        add(system, tb, new Conversion(ratioToString, false));
        //Operator stringToString = new Operator("ToString", new Signature(systemModel.getString()), systemModel.getString());
        //add(system, tb, stringToString);
        //add(system, tb, new Conversion(stringToString, false));

        // ToBoolean(Boolean) : Boolean
        // ToBoolean(Integer) : Boolean
        // ToBoolean(Decimal) : Boolean
        // ToBoolean(Long) : Boolean
        // ToBoolean(String) : Boolean
        Operator stringToBoolean = new Operator("ToBoolean", new Signature(systemModel.getString()), systemModel.getBoolean());
        add(system, tb, stringToBoolean);
        add(system, tb, new Conversion(stringToBoolean, false));
        Operator integerToBoolean = new Operator("ToBoolean", new Signature(systemModel.getInteger()), systemModel.getBoolean());
        add(system, tb, integerToBoolean);
        add(system, tb, new Conversion(integerToBoolean, false));
        Operator decimalToBoolean = new Operator("ToBoolean", new Signature(systemModel.getDecimal()), systemModel.getBoolean());
        add(system, tb, decimalToBoolean);
        add(system, tb, new Conversion(decimalToBoolean, false));
        Operator longToBoolean = new Operator("ToBoolean", new Signature(systemModel.getLong()), systemModel.getBoolean());
        add(system, tb, longToBoolean);
        add(system, tb, new Conversion(longToBoolean, false));
        //Operator booleanToBoolean = new Operator("ToBoolean", new Signature(systemModel.getBoolean()), systemModel.getBoolean());
        //add(system, tb, booleanToBoolean);
        //add(system, tb, new Conversion(booleanToBoolean, false));

        // ToChars(String) : List(String)
        Operator toChars = new Operator("ToChars", new Signature(systemModel.getString()), new ListType(systemModel.getString()));
        add(system, tb, toChars);
        add(system, tb, new Conversion(toChars, false));

        // ToInteger(String) : Integer
        // ToInteger(Boolean) : Integer
        // ToInteger(Long) : Integer
        // ToInteger(Integer) : Integer
        Operator stringToInteger = new Operator("ToInteger", new Signature(systemModel.getString()), systemModel.getInteger());
        add(system, tb, stringToInteger);
        add(system, tb, new Conversion(stringToInteger, false));
        Operator longToInteger = new Operator("ToInteger", new Signature(systemModel.getLong()), systemModel.getInteger());
        add(system, tb, longToInteger);
        add(system, tb, new Conversion(longToInteger, false));
        Operator booleanToInteger = new Operator("ToInteger", new Signature(systemModel.getBoolean()), systemModel.getInteger());
        add(system, tb, booleanToInteger);
        add(system, tb, new Conversion(booleanToInteger, false));
        //Operator integerToInteger = new Operator("ToInteger", new Signature(systemModel.getInteger()), systemModel.getInteger());
        //add(system, tb, integerToInteger);
        //add(system, tb, new Conversion(integerToInteger, false));

        // ToLong(Boolean) : Long
        // ToLong(String) : Long
        // ToLong(Integer) : Long
        // ToLong(Long) : Long
        Operator stringToLong = new Operator("ToLong", new Signature(systemModel.getString()), systemModel.getLong());
        add(system, tb, stringToLong);
        add(system, tb, new Conversion(stringToLong, false));
        Operator integerToLong = new Operator("ToLong", new Signature(systemModel.getInteger()), systemModel.getLong());
        add(system, tb, integerToLong);
        add(system, tb, new Conversion(integerToLong, true));
        //Operator longToLong = new Operator("ToLong", new Signature(systemModel.getLong()), systemModel.getLong());
        //add(system, tb, longToLong);
        //add(system, tb, new Conversion(longToLong, false));
        Operator booleanToLong = new Operator("ToLong", new Signature(systemModel.getBoolean()), systemModel.getLong());
        add(system, tb, booleanToLong);
        add(system, tb, new Conversion(booleanToLong, false));

        // ToDecimal(Boolean) : Decimal
        // ToDecimal(String) : Decimal
        // ToDecimal(Integer) : Decimal
        // ToDecimal(Long) : Decimal
        // ToDecimal(Decimal) : Decimal
        Operator stringToDecimal = new Operator("ToDecimal", new Signature(systemModel.getString()), systemModel.getDecimal());
        add(system, tb, stringToDecimal);
        add(system, tb, new Conversion(stringToDecimal, false));
        Operator integerToDecimal = new Operator("ToDecimal", new Signature(systemModel.getInteger()), systemModel.getDecimal());
        add(system, tb, integerToDecimal);
        add(system, tb, new Conversion(integerToDecimal, true));
        Operator longToDecimal = new Operator("ToDecimal", new Signature(systemModel.getLong()), systemModel.getDecimal());
        add(system, tb, longToDecimal);
        add(system, tb, new Conversion(longToDecimal, true));
        //Operator decimalToDecimal = new Operator("ToDecimal", new Signature(systemModel.getDecimal()), systemModel.getDecimal());
        //add(system, tb, decimalToDecimal);
        //add(system, tb, new Conversion(decimalToDecimal, false));
        Operator booleanToDecimal = new Operator("ToDecimal", new Signature(systemModel.getBoolean()), systemModel.getDecimal());
        add(system, tb, booleanToDecimal);
        add(system, tb, new Conversion(booleanToDecimal, false));

        // ToDateTime(String) : DateTime
        // ToDateTime(Date) : DateTime
        // ToDateTime(DateTime) : DateTime
        Operator stringToDateTime = new Operator("ToDateTime", new Signature(systemModel.getString()), systemModel.getDateTime());
        add(system, tb, stringToDateTime);
        add(system, tb, new Conversion(stringToDateTime, false));
        Operator dateToDateTime = new Operator("ToDateTime", new Signature(systemModel.getDate()), systemModel.getDateTime());
        add(system, tb, dateToDateTime);
        add(system, tb, new Conversion(dateToDateTime, true));
        //Operator dateTimeToDateTime = new Operator("ToDateTime", new Signature(systemModel.getDateTime()), systemModel.getDateTime());
        //add(system, tb, dateTimeToDateTime);
        //add(system, tb, new Conversion(dateTimeToDateTime, false));

        // ToDate(DateTime) : Date
        // ToDate(String) : Date
        // ToDate(Date) : Date
        Operator stringToDate = new Operator("ToDate", new Signature(systemModel.getString()), systemModel.getDate());
        add(system, tb, stringToDate);
        add(system, tb, new Conversion(stringToDate, false));
        Operator dateTimeToDate = new Operator("ToDate", new Signature(systemModel.getDateTime()), systemModel.getDate());
        add(system, tb, dateTimeToDate);
        add(system, tb, new Conversion(dateTimeToDate, false));
        //Operator dateToDate = new Operator("ToDate", new Signature(systemModel.getDate()), systemModel.getDate());
        //add(system, tb, dateToDate);
        //add(system, tb, new Conversion(dateToDate, false));

        // ToTime(String) : Time
        // ToTime(Time) : Time
        Operator stringToTime = new Operator("ToTime", new Signature(systemModel.getString()), systemModel.getTime());
        add(system, tb, stringToTime);
        add(system, tb, new Conversion(stringToTime, false));
        //Operator timeToTime = new Operator("ToTime", new Signature(systemModel.getTime()), systemModel.getTime());
        //add(system, tb, timeToTime);
        //add(system, tb, new Conversion(timeToTime, false));

        // ToQuantity(String) : Quantity
        // ToQuantity(Integer) : Quantity
        // ToQuantity(Ratio) : Quantity
        // ToQuantity(Decimal) : Quantity
        // ToQuantity(Quantity) : Quantity
        Operator stringToQuantity = new Operator("ToQuantity", new Signature(systemModel.getString()), systemModel.getQuantity());
        add(system, tb, stringToQuantity);
        add(system, tb, new Conversion(stringToQuantity, false));
        Operator ratioToQuantity = new Operator("ToQuantity", new Signature(systemModel.getRatio()), systemModel.getQuantity());
        add(system, tb, ratioToQuantity);
        add(system, tb, new Conversion(ratioToQuantity, false));
        Operator integerToQuantity = new Operator("ToQuantity", new Signature(systemModel.getInteger()), systemModel.getQuantity());
        add(system, tb, integerToQuantity);
        add(system, tb, new Conversion(integerToQuantity, true));
        Operator decimalToQuantity = new Operator("ToQuantity", new Signature(systemModel.getDecimal()), systemModel.getQuantity());
        add(system, tb, decimalToQuantity);
        add(system, tb, new Conversion(decimalToQuantity, true));
        //Operator quantityToQuantity = new Operator("ToQuantity", new Signature(systemModel.getQuantity()), systemModel.getQuantity());
        //add(system, tb, quantityToQuantity);
        //add(system, tb, new Conversion(quantityToQuantity, false));

        // ToRatio(String) : Ratio
        // ToRatio(Ratio) : Ratio
        Operator stringToRatio = new Operator("ToRatio", new Signature(systemModel.getString()), systemModel.getRatio());
        add(system, tb, stringToRatio);
        add(system, tb, new Conversion(stringToRatio, false));
        //Operator ratioToRatio = new Operator("ToRatio", new Signature(systemModel.getRatio()), systemModel.getRatio());
        //add(system, tb, ratioToRatio);
        //add(system, tb, new Conversion(ratioToRatio, false));

        // ConvertsToBoolean(Any): Boolean
        Operator convertsTo = new Operator("ConvertsToBoolean", new Signature(systemModel.getAny()), systemModel.getBoolean());
        add(system, tb, convertsTo);
        // ConvertsToInteger(Any): Boolean
        convertsTo = new Operator("ConvertsToInteger", new Signature(systemModel.getAny()), systemModel.getBoolean());
        add(system, tb, convertsTo);
        // ConvertsToLong(Any): Boolean
        convertsTo = new Operator("ConvertsToLong", new Signature(systemModel.getAny()), systemModel.getBoolean());
        add(system, tb, convertsTo);
        // ConvertsToDecimal
        convertsTo = new Operator("ConvertsToDecimal", new Signature(systemModel.getAny()), systemModel.getBoolean());
        add(system, tb, convertsTo);
        // ConvertsToDateTime
        convertsTo = new Operator("ConvertsToDateTime", new Signature(systemModel.getAny()), systemModel.getBoolean());
        add(system, tb, convertsTo);
        // ConvertsToDate
        convertsTo = new Operator("ConvertsToDate", new Signature(systemModel.getAny()), systemModel.getBoolean());
        add(system, tb, convertsTo);
        // ConvertsToTime
        convertsTo = new Operator("ConvertsToTime", new Signature(systemModel.getAny()), systemModel.getBoolean());
        add(system, tb, convertsTo);
        // ConvertsToString
        convertsTo = new Operator("ConvertsToString", new Signature(systemModel.getAny()), systemModel.getBoolean());
        add(system, tb, convertsTo);
        // ConvertsToQuantity
        convertsTo = new Operator("ConvertsToQuantity", new Signature(systemModel.getAny()), systemModel.getBoolean());
        add(system, tb, convertsTo);
        // ConvertsToRatio
        convertsTo = new Operator("ConvertsToRatio", new Signature(systemModel.getAny()), systemModel.getBoolean());
        add(system, tb, convertsTo);

        // CanConvertQuantity
        Operator canConvertToQuantity = new Operator("CanConvertQuantity", new Signature(systemModel.getQuantity(), systemModel.getString()), systemModel.getBoolean());
        add(system, tb, canConvertToQuantity);

        // ConvertQuantity
        Operator convertToQuantity = new Operator("ConvertQuantity", new Signature(systemModel.getQuantity(), systemModel.getString()), systemModel.getQuantity());
        add(system, tb, convertToQuantity);

        // Comparison Operators
        // Equal<T : value>(T, T) : Boolean
        //TypeParameter T = new TypeParameter("T", TypeParameter.TypeParameterConstraint.VALUE, null);
        //add(system, tb, new GenericOperator("Equal", new Signature(T, T), systemModel.getBoolean(), T));
        // Equal<C : class>(C, C) : Boolean
        TypeParameter C = new TypeParameter("C", TypeParameter.TypeParameterConstraint.CLASS, null);
        add(system, tb, new GenericOperator("Equal", new Signature(C, C), systemModel.getBoolean(), C));
        // Equal<R : tuple>(R, R) : Boolean
        TypeParameter R = new TypeParameter("R", TypeParameter.TypeParameterConstraint.TUPLE, null);
        add(system, tb, new GenericOperator("Equal", new Signature(R, R), systemModel.getBoolean(), R));
        // Equal<H : choice>(H, H) : Boolean
        TypeParameter H = new TypeParameter("H", TypeParameter.TypeParameterConstraint.CHOICE, null);
        add(system, tb, new GenericOperator("Equal", new Signature(H, H), systemModel.getBoolean(), H));
        // Equal(Any, Any) : Boolean
        //add(system, tb, new Operator("Equal", new Signature(systemModel.getAny(), systemModel.getAny()), systemModel.getBoolean()));
        // Equivalent<T : value>(T, T) : Boolean
        //T = new TypeParameter("T", TypeParameter.TypeParameterConstraint.VALUE, null);
        //add(system, tb, new GenericOperator("Equivalent", new Signature(T, T), systemModel.getBoolean(), T));
        // Equivalent<C : class>(C, C) : Boolean
        C = new TypeParameter("C", TypeParameter.TypeParameterConstraint.CLASS, null);
        add(system, tb, new GenericOperator("Equivalent", new Signature(C, C), systemModel.getBoolean(), C));
        // Equivalent<R : tuple>(R, R) : Boolean
        R = new TypeParameter("R", TypeParameter.TypeParameterConstraint.TUPLE, null);
        add(system, tb, new GenericOperator("Equivalent", new Signature(R, R), systemModel.getBoolean(), R));
        // Equivalent<H : choice>(H, H) : Boolean
        H = new TypeParameter("H", TypeParameter.TypeParameterConstraint.CHOICE, null);
        add(system, tb, new GenericOperator("Equivalent", new Signature(H, H), systemModel.getBoolean(), H));
        // Equivalent(Any, Any) : Boolean
        //add(system, tb, new Operator("Equivalent", new Signature(systemModel.getAny(), systemModel.getAny()), systemModel.getBoolean()));

        add(system, tb, new Operator("Equal", new Signature(systemModel.getBoolean(), systemModel.getBoolean()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equivalent", new Signature(systemModel.getBoolean(), systemModel.getBoolean()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equal", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equivalent", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        add(system, tb, new Operator("Less", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        add(system, tb, new Operator("LessOrEqual", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        add(system, tb, new Operator("Greater", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        add(system, tb, new Operator("GreaterOrEqual", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equal", new Signature(systemModel.getLong(), systemModel.getLong()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equivalent", new Signature(systemModel.getLong(), systemModel.getLong()), systemModel.getBoolean()));
        add(system, tb, new Operator("Less", new Signature(systemModel.getLong(), systemModel.getLong()), systemModel.getBoolean()));
        add(system, tb, new Operator("LessOrEqual", new Signature(systemModel.getLong(), systemModel.getLong()), systemModel.getBoolean()));
        add(system, tb, new Operator("Greater", new Signature(systemModel.getLong(), systemModel.getLong()), systemModel.getBoolean()));
        add(system, tb, new Operator("GreaterOrEqual", new Signature(systemModel.getLong(), systemModel.getLong()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equal", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equivalent", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        add(system, tb, new Operator("Less", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        add(system, tb, new Operator("LessOrEqual", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        add(system, tb, new Operator("Greater", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        add(system, tb, new Operator("GreaterOrEqual", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equal", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equivalent", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        add(system, tb, new Operator("Less", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        add(system, tb, new Operator("LessOrEqual", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        add(system, tb, new Operator("Greater", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        add(system, tb, new Operator("GreaterOrEqual", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equal", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equivalent", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("Less", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("LessOrEqual", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("Greater", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("GreaterOrEqual", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equal", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equivalent", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        add(system, tb, new Operator("Less", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        add(system, tb, new Operator("LessOrEqual", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        add(system, tb, new Operator("Greater", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        add(system, tb, new Operator("GreaterOrEqual", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equal", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equivalent", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("Less", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("LessOrEqual", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("Greater", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("GreaterOrEqual", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equal", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equivalent", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        add(system, tb, new Operator("Less", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        add(system, tb, new Operator("LessOrEqual", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        add(system, tb, new Operator("Greater", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        add(system, tb, new Operator("GreaterOrEqual", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equal", new Signature(systemModel.getRatio(), systemModel.getRatio()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equivalent", new Signature(systemModel.getRatio(), systemModel.getRatio()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equal", new Signature(systemModel.getCode(), systemModel.getCode()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equivalent", new Signature(systemModel.getCode(), systemModel.getCode()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equal", new Signature(systemModel.getConcept(), systemModel.getConcept()), systemModel.getBoolean()));
        add(system, tb, new Operator("Equivalent", new Signature(systemModel.getConcept(), systemModel.getConcept()), systemModel.getBoolean()));

        // Arithmetic Operators
        add(system, tb, new Operator("Abs", new Signature(systemModel.getInteger()), systemModel.getInteger()));
        add(system, tb, new Operator("Abs", new Signature(systemModel.getLong()), systemModel.getLong()));
        add(system, tb, new Operator("Abs", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
        add(system, tb, new Operator("Abs", new Signature(systemModel.getQuantity()), systemModel.getQuantity()));

        add(system, tb, new Operator("Add", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        add(system, tb, new Operator("Add", new Signature(systemModel.getLong(), systemModel.getLong()), systemModel.getLong()));
        add(system, tb, new Operator("Add", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        add(system, tb, new Operator("Add", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        add(system, tb, new Operator("Ceiling", new Signature(systemModel.getDecimal()), systemModel.getInteger()));

        add(system, tb, new Operator("Divide", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        //add(system, tb, new Operator("Divide", new Signature(systemModel.getQuantity(), systemModel.getDecimal()), systemModel.getQuantity()));
        add(system, tb, new Operator("Divide", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        add(system, tb, new Operator("Exp", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));

        add(system, tb, new Operator("Floor", new Signature(systemModel.getDecimal()), systemModel.getInteger()));

        add(system, tb, new Operator("HighBoundary", new Signature(systemModel.getDecimal(), systemModel.getInteger()), systemModel.getDecimal()));
        add(system, tb, new Operator("HighBoundary", new Signature(systemModel.getDate(), systemModel.getInteger()), systemModel.getDate()));
        add(system, tb, new Operator("HighBoundary", new Signature(systemModel.getDateTime(), systemModel.getInteger()), systemModel.getDateTime()));
        add(system, tb, new Operator("HighBoundary", new Signature(systemModel.getTime(), systemModel.getInteger()), systemModel.getTime()));

        add(system, tb, new Operator("Log", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));

        add(system, tb, new Operator("LowBoundary", new Signature(systemModel.getDecimal(), systemModel.getInteger()), systemModel.getDecimal()));
        add(system, tb, new Operator("LowBoundary", new Signature(systemModel.getDate(), systemModel.getInteger()), systemModel.getDate()));
        add(system, tb, new Operator("LowBoundary", new Signature(systemModel.getDateTime(), systemModel.getInteger()), systemModel.getDateTime()));
        add(system, tb, new Operator("LowBoundary", new Signature(systemModel.getTime(), systemModel.getInteger()), systemModel.getTime()));

        add(system, tb, new Operator("Ln", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));

        // MaxValue<T>() : T
        // MinValue<T>() : T

        add(system, tb, new Operator("Modulo", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        add(system, tb, new Operator("Modulo", new Signature(systemModel.getLong(), systemModel.getLong()), systemModel.getLong()));
        add(system, tb, new Operator("Modulo", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        add(system, tb, new Operator("Modulo", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        add(system, tb, new Operator("Multiply", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        add(system, tb, new Operator("Multiply", new Signature(systemModel.getLong(), systemModel.getLong()), systemModel.getLong()));
        add(system, tb, new Operator("Multiply", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        add(system, tb, new Operator("Multiply", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        add(system, tb, new Operator("Negate", new Signature(systemModel.getInteger()), systemModel.getInteger()));
        add(system, tb, new Operator("Negate", new Signature(systemModel.getLong()), systemModel.getLong()));
        add(system, tb, new Operator("Negate", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
        add(system, tb, new Operator("Negate", new Signature(systemModel.getQuantity()), systemModel.getQuantity()));

        add(system, tb, new Operator("Precision", new Signature(systemModel.getDecimal()), systemModel.getInteger()));
        add(system, tb, new Operator("Precision", new Signature(systemModel.getDate()), systemModel.getInteger()));
        add(system, tb, new Operator("Precision", new Signature(systemModel.getDateTime()), systemModel.getInteger()));
        add(system, tb, new Operator("Precision", new Signature(systemModel.getTime()), systemModel.getInteger()));

        add(system, tb, new Operator("Predecessor", new Signature(systemModel.getInteger()), systemModel.getInteger()));
        add(system, tb, new Operator("Predecessor", new Signature(systemModel.getLong()), systemModel.getLong()));
        add(system, tb, new Operator("Predecessor", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
        add(system, tb, new Operator("Predecessor", new Signature(systemModel.getDate()), systemModel.getDate()));
        add(system, tb, new Operator("Predecessor", new Signature(systemModel.getDateTime()), systemModel.getDateTime()));
        add(system, tb, new Operator("Predecessor", new Signature(systemModel.getTime()), systemModel.getTime()));
        add(system, tb, new Operator("Predecessor", new Signature(systemModel.getQuantity()), systemModel.getQuantity()));

        add(system, tb, new Operator("Power", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        add(system, tb, new Operator("Power", new Signature(systemModel.getLong(), systemModel.getLong()), systemModel.getLong()));
        add(system, tb, new Operator("Power", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));

        add(system, tb, new Operator("Round", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
        add(system, tb, new Operator("Round", new Signature(systemModel.getDecimal(), systemModel.getInteger()), systemModel.getDecimal()));

        add(system, tb, new Operator("Subtract", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        add(system, tb, new Operator("Subtract", new Signature(systemModel.getLong(), systemModel.getLong()), systemModel.getLong()));
        add(system, tb, new Operator("Subtract", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        add(system, tb, new Operator("Subtract", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        add(system, tb, new Operator("Successor", new Signature(systemModel.getInteger()), systemModel.getInteger()));
        add(system, tb, new Operator("Successor", new Signature(systemModel.getLong()), systemModel.getLong()));
        add(system, tb, new Operator("Successor", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
        add(system, tb, new Operator("Successor", new Signature(systemModel.getDate()), systemModel.getDate()));
        add(system, tb, new Operator("Successor", new Signature(systemModel.getDateTime()), systemModel.getDateTime()));
        add(system, tb, new Operator("Successor", new Signature(systemModel.getTime()), systemModel.getTime()));
        add(system, tb, new Operator("Successor", new Signature(systemModel.getQuantity()), systemModel.getQuantity()));

        add(system, tb, new Operator("Truncate", new Signature(systemModel.getDecimal()), systemModel.getInteger()));

        add(system, tb, new Operator("TruncatedDivide", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        add(system, tb, new Operator("TruncatedDivide", new Signature(systemModel.getLong(), systemModel.getLong()), systemModel.getLong()));
        add(system, tb, new Operator("TruncatedDivide", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        add(system, tb, new Operator("TruncatedDivide", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        // String operators
        add(system, tb, new Operator("Add", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getString()));
        add(system, tb, new Operator("Combine", new Signature(new ListType(systemModel.getString())), systemModel.getString()));
        add(system, tb, new Operator("Combine", new Signature(new ListType(systemModel.getString()), systemModel.getString()), systemModel.getString()));
        add(system, tb, new Operator("Concatenate", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getString()));
        add(system, tb, new Operator("EndsWith", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        add(system, tb, new Operator("Indexer", new Signature(systemModel.getString(), systemModel.getInteger()), systemModel.getString()));
        add(system, tb, new Operator("LastPositionOf", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getInteger()));
        add(system, tb, new Operator("Length", new Signature(systemModel.getString()), systemModel.getInteger()));
        add(system, tb, new Operator("Lower", new Signature(systemModel.getString()), systemModel.getString()));
        add(system, tb, new Operator("Matches", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        add(system, tb, new Operator("PositionOf", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getInteger()));
        add(system, tb, new Operator("ReplaceMatches", new Signature(systemModel.getString(), systemModel.getString(), systemModel.getString()), systemModel.getString()));
        add(system, tb, new Operator("Split", new Signature(systemModel.getString(), systemModel.getString()), new ListType(systemModel.getString())));
        add(system, tb, new Operator("SplitOnMatches", new Signature(systemModel.getString(), systemModel.getString()), new ListType(systemModel.getString())));
        add(system, tb, new Operator("StartsWith", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        add(system, tb, new Operator("Substring", new Signature(systemModel.getString(), systemModel.getInteger()), systemModel.getString()));
        add(system, tb, new Operator("Substring", new Signature(systemModel.getString(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getString()));
        add(system, tb, new Operator("Upper", new Signature(systemModel.getString()), systemModel.getString()));

        // Date/Time Operators
        add(system, tb, new Operator("Add", new Signature(systemModel.getDateTime(), systemModel.getQuantity()), systemModel.getDateTime()));
        add(system, tb, new Operator("Add", new Signature(systemModel.getDate(), systemModel.getQuantity()), systemModel.getDate()));
        add(system, tb, new Operator("Add", new Signature(systemModel.getTime(), systemModel.getQuantity()), systemModel.getTime()));
        add(system, tb, new Operator("After", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("After", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        add(system, tb, new Operator("After", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("Before", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("Before", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        add(system, tb, new Operator("Before", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("DateTime", new Signature(systemModel.getInteger()), systemModel.getDateTime()));
        add(system, tb, new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        add(system, tb, new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        add(system, tb, new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        add(system, tb, new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        add(system, tb, new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        add(system, tb, new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        add(system, tb, new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getDecimal()), systemModel.getDateTime()));
        add(system, tb, new Operator("Date", new Signature(systemModel.getInteger()), systemModel.getDate()));
        add(system, tb, new Operator("Date", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getDate()));
        add(system, tb, new Operator("Date", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDate()));
        add(system, tb, new Operator("DateFrom", new Signature(systemModel.getDateTime()), systemModel.getDate()));
        add(system, tb, new Operator("TimeFrom", new Signature(systemModel.getDateTime()), systemModel.getTime()));
        add(system, tb, new Operator("TimezoneFrom", new Signature(systemModel.getDateTime()), systemModel.getDecimal()));
        add(system, tb, new Operator("TimezoneOffsetFrom", new Signature(systemModel.getDateTime()), systemModel.getDecimal()));
        add(system, tb, new Operator("DateTimeComponentFrom", new Signature(systemModel.getDateTime()), systemModel.getInteger()));
        add(system, tb, new Operator("DateTimeComponentFrom", new Signature(systemModel.getDate()), systemModel.getInteger()));
        add(system, tb, new Operator("DateTimeComponentFrom", new Signature(systemModel.getTime()), systemModel.getInteger()));
        add(system, tb, new Operator("DifferenceBetween", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getInteger()));
        add(system, tb, new Operator("DifferenceBetween", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getInteger()));
        add(system, tb, new Operator("DifferenceBetween", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getInteger()));
        add(system, tb, new Operator("DurationBetween", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getInteger()));
        add(system, tb, new Operator("DurationBetween", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getInteger()));
        add(system, tb, new Operator("DurationBetween", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getInteger()));
        add(system, tb, new Operator("Now", new Signature(), systemModel.getDateTime()));
        add(system, tb, new Operator("SameAs", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("SameAs", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        add(system, tb, new Operator("SameAs", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("SameOrAfter", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("SameOrAfter", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        add(system, tb, new Operator("SameOrAfter", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("SameOrBefore", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("SameOrBefore", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        add(system, tb, new Operator("SameOrBefore", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        add(system, tb, new Operator("Subtract", new Signature(systemModel.getDateTime(), systemModel.getQuantity()), systemModel.getDateTime()));
        add(system, tb, new Operator("Subtract", new Signature(systemModel.getDate(), systemModel.getQuantity()), systemModel.getDate()));
        add(system, tb, new Operator("Subtract", new Signature(systemModel.getTime(), systemModel.getQuantity()), systemModel.getTime()));
        add(system, tb, new Operator("Today", new Signature(), systemModel.getDate()));
        add(system, tb, new Operator("Time", new Signature(systemModel.getInteger()), systemModel.getTime()));
        add(system, tb, new Operator("Time", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getTime()));
        add(system, tb, new Operator("Time", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getTime()));
        add(system, tb, new Operator("Time", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getTime()));
        add(system, tb, new Operator("TimeOfDay", new Signature(), systemModel.getTime()));

        // Interval Operators
        // After<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("After", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Before<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("Before", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Collapse<T>(list<interval<T>>) : list<interval<T>>
        // Collapse<T>(list<interval<T>>, Quantity) : list<interval<T>>
        add(system, tb, new GenericOperator("Collapse", new Signature(new ListType(new IntervalType(new TypeParameter("T"))), systemModel.getQuantity()), new ListType(new IntervalType(new TypeParameter("T"))), new TypeParameter("T")));
        // Contains<T>(interval<T>, T) : Boolean
        add(system, tb, new GenericOperator("Contains", new Signature(new IntervalType(new TypeParameter("T")), new TypeParameter("T")), systemModel.getBoolean(), new TypeParameter("T")));
        // End<T>(interval<T>) : T
        add(system, tb, new GenericOperator("End", new Signature(new IntervalType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Ends<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("Ends", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Equal<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("Equal", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Equivalent<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("Equivalent", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Except<T>(interval<T>, interval<T>) : interval<T>
        add(system, tb, new GenericOperator("Except", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), new IntervalType(new TypeParameter("T")), new TypeParameter("T")));
        // Expand<T>(list<interval<T>>) : list<interval<T>>
        // Expand<T>(list<interval<T>>, Quantity) : list<interval<T>>
        // Expand<T>(interval<T>) : List<T>
        // Expand<T>(interval<T>, Quantity) : list<T>
        add(system, tb, new GenericOperator("Expand", new Signature(new ListType(new IntervalType(new TypeParameter("T"))), systemModel.getQuantity()), new ListType(new IntervalType(new TypeParameter("T"))), new TypeParameter("T")));
        add(system, tb, new GenericOperator("Expand", new Signature(new IntervalType(new TypeParameter("T")), systemModel.getQuantity()), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // In<T>(T, interval<T>) : Boolean
        add(system, tb, new GenericOperator("In", new Signature(new TypeParameter("T"), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Includes<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("Includes", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // IncludedIn<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("IncludedIn", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Intersect<T>(interval<T>, interval<T>) : interval<T>
        add(system, tb, new GenericOperator("Intersect", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), new IntervalType(new TypeParameter("T")), new TypeParameter("T")));
        // Meets<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("Meets", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // MeetsBefore<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("MeetsBefore", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // MeetsAfter<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("MeetsAfter", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Overlaps<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("Overlaps", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // OverlapsBefore<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("OverlapsBefore", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // OverlapsAfter<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("OverlapsAfter", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // PointFrom<T>(interval<T>) : T
        GenericOperator pointFrom = new GenericOperator("PointFrom", new Signature(new IntervalType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T"));
        add(system, tb, pointFrom);
        // ProperContains<T>(interval<T>, T) : Boolean
        add(system, tb, new GenericOperator("ProperContains", new Signature(new IntervalType(new TypeParameter("T")), new TypeParameter("T")), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIn<T>(T, interval<T>) : Boolean
        add(system, tb, new GenericOperator("ProperIn", new Signature(new TypeParameter("T"), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIncludes<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("ProperIncludes", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIncludedIn<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("ProperIncludedIn", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // SameAs<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("SameAs", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // SameOrAfter<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("SameOrAfter", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // SameOrBefore<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("SameOrBefore", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Size<T>(interval<T>) : T
        add(system, tb, new GenericOperator("Size", new Signature(new IntervalType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Start<T>(interval<T>) : T
        add(system, tb, new GenericOperator("Start", new Signature(new IntervalType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Starts<T>(interval<T>, interval<T>) : Boolean
        add(system, tb, new GenericOperator("Starts", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Union<T>(interval<T>, interval<T>) : interval<T>
        add(system, tb, new GenericOperator("Union", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), new IntervalType(new TypeParameter("T")), new TypeParameter("T")));
        // Width<T>(interval<T>) : T
        add(system, tb, new GenericOperator("Width", new Signature(new IntervalType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));

        // List Operators
        // Contains<T>(list<T>, T) : Boolean
        add(system, tb, new GenericOperator("Contains", new Signature(new ListType(new TypeParameter("T")), new TypeParameter("T")), systemModel.getBoolean(), new TypeParameter("T")));
        // Distinct<T>(list<T>) : list<T>
        add(system, tb, new GenericOperator("Distinct", new Signature(new ListType(new TypeParameter("T"))), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Equal<T>(list<T>, list<T>) : Boolean
        add(system, tb, new GenericOperator("Equal", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Equivalent<T>(list<T>, list<T>) : Boolean
        add(system, tb, new GenericOperator("Equivalent", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Except<T>(list<T>, list<T>) : list<T>
        add(system, tb, new GenericOperator("Except", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Exists<T>(list<T>) : Boolean
        add(system, tb, new GenericOperator("Exists", new Signature(new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Flatten<T>(list<list<T>>) : list<T>
        add(system, tb, new GenericOperator("Flatten", new Signature(new ListType(new ListType(new TypeParameter("T")))), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // First<T>(list<T>) : T
        add(system, tb, new GenericOperator("First", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // In<T>(T, list<T>) : Boolean
        add(system, tb, new GenericOperator("In", new Signature(new TypeParameter("T"), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Includes<T>(list<T>, list<T>) : Boolean
        add(system, tb, new GenericOperator("Includes", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // IncludedIn<T>(list<T>, list<T>) : Boolean
        add(system, tb, new GenericOperator("IncludedIn", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Indexer<T>(list<T>, integer) : T
        add(system, tb, new GenericOperator("Indexer", new Signature(new ListType(new TypeParameter("T")), systemModel.getInteger()), new TypeParameter("T"), new TypeParameter("T")));
        // IndexOf<T>(list<T>, T) : Integer
        add(system, tb, new GenericOperator("IndexOf", new Signature(new ListType(new TypeParameter("T")), new TypeParameter("T")), systemModel.getInteger(), new TypeParameter("T")));
        // Intersect<T>(list<T>, list<T>) : list<T>
        add(system, tb, new GenericOperator("Intersect", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Last<T>(list<T>) : T
        add(system, tb, new GenericOperator("Last", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Length<T>(list<T>) : Integer
        add(system, tb, new GenericOperator("Length", new Signature(new ListType(new TypeParameter("T"))), systemModel.getInteger(), new TypeParameter("T")));
        // ProperContains<T>(list<T>, T) : Boolean
        add(system, tb, new GenericOperator("ProperContains", new Signature(new ListType(new TypeParameter("T")), new TypeParameter("T")), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIn<T>(T, list<T>) : Boolean
        add(system, tb, new GenericOperator("ProperIn", new Signature(new TypeParameter("T"), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIncludes<T>(list<T>, list<T>) : Boolean
        add(system, tb, new GenericOperator("ProperIncludes", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIncludedIn<T>(list<T>, list<T>) : Boolean
        add(system, tb, new GenericOperator("ProperIncludedIn", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // SingletonFrom<T>(list<T>) : T
        GenericOperator singletonFrom = new GenericOperator("SingletonFrom", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T"));
        add(system, tb, singletonFrom);
        //// NOTE: FHIRPath Implicit List Demotion
        // Generic conversions turned out to be computationally expensive, so we added explicit list promotion/demotion in the conversion map directly instead.
        //add(system, tb, new Conversion(singletonFrom, true));
        // Skip(list<T>, Integer): list<T>
        add(system, tb, new GenericOperator("Skip", new Signature(new ListType(new TypeParameter("T")), systemModel.getInteger()), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Tail(list<T>): list<T>
        add(system, tb, new GenericOperator("Tail", new Signature(new ListType(new TypeParameter("T"))), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Take(list<T>, Integer): list<T>
        add(system, tb, new GenericOperator("Take", new Signature(new ListType(new TypeParameter("T")), systemModel.getInteger()), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Union<T>(list<T>, list<T>) : list<T>
        add(system, tb, new GenericOperator("Union", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), new ListType(new TypeParameter("T")), new TypeParameter("T")));

        // NOTE: FHIRPath Implicit List Promotion operator
        //GenericOperator toList = new GenericOperator("List", new Signature(new TypeParameter("T")), new ListType(new TypeParameter("T")), new TypeParameter("T"));
        //add(system, tb, toList);
        //add(system, tb, new Conversion(toList, true));

        // Aggregate Operators
        add(system, tb, new Operator("AllTrue", new Signature(new ListType(systemModel.getBoolean())), systemModel.getBoolean()));
        add(system, tb, new Operator("AnyTrue", new Signature(new ListType(systemModel.getBoolean())), systemModel.getBoolean()));
        add(system, tb, new Operator("Avg", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        add(system, tb, new Operator("Avg", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        // Count<T>(list<T>) : Integer
        add(system, tb, new GenericOperator("Count", new Signature(new ListType(new TypeParameter("T"))), systemModel.getInteger(), new TypeParameter("T")));
        //// Count(list<Any>) : Integer
        //add(system, tb, new Operator("Count", new Signature(new ListType(systemModel.getAny())), systemModel.getInteger()));
        add(system, tb, new Operator("GeometricMean", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        add(system, tb, new Operator("Max", new Signature(new ListType(systemModel.getInteger())), systemModel.getInteger()));
        add(system, tb, new Operator("Max", new Signature(new ListType(systemModel.getLong())), systemModel.getLong()));
        add(system, tb, new Operator("Max", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        add(system, tb, new Operator("Max", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        add(system, tb, new Operator("Max", new Signature(new ListType(systemModel.getDateTime())), systemModel.getDateTime()));
        add(system, tb, new Operator("Max", new Signature(new ListType(systemModel.getDate())), systemModel.getDate()));
        add(system, tb, new Operator("Max", new Signature(new ListType(systemModel.getTime())), systemModel.getTime()));
        add(system, tb, new Operator("Max", new Signature(new ListType(systemModel.getString())), systemModel.getString()));
        add(system, tb, new Operator("Min", new Signature(new ListType(systemModel.getInteger())), systemModel.getInteger()));
        add(system, tb, new Operator("Min", new Signature(new ListType(systemModel.getLong())), systemModel.getLong()));
        add(system, tb, new Operator("Min", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        add(system, tb, new Operator("Min", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        add(system, tb, new Operator("Min", new Signature(new ListType(systemModel.getDateTime())), systemModel.getDateTime()));
        add(system, tb, new Operator("Min", new Signature(new ListType(systemModel.getDate())), systemModel.getDate()));
        add(system, tb, new Operator("Min", new Signature(new ListType(systemModel.getTime())), systemModel.getTime()));
        add(system, tb, new Operator("Min", new Signature(new ListType(systemModel.getString())), systemModel.getString()));
        add(system, tb, new Operator("Median", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        add(system, tb, new Operator("Median", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        // Mode<T>(list<T>) : T
        add(system, tb, new GenericOperator("Mode", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        add(system, tb, new Operator("PopulationStdDev", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        add(system, tb, new Operator("PopulationStdDev", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        add(system, tb, new Operator("PopulationVariance", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        add(system, tb, new Operator("PopulationVariance", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        add(system, tb, new Operator("Product", new Signature(new ListType(systemModel.getInteger())), systemModel.getInteger()));
        add(system, tb, new Operator("Product", new Signature(new ListType(systemModel.getLong())), systemModel.getLong()));
        add(system, tb, new Operator("Product", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        add(system, tb, new Operator("Product", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        add(system, tb, new Operator("StdDev", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        add(system, tb, new Operator("StdDev", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        add(system, tb, new Operator("Sum", new Signature(new ListType(systemModel.getInteger())), systemModel.getInteger()));
        add(system, tb, new Operator("Sum", new Signature(new ListType(systemModel.getLong())), systemModel.getLong()));
        add(system, tb, new Operator("Sum", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        add(system, tb, new Operator("Sum", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        add(system, tb, new Operator("Variance", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        add(system, tb, new Operator("Variance", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));

        // Clinical
        // ToConcept(Code)
        Operator codeToConcept = new Operator("ToConcept", new Signature(systemModel.getCode()), systemModel.getConcept());
        add(system, tb, codeToConcept);
        add(system, tb, new Conversion(codeToConcept, true));
        // ToConcept(list<Code>)
        Operator codesToConcept = new Operator("ToConcept", new Signature(new ListType(systemModel.getCode())), systemModel.getConcept());
        add(system, tb, codesToConcept);
        add(system, tb, new Conversion(codesToConcept, false));

        add(system, tb, new Operator("CalculateAge", new Signature(systemModel.getDateTime()), systemModel.getInteger()));
        add(system, tb, new Operator("CalculateAge", new Signature(systemModel.getDate()), systemModel.getInteger()));
        add(system, tb, new Operator("CalculateAgeAt", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getInteger()));
        add(system, tb, new Operator("CalculateAgeAt", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getInteger()));

        add(system, tb, new Operator("InValueSet", new Signature(systemModel.getString()), systemModel.getBoolean()));
        add(system, tb, new Operator("InValueSet", new Signature(systemModel.getCode()), systemModel.getBoolean()));
        add(system, tb, new Operator("InValueSet", new Signature(systemModel.getConcept()), systemModel.getBoolean()));

        add(system, tb, new Operator("InValueSet", new Signature(systemModel.getString(), systemModel.getValueSet()), systemModel.getBoolean()));
        add(system, tb, new Operator("InValueSet", new Signature(systemModel.getCode(), systemModel.getValueSet()), systemModel.getBoolean()));
        add(system, tb, new Operator("InValueSet", new Signature(systemModel.getConcept(), systemModel.getValueSet()), systemModel.getBoolean()));

        add(system, tb, new Operator("AnyInValueSet", new Signature(new ListType(systemModel.getString())), systemModel.getBoolean()));
        add(system, tb, new Operator("AnyInValueSet", new Signature(new ListType(systemModel.getCode())), systemModel.getBoolean()));
        add(system, tb, new Operator("AnyInValueSet", new Signature(new ListType(systemModel.getConcept())), systemModel.getBoolean()));

        add(system, tb, new Operator("AnyInValueSet", new Signature(new ListType(systemModel.getString()), systemModel.getValueSet()), systemModel.getBoolean()));
        add(system, tb, new Operator("AnyInValueSet", new Signature(new ListType(systemModel.getCode()), systemModel.getValueSet()), systemModel.getBoolean()));
        add(system, tb, new Operator("AnyInValueSet", new Signature(new ListType(systemModel.getConcept()), systemModel.getValueSet()), systemModel.getBoolean()));

        add(system, tb, new Operator("InCodeSystem", new Signature(systemModel.getString()), systemModel.getBoolean()));
        add(system, tb, new Operator("InCodeSystem", new Signature(systemModel.getCode()), systemModel.getBoolean()));
        add(system, tb, new Operator("InCodeSystem", new Signature(systemModel.getConcept()), systemModel.getBoolean()));

        add(system, tb, new Operator("InCodeSystem", new Signature(systemModel.getString(), systemModel.getCodeSystem()), systemModel.getBoolean()));
        add(system, tb, new Operator("InCodeSystem", new Signature(systemModel.getCode(), systemModel.getCodeSystem()), systemModel.getBoolean()));
        add(system, tb, new Operator("InCodeSystem", new Signature(systemModel.getConcept(), systemModel.getCodeSystem()), systemModel.getBoolean()));

        add(system, tb, new Operator("AnyInCodeSystem", new Signature(new ListType(systemModel.getString())), systemModel.getBoolean()));
        add(system, tb, new Operator("AnyInCodeSystem", new Signature(new ListType(systemModel.getCode())), systemModel.getBoolean()));
        add(system, tb, new Operator("AnyInCodeSystem", new Signature(new ListType(systemModel.getConcept())), systemModel.getBoolean()));

        add(system, tb, new Operator("AnyInCodeSystem", new Signature(new ListType(systemModel.getString()), systemModel.getCodeSystem()), systemModel.getBoolean()));
        add(system, tb, new Operator("AnyInCodeSystem", new Signature(new ListType(systemModel.getCode()), systemModel.getCodeSystem()), systemModel.getBoolean()));
        add(system, tb, new Operator("AnyInCodeSystem", new Signature(new ListType(systemModel.getConcept()), systemModel.getCodeSystem()), systemModel.getBoolean()));

        add(system, tb, new Operator("Subsumes", new Signature(systemModel.getCode(), systemModel.getCode()), systemModel.getBoolean()));
        add(system, tb, new Operator("Subsumes", new Signature(systemModel.getConcept(), systemModel.getConcept()), systemModel.getBoolean()));

        add(system, tb, new Operator("SubsumedBy", new Signature(systemModel.getCode(), systemModel.getCode()), systemModel.getBoolean()));
        add(system, tb, new Operator("SubsumedBy", new Signature(systemModel.getConcept(), systemModel.getConcept()), systemModel.getBoolean()));

        // Errors
        // Message(source T, condition Boolean, code String, severity String, message String) T
        add(system, tb, new GenericOperator("Message", new Signature(new TypeParameter("T"), systemModel.getBoolean(),
                systemModel.getString(), systemModel.getString(), systemModel.getString()), new TypeParameter("T"),
                new TypeParameter("T")));

        return system;
    }

    private static void add(TranslatedLibrary systemLibrary, TypeBuilder tb, Operator operator) {
        // In the case that an operator is added directly, manufacture a FunctionDef so it can be referred to in ELM Analysis
        FunctionDef fd = new FunctionDef();
        fd.setName(operator.getName());
        int n = 0;
        for (DataType dataType : operator.getSignature().getOperandTypes()) {
            n++;
            OperandDef od = new OperandDef().withName(String.format("param%d", n));
            if (dataType instanceof NamedType) {
                od.setOperandType(tb.dataTypeToQName(dataType));
            }
            else {
                od.setOperandTypeSpecifier(tb.dataTypeToTypeSpecifier(dataType));
            }
            od.setResultType(dataType);
            fd.getOperand().add(od);
        }
        operator.setFunctionDef(fd);

        systemLibrary.add(fd, operator);
    }

    private static void add (TranslatedLibrary systemLibrary, TypeBuilder tb, Conversion conversion) {
        systemLibrary.add(conversion);
    }
}
