package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.*;
import org.hl7.elm.r1.VersionedIdentifier;

public class SystemLibraryHelper {
    public static TranslatedLibrary load(SystemModel systemModel) {
        TranslatedLibrary system = new TranslatedLibrary();
        system.setIdentifier(new VersionedIdentifier().withId("System").withVersion("1.0"));

        // Logical Operators
        system.add(new Operator("And", new Signature(systemModel.getBoolean(), systemModel.getBoolean()), systemModel.getBoolean()));
        system.add(new Operator("Or", new Signature(systemModel.getBoolean(), systemModel.getBoolean()), systemModel.getBoolean()));
        system.add(new Operator("Xor", new Signature(systemModel.getBoolean(), systemModel.getBoolean()), systemModel.getBoolean()));
        system.add(new Operator("Not", new Signature(systemModel.getBoolean()), systemModel.getBoolean()));

        // Nullological Operators
        system.add(new Operator("IsNull", new Signature(systemModel.getAny()), systemModel.getBoolean()));
        system.add(new Operator("IsTrue", new Signature(systemModel.getBoolean()), systemModel.getBoolean()));
        system.add(new Operator("IsFalse", new Signature(systemModel.getBoolean()), systemModel.getBoolean()));
        // Coalesce<T>(list<T>)
        // Coalesce<T>(T, T)
        // Coalesce<T>(T, T, T)
        // Coalesce<T>(T, T, T, T)
        // Coalesce<T>(T, T, T, T, T)
        system.add(new GenericOperator("Coalesce", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        system.add(new GenericOperator("Coalesce", new Signature(new TypeParameter("T"), new TypeParameter("T")), new TypeParameter("T"), new TypeParameter("T")));
        system.add(new GenericOperator("Coalesce", new Signature(new TypeParameter("T"), new TypeParameter("T"), new TypeParameter("T")), new TypeParameter("T"), new TypeParameter("T")));
        system.add(new GenericOperator("Coalesce", new Signature(new TypeParameter("T"), new TypeParameter("T"), new TypeParameter("T"), new TypeParameter("T")), new TypeParameter("T"), new TypeParameter("T")));
        system.add(new GenericOperator("Coalesce", new Signature(new TypeParameter("T"), new TypeParameter("T"), new TypeParameter("T"), new TypeParameter("T"), new TypeParameter("T")), new TypeParameter("T"), new TypeParameter("T")));

        // Conversion Operators
        // ToString(Boolean) : String
        // ToString(Integer) : String
        // ToString(Decimal) : String
        // ToString(DateTime) : String
        // ToString(Date) : String
        // ToString(Time) : String
        // ToString(Quantity) : String
        // ToString(Ratio) : String
        Operator booleanToString = new Operator("ToString", new Signature(systemModel.getBoolean()), systemModel.getString());
        system.add(booleanToString);
        system.add(new Conversion(booleanToString, false));
        Operator integerToString = new Operator("ToString", new Signature(systemModel.getInteger()), systemModel.getString());
        system.add(integerToString);
        system.add(new Conversion(integerToString, false));
        Operator decimalToString = new Operator("ToString", new Signature(systemModel.getDecimal()), systemModel.getString());
        system.add(decimalToString);
        system.add(new Conversion(decimalToString, false));
        Operator dateTimeToString = new Operator("ToString", new Signature(systemModel.getDateTime()), systemModel.getString());
        system.add(dateTimeToString);
        system.add(new Conversion(dateTimeToString, false));
        Operator dateToString = new Operator("ToString", new Signature(systemModel.getDate()), systemModel.getString());
        system.add(dateToString);
        system.add(new Conversion(dateToString, false));
        Operator timeToString = new Operator("ToString", new Signature(systemModel.getTime()), systemModel.getString());
        system.add(timeToString);
        system.add(new Conversion(timeToString, false));
        Operator quantityToString = new Operator("ToString", new Signature(systemModel.getQuantity()), systemModel.getString());
        system.add(quantityToString);
        system.add(new Conversion(quantityToString, false));
        Operator ratioToString = new Operator("ToString", new Signature(systemModel.getRatio()), systemModel.getString());
        system.add(ratioToString);
        system.add(new Conversion(ratioToString, false));

        // ToBoolean(String) : Boolean
        Operator stringToBoolean = new Operator("ToBoolean", new Signature(systemModel.getString()), systemModel.getBoolean());
        system.add(stringToBoolean);
        system.add(new Conversion(stringToBoolean, false));

        // ToChars(String) : List(String)
        Operator toChars = new Operator("ToChars", new Signature(systemModel.getString()), new ListType(systemModel.getString()));
        system.add(toChars);
        system.add(new Conversion(toChars, false));

        // ToInteger(String) : Integer
        Operator stringToInteger = new Operator("ToInteger", new Signature(systemModel.getString()), systemModel.getInteger());
        system.add(stringToInteger);
        system.add(new Conversion(stringToInteger, false));

        // ToDecimal(String) : Decimal
        // ToDecimal(Integer) : Decimal
        Operator stringToDecimal = new Operator("ToDecimal", new Signature(systemModel.getString()), systemModel.getDecimal());
        system.add(stringToDecimal);
        system.add(new Conversion(stringToDecimal, false));
        Operator integerToDecimal = new Operator("ToDecimal", new Signature(systemModel.getInteger()), systemModel.getDecimal());
        system.add(integerToDecimal);
        system.add(new Conversion(integerToDecimal, true));

        // ToDateTime(String) : DateTime
        // ToDateTime(Date) : DateTime
        Operator stringToDateTime = new Operator("ToDateTime", new Signature(systemModel.getString()), systemModel.getDateTime());
        system.add(stringToDateTime);
        system.add(new Conversion(stringToDateTime, false));
        Operator dateToDateTime = new Operator("ToDateTime", new Signature(systemModel.getDate()), systemModel.getDateTime());
        system.add(dateToDateTime);
        system.add(new Conversion(dateToDateTime, true));

        // ToDate(DateTime) : Date
        // ToDate(String) : Date
        Operator stringToDate = new Operator("ToDate", new Signature(systemModel.getString()), systemModel.getDate());
        system.add(stringToDate);
        system.add(new Conversion(stringToDate, false));
        Operator dateTimeToDate = new Operator("ToDate", new Signature(systemModel.getDateTime()), systemModel.getDate());
        system.add(dateTimeToDate);
        system.add(new Conversion(dateTimeToDate, false));

        // ToTime(String) : Time
        Operator stringToTime = new Operator("ToTime", new Signature(systemModel.getString()), systemModel.getTime());
        system.add(stringToTime);
        system.add(new Conversion(stringToTime, false));

        // ToQuantity(String) : Quantity
        // ToQuantity(Integer) : Quantity
        // ToQuantity(Ratio) : Quantity
        // ToQuantity(Decimal) : Quantity
        Operator stringToQuantity = new Operator("ToQuantity", new Signature(systemModel.getString()), systemModel.getQuantity());
        system.add(stringToQuantity);
        system.add(new Conversion(stringToQuantity, false));
        Operator ratioToQuantity = new Operator("ToQuantity", new Signature(systemModel.getRatio()), systemModel.getQuantity());
        system.add(ratioToQuantity);
        system.add(new Conversion(ratioToQuantity, false));
        Operator integerToQuantity = new Operator("ToQuantity", new Signature(systemModel.getInteger()), systemModel.getQuantity());
        system.add(integerToQuantity);
        system.add(new Conversion(integerToQuantity, true));
        Operator decimalToQuantity = new Operator("ToQuantity", new Signature(systemModel.getDecimal()), systemModel.getQuantity());
        system.add(decimalToQuantity);
        system.add(new Conversion(decimalToQuantity, true));

        // ToRatio(String) : Ratio
        Operator stringToRatio = new Operator("ToRatio", new Signature(systemModel.getString()), systemModel.getRatio());
        system.add(stringToRatio);
        system.add(new Conversion(stringToRatio, false));

        // ConvertsToBoolean(Any): Boolean
        Operator convertsTo = new Operator("ConvertsToBoolean", new Signature(systemModel.getAny()), systemModel.getBoolean());
        system.add(convertsTo);
        // ConvertsToInteger(Any): Boolean
        convertsTo = new Operator("ConvertsToInteger", new Signature(systemModel.getAny()), systemModel.getBoolean());
        system.add(convertsTo);
        // ConvertsToDecimal
        convertsTo = new Operator("ConvertsToDecimal", new Signature(systemModel.getAny()), systemModel.getBoolean());
        system.add(convertsTo);
        // ConvertsToDateTime
        convertsTo = new Operator("ConvertsToDateTime", new Signature(systemModel.getAny()), systemModel.getBoolean());
        system.add(convertsTo);
        // ConvertsToDate
        convertsTo = new Operator("ConvertsToDate", new Signature(systemModel.getAny()), systemModel.getBoolean());
        system.add(convertsTo);
        // ConvertsToTime
        convertsTo = new Operator("ConvertsToTime", new Signature(systemModel.getAny()), systemModel.getBoolean());
        system.add(convertsTo);
        // ConvertsToString
        convertsTo = new Operator("ConvertsToString", new Signature(systemModel.getAny()), systemModel.getBoolean());
        system.add(convertsTo);
        // ConvertsToQuantity
        convertsTo = new Operator("ConvertsToQuantity", new Signature(systemModel.getAny()), systemModel.getBoolean());
        system.add(convertsTo);
        // ConvertsToRatio
        convertsTo = new Operator("ConvertsToRatio", new Signature(systemModel.getAny()), systemModel.getBoolean());
        system.add(convertsTo);

        // CanConvertQuantity
        Operator canConvertToQuantity = new Operator("CanConvertQuantity", new Signature(systemModel.getQuantity(), systemModel.getString()), systemModel.getBoolean());
        system.add(canConvertToQuantity);

        // ConvertQuantity
        Operator convertToQuantity = new Operator("ConvertQuantity", new Signature(systemModel.getQuantity(), systemModel.getString()), systemModel.getQuantity());
        system.add(convertToQuantity);

        // Comparison Operators
        // Equal<T : value>(T, T) : Boolean
        //TypeParameter T = new TypeParameter("T", TypeParameter.TypeParameterConstraint.VALUE, null);
        //system.add(new GenericOperator("Equal", new Signature(T, T), systemModel.getBoolean(), T));
        // Equal<C : class>(C, C) : Boolean
        TypeParameter C = new TypeParameter("C", TypeParameter.TypeParameterConstraint.CLASS, null);
        system.add(new GenericOperator("Equal", new Signature(C, C), systemModel.getBoolean(), C));
        // Equal<R : tuple>(R, R) : Boolean
        TypeParameter R = new TypeParameter("R", TypeParameter.TypeParameterConstraint.TUPLE, null);
        system.add(new GenericOperator("Equal", new Signature(R, R), systemModel.getBoolean(), R));
        // Equal<H : choice>(H, H) : Boolean
        TypeParameter H = new TypeParameter("H", TypeParameter.TypeParameterConstraint.CHOICE, null);
        system.add(new GenericOperator("Equal", new Signature(H, H), systemModel.getBoolean(), H));
        // Equal(Any, Any) : Boolean
        //system.add(new Operator("Equal", new Signature(systemModel.getAny(), systemModel.getAny()), systemModel.getBoolean()));
        // Equivalent<T : value>(T, T) : Boolean
        //T = new TypeParameter("T", TypeParameter.TypeParameterConstraint.VALUE, null);
        //system.add(new GenericOperator("Equivalent", new Signature(T, T), systemModel.getBoolean(), T));
        // Equivalent<C : class>(C, C) : Boolean
        C = new TypeParameter("C", TypeParameter.TypeParameterConstraint.CLASS, null);
        system.add(new GenericOperator("Equivalent", new Signature(C, C), systemModel.getBoolean(), C));
        // Equivalent<R : tuple>(R, R) : Boolean
        R = new TypeParameter("R", TypeParameter.TypeParameterConstraint.TUPLE, null);
        system.add(new GenericOperator("Equivalent", new Signature(R, R), systemModel.getBoolean(), R));
        // Equivalent<H : choice>(H, H) : Boolean
        H = new TypeParameter("H", TypeParameter.TypeParameterConstraint.CHOICE, null);
        system.add(new GenericOperator("Equivalent", new Signature(H, H), systemModel.getBoolean(), H));
        // Equivalent(Any, Any) : Boolean
        //system.add(new Operator("Equivalent", new Signature(systemModel.getAny(), systemModel.getAny()), systemModel.getBoolean()));

        system.add(new Operator("Equal", new Signature(systemModel.getBoolean(), systemModel.getBoolean()), systemModel.getBoolean()));
        system.add(new Operator("Equivalent", new Signature(systemModel.getBoolean(), systemModel.getBoolean()), systemModel.getBoolean()));
        system.add(new Operator("Equal", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        system.add(new Operator("Equivalent", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        system.add(new Operator("Less", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        system.add(new Operator("LessOrEqual", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        system.add(new Operator("Greater", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        system.add(new Operator("GreaterOrEqual", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        system.add(new Operator("Equal", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        system.add(new Operator("Equivalent", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        system.add(new Operator("Less", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        system.add(new Operator("LessOrEqual", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        system.add(new Operator("Greater", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        system.add(new Operator("GreaterOrEqual", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        system.add(new Operator("Equal", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("Equivalent", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("Less", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("LessOrEqual", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("Greater", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("GreaterOrEqual", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("Equal", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("Equivalent", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("Less", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("LessOrEqual", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("Greater", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("GreaterOrEqual", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("Equal", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        system.add(new Operator("Equivalent", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        system.add(new Operator("Less", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        system.add(new Operator("LessOrEqual", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        system.add(new Operator("Greater", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        system.add(new Operator("GreaterOrEqual", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        system.add(new Operator("Equal", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("Equivalent", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("Less", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("LessOrEqual", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("Greater", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("GreaterOrEqual", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("Equal", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        system.add(new Operator("Equivalent", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        system.add(new Operator("Less", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        system.add(new Operator("LessOrEqual", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        system.add(new Operator("Greater", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        system.add(new Operator("GreaterOrEqual", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        system.add(new Operator("Equal", new Signature(systemModel.getRatio(), systemModel.getRatio()), systemModel.getBoolean()));
        system.add(new Operator("Equivalent", new Signature(systemModel.getRatio(), systemModel.getRatio()), systemModel.getBoolean()));

        // Arithmetic Operators
        system.add(new Operator("Abs", new Signature(systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Abs", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Abs", new Signature(systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Add", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Add", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Add", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Ceiling", new Signature(systemModel.getDecimal()), systemModel.getInteger()));

        system.add(new Operator("Divide", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        //system.add(new Operator("Divide", new Signature(systemModel.getQuantity(), systemModel.getDecimal()), systemModel.getQuantity()));
        system.add(new Operator("Divide", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Floor", new Signature(systemModel.getDecimal()), systemModel.getInteger()));

        system.add(new Operator("Log", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));

        system.add(new Operator("Ln", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));

        system.add(new Operator("Exp", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));

        // MaxValue<T>() : T
        // MinValue<T>() : T

        system.add(new Operator("Modulo", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Modulo", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        // BTR -> Removed these, we should make sure we have a clear use case for this operator before adding these signatures
        //system.add(new Operator("Modulo", new Signature(systemModel.getQuantity(), systemModel.getInteger()), systemModel.getQuantity()));
        //system.add(new Operator("Modulo", new Signature(systemModel.getQuantity(), systemModel.getDecimal()), systemModel.getQuantity()));

        system.add(new Operator("Multiply", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Multiply", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        //system.add(new Operator("Multiply", new Signature(systemModel.getQuantity(), systemModel.getDecimal()), systemModel.getQuantity()));
        //system.add(new Operator("Multiply", new Signature(systemModel.getDecimal(), systemModel.getQuantity()), systemModel.getQuantity()));
        system.add(new Operator("Multiply", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Negate", new Signature(systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Negate", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Negate", new Signature(systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Predecessor", new Signature(systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Predecessor", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Predecessor", new Signature(systemModel.getDate()), systemModel.getDate()));
        system.add(new Operator("Predecessor", new Signature(systemModel.getDateTime()), systemModel.getDateTime()));
        system.add(new Operator("Predecessor", new Signature(systemModel.getTime()), systemModel.getTime()));
        system.add(new Operator("Predecessor", new Signature(systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Power", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Power", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));

        system.add(new Operator("Round", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Round", new Signature(systemModel.getDecimal(), systemModel.getInteger()), systemModel.getDecimal()));

        system.add(new Operator("Subtract", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Subtract", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Subtract", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Successor", new Signature(systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Successor", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Successor", new Signature(systemModel.getDate()), systemModel.getDate()));
        system.add(new Operator("Successor", new Signature(systemModel.getDateTime()), systemModel.getDateTime()));
        system.add(new Operator("Successor", new Signature(systemModel.getTime()), systemModel.getTime()));
        system.add(new Operator("Successor", new Signature(systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Truncate", new Signature(systemModel.getDecimal()), systemModel.getInteger()));

        system.add(new Operator("TruncatedDivide", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("TruncatedDivide", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        // BTR -> Removed these, we should make sure we have a clear use case for this operator before adding these signatures
        // system.add(new Operator("TruncatedDivide", new Signature(systemModel.getQuantity(), systemModel.getInteger()), systemModel.getQuantity()));
        //system.add(new Operator("TruncatedDivide", new Signature(systemModel.getQuantity(), systemModel.getDecimal()), systemModel.getQuantity()));

        // String operators
        system.add(new Operator("Add", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getString()));
        system.add(new Operator("Combine", new Signature(new ListType(systemModel.getString())), systemModel.getString()));
        system.add(new Operator("Combine", new Signature(new ListType(systemModel.getString()), systemModel.getString()), systemModel.getString()));
        system.add(new Operator("Concatenate", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getString()));
        system.add(new Operator("EndsWith", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("Indexer", new Signature(systemModel.getString(), systemModel.getInteger()), systemModel.getString()));
        system.add(new Operator("LastPositionOf", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getInteger()));
        system.add(new Operator("Length", new Signature(systemModel.getString()), systemModel.getInteger()));
        system.add(new Operator("Lower", new Signature(systemModel.getString()), systemModel.getString()));
        system.add(new Operator("Matches", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("PositionOf", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getInteger()));
        system.add(new Operator("ReplaceMatches", new Signature(systemModel.getString(), systemModel.getString(), systemModel.getString()), systemModel.getString()));
        system.add(new Operator("Split", new Signature(systemModel.getString(), systemModel.getString()), new ListType(systemModel.getString())));
        system.add(new Operator("SplitOnMatches", new Signature(systemModel.getString(), systemModel.getString()), new ListType(systemModel.getString())));
        system.add(new Operator("StartsWith", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("Substring", new Signature(systemModel.getString(), systemModel.getInteger()), systemModel.getString()));
        system.add(new Operator("Substring", new Signature(systemModel.getString(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getString()));
        system.add(new Operator("Upper", new Signature(systemModel.getString()), systemModel.getString()));

        // Date/Time Operators
        system.add(new Operator("Add", new Signature(systemModel.getDateTime(), systemModel.getQuantity()), systemModel.getDateTime()));
        system.add(new Operator("Add", new Signature(systemModel.getDate(), systemModel.getQuantity()), systemModel.getDate()));
        system.add(new Operator("Add", new Signature(systemModel.getTime(), systemModel.getQuantity()), systemModel.getTime()));
        system.add(new Operator("After", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("After", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        system.add(new Operator("After", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("Before", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("Before", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        system.add(new Operator("Before", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getDecimal()), systemModel.getDateTime()));
        system.add(new Operator("Date", new Signature(systemModel.getInteger()), systemModel.getDate()));
        system.add(new Operator("Date", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getDate()));
        system.add(new Operator("Date", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDate()));
        system.add(new Operator("DateFrom", new Signature(systemModel.getDateTime()), systemModel.getDate()));
        system.add(new Operator("TimeFrom", new Signature(systemModel.getDateTime()), systemModel.getTime()));
        system.add(new Operator("TimezoneOffsetFrom", new Signature(systemModel.getDateTime()), systemModel.getDecimal()));
        system.add(new Operator("DateTimeComponentFrom", new Signature(systemModel.getDateTime()), systemModel.getInteger()));
        system.add(new Operator("DateTimeComponentFrom", new Signature(systemModel.getDate()), systemModel.getInteger()));
        system.add(new Operator("DateTimeComponentFrom", new Signature(systemModel.getTime()), systemModel.getInteger()));
        system.add(new Operator("DifferenceBetween", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getInteger()));
        system.add(new Operator("DifferenceBetween", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getInteger()));
        system.add(new Operator("DifferenceBetween", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getInteger()));
        system.add(new Operator("DurationBetween", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getInteger()));
        system.add(new Operator("DurationBetween", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getInteger()));
        system.add(new Operator("DurationBetween", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getInteger()));
        system.add(new Operator("Now", new Signature(), systemModel.getDateTime()));
        system.add(new Operator("SameAs", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("SameAs", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        system.add(new Operator("SameAs", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("SameOrAfter", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("SameOrAfter", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        system.add(new Operator("SameOrAfter", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("SameOrBefore", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("SameOrBefore", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getBoolean()));
        system.add(new Operator("SameOrBefore", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("Subtract", new Signature(systemModel.getDateTime(), systemModel.getQuantity()), systemModel.getDateTime()));
        system.add(new Operator("Subtract", new Signature(systemModel.getDate(), systemModel.getQuantity()), systemModel.getDate()));
        system.add(new Operator("Subtract", new Signature(systemModel.getTime(), systemModel.getQuantity()), systemModel.getTime()));
        system.add(new Operator("Today", new Signature(), systemModel.getDate()));
        system.add(new Operator("Time", new Signature(systemModel.getInteger()), systemModel.getTime()));
        system.add(new Operator("Time", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getTime()));
        system.add(new Operator("Time", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getTime()));
        system.add(new Operator("Time", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getTime()));
        system.add(new Operator("TimeOfDay", new Signature(), systemModel.getTime()));

        // Interval Operators
        // After<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("After", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Before<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("Before", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Collapse<T>(list<interval<T>>) : list<interval<T>>
        // Collapse<T>(list<interval<T>>, Quantity) : list<interval<T>>
        system.add(new GenericOperator("Collapse", new Signature(new ListType(new IntervalType(new TypeParameter("T"))), systemModel.getQuantity()), new ListType(new IntervalType(new TypeParameter("T"))), new TypeParameter("T")));
        // Contains<T>(interval<T>, T) : Boolean
        system.add(new GenericOperator("Contains", new Signature(new IntervalType(new TypeParameter("T")), new TypeParameter("T")), systemModel.getBoolean(), new TypeParameter("T")));
        // End<T>(interval<T>) : T
        system.add(new GenericOperator("End", new Signature(new IntervalType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Ends<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("Ends", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Equal<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("Equal", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Equivalent<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("Equivalent", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Except<T>(interval<T>, interval<T>) : interval<T>
        system.add(new GenericOperator("Except", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), new IntervalType(new TypeParameter("T")), new TypeParameter("T")));
        // Expand<T>(list<interval<T>>) : list<interval<T>>
        // Expand<T>(list<interval<T>>, Quantity) : list<interval<T>>
        system.add(new GenericOperator("Expand", new Signature(new ListType(new IntervalType(new TypeParameter("T"))), systemModel.getQuantity()), new ListType(new IntervalType(new TypeParameter("T"))), new TypeParameter("T")));
        // In<T>(T, interval<T>) : Boolean
        system.add(new GenericOperator("In", new Signature(new TypeParameter("T"), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Includes<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("Includes", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // IncludedIn<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("IncludedIn", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Intersect<T>(interval<T>, interval<T>) : interval<T>
        system.add(new GenericOperator("Intersect", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), new IntervalType(new TypeParameter("T")), new TypeParameter("T")));
        // Meets<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("Meets", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // MeetsBefore<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("MeetsBefore", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // MeetsAfter<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("MeetsAfter", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Overlaps<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("Overlaps", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // OverlapsBefore<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("OverlapsBefore", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // OverlapsAfter<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("OverlapsAfter", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // PointFrom<T>(interval<T>) : T
        GenericOperator pointFrom = new GenericOperator("PointFrom", new Signature(new IntervalType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T"));
        system.add(pointFrom);
        // ProperContains<T>(interval<T>, T) : Boolean
        system.add(new GenericOperator("ProperContains", new Signature(new IntervalType(new TypeParameter("T")), new TypeParameter("T")), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIn<T>(T, interval<T>) : Boolean
        system.add(new GenericOperator("ProperIn", new Signature(new TypeParameter("T"), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIncludes<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("ProperIncludes", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIncludedIn<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("ProperIncludedIn", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // SameAs<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("SameAs", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // SameOrAfter<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("SameOrAfter", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // SameOrBefore<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("SameOrBefore", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Size<T>(interval<T>) : T
        system.add(new GenericOperator("Size", new Signature(new IntervalType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Start<T>(interval<T>) : T
        system.add(new GenericOperator("Start", new Signature(new IntervalType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Starts<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("Starts", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Union<T>(interval<T>, interval<T>) : interval<T>
        system.add(new GenericOperator("Union", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), new IntervalType(new TypeParameter("T")), new TypeParameter("T")));
        // Width<T>(interval<T>) : T
        system.add(new GenericOperator("Width", new Signature(new IntervalType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));

        // List Operators
        // Contains<T>(list<T>, T) : Boolean
        system.add(new GenericOperator("Contains", new Signature(new ListType(new TypeParameter("T")), new TypeParameter("T")), systemModel.getBoolean(), new TypeParameter("T")));
        // Distinct<T>(list<T>) : list<T>
        system.add(new GenericOperator("Distinct", new Signature(new ListType(new TypeParameter("T"))), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Equal<T>(list<T>, list<T>) : Boolean
        system.add(new GenericOperator("Equal", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Equivalent<T>(list<T>, list<T>) : Boolean
        system.add(new GenericOperator("Equivalent", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Except<T>(list<T>, list<T>) : list<T>
        system.add(new GenericOperator("Except", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Exists<T>(list<T>) : Boolean
        system.add(new GenericOperator("Exists", new Signature(new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Flatten<T>(list<list<T>>) : list<T>
        system.add(new GenericOperator("Flatten", new Signature(new ListType(new ListType(new TypeParameter("T")))), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // First<T>(list<T>) : T
        system.add(new GenericOperator("First", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // In<T>(T, list<T>) : Boolean
        system.add(new GenericOperator("In", new Signature(new TypeParameter("T"), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Includes<T>(list<T>, list<T>) : Boolean
        system.add(new GenericOperator("Includes", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // IncludedIn<T>(list<T>, list<T>) : Boolean
        system.add(new GenericOperator("IncludedIn", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Indexer<T>(list<T>, integer) : T
        system.add(new GenericOperator("Indexer", new Signature(new ListType(new TypeParameter("T")), systemModel.getInteger()), new TypeParameter("T"), new TypeParameter("T")));
        // IndexOf<T>(list<T>, T) : Integer
        system.add(new GenericOperator("IndexOf", new Signature(new ListType(new TypeParameter("T")), new TypeParameter("T")), systemModel.getInteger(), new TypeParameter("T")));
        // Intersect<T>(list<T>, list<T>) : list<T>
        system.add(new GenericOperator("Intersect", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Last<T>(list<T>) : T
        system.add(new GenericOperator("Last", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Length<T>(list<T>) : Integer
        system.add(new GenericOperator("Length", new Signature(new ListType(new TypeParameter("T"))), systemModel.getInteger(), new TypeParameter("T")));
        // ProperContains<T>(list<T>, T) : Boolean
        system.add(new GenericOperator("ProperContains", new Signature(new ListType(new TypeParameter("T")), new TypeParameter("T")), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIn<T>(T, list<T>) : Boolean
        system.add(new GenericOperator("ProperIn", new Signature(new TypeParameter("T"), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIncludes<T>(list<T>, list<T>) : Boolean
        system.add(new GenericOperator("ProperIncludes", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIncludedIn<T>(list<T>, list<T>) : Boolean
        system.add(new GenericOperator("ProperIncludedIn", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // SingletonFrom<T>(list<T>) : T
        GenericOperator singletonFrom = new GenericOperator("SingletonFrom", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T"));
        system.add(singletonFrom);
        //// NOTE: FHIRPath Implicit List Demotion
        // Generic conversions turned out to be computationally expensive, so we added explicit list promotion/demotion in the conversion map directly instead.
        //system.add(new Conversion(singletonFrom, true));
        // Skip(list<T>, Integer): list<T>
        system.add(new GenericOperator("Skip", new Signature(new ListType(new TypeParameter("T")), systemModel.getInteger()), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Tail(list<T>): list<T>
        system.add(new GenericOperator("Tail", new Signature(new ListType(new TypeParameter("T"))), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Take(list<T>, Integer): list<T>
        system.add(new GenericOperator("Take", new Signature(new ListType(new TypeParameter("T")), systemModel.getInteger()), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Union<T>(list<T>, list<T>) : list<T>
        system.add(new GenericOperator("Union", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), new ListType(new TypeParameter("T")), new TypeParameter("T")));

        // NOTE: FHIRPath Implicit List Promotion operator
        //GenericOperator toList = new GenericOperator("List", new Signature(new TypeParameter("T")), new ListType(new TypeParameter("T")), new TypeParameter("T"));
        //system.add(toList);
        //system.add(new Conversion(toList, true));

        // Aggregate Operators
        system.add(new Operator("AllTrue", new Signature(new ListType(systemModel.getBoolean())), systemModel.getBoolean()));
        system.add(new Operator("AnyTrue", new Signature(new ListType(systemModel.getBoolean())), systemModel.getBoolean()));
        system.add(new Operator("Avg", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("Avg", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        // Count<T>(list<T>) : Integer
        system.add(new GenericOperator("Count", new Signature(new ListType(new TypeParameter("T"))), systemModel.getInteger(), new TypeParameter("T")));
        //// Count(list<Any>) : Integer
        //system.add(new Operator("Count", new Signature(new ListType(systemModel.getAny())), systemModel.getInteger()));
        system.add(new Operator("GeometricMean", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("Max", new Signature(new ListType(systemModel.getInteger())), systemModel.getInteger()));
        system.add(new Operator("Max", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("Max", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        system.add(new Operator("Max", new Signature(new ListType(systemModel.getDateTime())), systemModel.getDateTime()));
        system.add(new Operator("Max", new Signature(new ListType(systemModel.getDate())), systemModel.getDate()));
        system.add(new Operator("Max", new Signature(new ListType(systemModel.getTime())), systemModel.getTime()));
        system.add(new Operator("Max", new Signature(new ListType(systemModel.getString())), systemModel.getString()));
        system.add(new Operator("Min", new Signature(new ListType(systemModel.getInteger())), systemModel.getInteger()));
        system.add(new Operator("Min", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("Min", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        system.add(new Operator("Min", new Signature(new ListType(systemModel.getDateTime())), systemModel.getDateTime()));
        system.add(new Operator("Min", new Signature(new ListType(systemModel.getDate())), systemModel.getDate()));
        system.add(new Operator("Min", new Signature(new ListType(systemModel.getTime())), systemModel.getTime()));
        system.add(new Operator("Min", new Signature(new ListType(systemModel.getString())), systemModel.getString()));
        system.add(new Operator("Median", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("Median", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        // Mode<T>(list<T>) : T
        system.add(new GenericOperator("Mode", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        system.add(new Operator("PopulationStdDev", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("PopulationStdDev", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        system.add(new Operator("PopulationVariance", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("PopulationVariance", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        system.add(new Operator("Product", new Signature(new ListType(systemModel.getInteger())), systemModel.getInteger()));
        system.add(new Operator("Product", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("Product", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        system.add(new Operator("StdDev", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("StdDev", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        system.add(new Operator("Sum", new Signature(new ListType(systemModel.getInteger())), systemModel.getInteger()));
        system.add(new Operator("Sum", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("Sum", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        system.add(new Operator("Variance", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("Variance", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));

        // Clinical
        // ToConcept(Code)
        Operator codeToConcept = new Operator("ToConcept", new Signature(systemModel.getCode()), systemModel.getConcept());
        system.add(codeToConcept);
        system.add(new Conversion(codeToConcept, true));
        // ToConcept(list<Code>)
        Operator codesToConcept = new Operator("ToConcept", new Signature(new ListType(systemModel.getCode())), systemModel.getConcept());
        system.add(codesToConcept);
        system.add(new Conversion(codesToConcept, false));

        system.add(new Operator("CalculateAge", new Signature(systemModel.getDateTime()), systemModel.getInteger()));
        system.add(new Operator("CalculateAge", new Signature(systemModel.getDate()), systemModel.getInteger()));
        system.add(new Operator("CalculateAgeAt", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getInteger()));
        system.add(new Operator("CalculateAgeAt", new Signature(systemModel.getDate(), systemModel.getDate()), systemModel.getInteger()));

        system.add(new Operator("InValueSet", new Signature(systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("InValueSet", new Signature(systemModel.getCode()), systemModel.getBoolean()));
        system.add(new Operator("InValueSet", new Signature(systemModel.getConcept()), systemModel.getBoolean()));

        system.add(new Operator("AnyInValueSet", new Signature(new ListType(systemModel.getString())), systemModel.getBoolean()));
        system.add(new Operator("AnyInValueSet", new Signature(new ListType(systemModel.getCode())), systemModel.getBoolean()));
        system.add(new Operator("AnyInValueSet", new Signature(new ListType(systemModel.getConcept())), systemModel.getBoolean()));

        system.add(new Operator("InCodeSystem", new Signature(systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("InCodeSystem", new Signature(systemModel.getCode()), systemModel.getBoolean()));
        system.add(new Operator("InCodeSystem", new Signature(systemModel.getConcept()), systemModel.getBoolean()));

        system.add(new Operator("AnyInCodeSystem", new Signature(new ListType(systemModel.getString())), systemModel.getBoolean()));
        system.add(new Operator("AnyInCodeSystem", new Signature(new ListType(systemModel.getCode())), systemModel.getBoolean()));
        system.add(new Operator("AnyInCodeSystem", new Signature(new ListType(systemModel.getConcept())), systemModel.getBoolean()));

        system.add(new Operator("Subsumes", new Signature(systemModel.getCode(), systemModel.getCode()), systemModel.getBoolean()));
        system.add(new Operator("Subsumes", new Signature(systemModel.getConcept(), systemModel.getConcept()), systemModel.getBoolean()));

        system.add(new Operator("SubsumedBy", new Signature(systemModel.getCode(), systemModel.getCode()), systemModel.getBoolean()));
        system.add(new Operator("SubsumedBy", new Signature(systemModel.getConcept(), systemModel.getConcept()), systemModel.getBoolean()));

        // Errors
        // Message(source T, condition Boolean, code String, severity String, message String) T
        system.add(new GenericOperator("Message", new Signature(new TypeParameter("T"), systemModel.getBoolean(),
                systemModel.getString(), systemModel.getString(), systemModel.getString()), new TypeParameter("T"),
                new TypeParameter("T")));

        return system;
    }
}
