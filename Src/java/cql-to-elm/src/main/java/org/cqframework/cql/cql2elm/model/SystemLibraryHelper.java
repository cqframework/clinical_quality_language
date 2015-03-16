package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.IntervalType;
import org.cqframework.cql.elm.tracking.ListType;
import org.cqframework.cql.elm.tracking.TypeParameter;
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

        // Conversion Operators
        // ToString(Boolean) : String
        // ToString(Integer) : String
        // ToString(Decimal) : String
        // ToString(DateTime) : String
        // ToString(Time) : String
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
        Operator timeToString = new Operator("ToString", new Signature(systemModel.getTime()), systemModel.getString());
        system.add(timeToString);
        system.add(new Conversion(timeToString, false));

        // ToBoolean(String) : Boolean
        Operator stringToBoolean = new Operator("ToBoolean", new Signature(systemModel.getString()), systemModel.getBoolean());
        system.add(stringToBoolean);
        system.add(new Conversion(stringToBoolean, false));

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
        Operator stringToDateTime = new Operator("ToDateTime", new Signature(systemModel.getString()), systemModel.getDateTime());
        system.add(stringToDateTime);
        system.add(new Conversion(stringToDateTime, false));

        // ToTime(String) : Time
        Operator stringToTime = new Operator("ToTime", new Signature(systemModel.getString()), systemModel.getTime());
        system.add(stringToTime);
        system.add(new Conversion(stringToTime, false));

        // Comparison Operators
        // Equal<T>(T, T) : Boolean
        system.add(new GenericOperator("Equal", new Signature(new TypeParameter("T"), new TypeParameter("T")), systemModel.getBoolean(), new TypeParameter("T")));
        system.add(new Operator("Less", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        system.add(new Operator("LessOrEqual", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        system.add(new Operator("Greater", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        system.add(new Operator("GreaterOrEqual", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getBoolean()));
        system.add(new Operator("Less", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        system.add(new Operator("LessOrEqual", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        system.add(new Operator("Greater", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        system.add(new Operator("GreaterOrEqual", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getBoolean()));
        system.add(new Operator("Less", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("LessOrEqual", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("Greater", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("GreaterOrEqual", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("Less", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("LessOrEqual", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("Greater", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("GreaterOrEqual", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("Less", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        system.add(new Operator("LessOrEqual", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        system.add(new Operator("Greater", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));
        system.add(new Operator("GreaterOrEqual", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getBoolean()));

        // Arithmetic Operators
        system.add(new Operator("Abs", new Signature(systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Abs", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Abs", new Signature(systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Add", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Add", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Add", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Ceiling", new Signature(systemModel.getDecimal()), systemModel.getInteger()));

        system.add(new Operator("Divide", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getDecimal()));
        system.add(new Operator("Divide", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Divide", new Signature(systemModel.getQuantity(), systemModel.getDecimal()), systemModel.getQuantity()));
        system.add(new Operator("Divide", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Floor", new Signature(systemModel.getDecimal()), systemModel.getInteger()));

        system.add(new Operator("Log", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));

        system.add(new Operator("Ln", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));

        // MaxValue<T>() : T
        // MinValue<T>() : T

        system.add(new Operator("Modulo", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Modulo", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Modulo", new Signature(systemModel.getQuantity(), systemModel.getInteger()), systemModel.getQuantity()));
        system.add(new Operator("Modulo", new Signature(systemModel.getQuantity(), systemModel.getDecimal()), systemModel.getQuantity()));

        system.add(new Operator("Multiply", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Multiply", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Multiply", new Signature(systemModel.getQuantity(), systemModel.getDecimal()), systemModel.getQuantity()));
        system.add(new Operator("Multiply", new Signature(systemModel.getDecimal(), systemModel.getQuantity()), systemModel.getQuantity()));
        system.add(new Operator("Multiply", new Signature(systemModel.getQuantity(), systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Negate", new Signature(systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Negate", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("Negate", new Signature(systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Predecessor", new Signature(systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("Predecessor", new Signature(systemModel.getDecimal()), systemModel.getDecimal()));
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
        system.add(new Operator("Successor", new Signature(systemModel.getDateTime()), systemModel.getDateTime()));
        system.add(new Operator("Successor", new Signature(systemModel.getTime()), systemModel.getTime()));
        system.add(new Operator("Successor", new Signature(systemModel.getQuantity()), systemModel.getQuantity()));

        system.add(new Operator("Truncate", new Signature(systemModel.getDecimal()), systemModel.getInteger()));

        system.add(new Operator("TruncatedDivide", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getInteger()));
        system.add(new Operator("TruncatedDivide", new Signature(systemModel.getDecimal(), systemModel.getDecimal()), systemModel.getDecimal()));
        system.add(new Operator("TruncatedDivide", new Signature(systemModel.getQuantity(), systemModel.getInteger()), systemModel.getQuantity()));
        system.add(new Operator("TruncatedDivide", new Signature(systemModel.getQuantity(), systemModel.getDecimal()), systemModel.getQuantity()));

        // String operators
        system.add(new Operator("Add", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getString()));
        system.add(new Operator("Combine", new Signature(new ListType(systemModel.getString())), systemModel.getString()));
        system.add(new Operator("Combine", new Signature(new ListType(systemModel.getString()), systemModel.getString()), systemModel.getString()));
        system.add(new Operator("Concatenate", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getString()));
        system.add(new Operator("Indexer", new Signature(systemModel.getString(), systemModel.getInteger()), systemModel.getString()));
        system.add(new Operator("Length", new Signature(systemModel.getString()), systemModel.getInteger()));
        system.add(new Operator("Lower", new Signature(systemModel.getString()), systemModel.getString()));
        system.add(new Operator("Pos", new Signature(systemModel.getString(), systemModel.getString()), systemModel.getInteger()));
        system.add(new Operator("Split", new Signature(systemModel.getString(), systemModel.getString()), new ListType(systemModel.getString())));
        system.add(new Operator("Substring", new Signature(systemModel.getString(), systemModel.getInteger()), systemModel.getString()));
        system.add(new Operator("Substring", new Signature(systemModel.getString(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getString()));
        system.add(new Operator("Upper", new Signature(systemModel.getString()), systemModel.getString()));

        // Date/Time Operators
        system.add(new Operator("Add", new Signature(systemModel.getDateTime(), systemModel.getQuantity()), systemModel.getDateTime()));
        system.add(new Operator("Add", new Signature(systemModel.getTime(), systemModel.getQuantity()), systemModel.getTime()));
        system.add(new Operator("After", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("After", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("Before", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("Before", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getDateTime()));
        system.add(new Operator("DateTime", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getDecimal()), systemModel.getDateTime()));
        system.add(new Operator("DateFrom", new Signature(systemModel.getDateTime()), systemModel.getDateTime()));
        system.add(new Operator("TimeFrom", new Signature(systemModel.getDateTime()), systemModel.getTime()));
        system.add(new Operator("TimezoneFrom", new Signature(systemModel.getDateTime()), systemModel.getDecimal()));
        system.add(new Operator("TimezoneFrom", new Signature(systemModel.getTime()), systemModel.getDecimal()));
        system.add(new Operator("DateTimeComponentFrom", new Signature(systemModel.getDateTime()), systemModel.getInteger()));
        system.add(new Operator("DateTimeComponentFrom", new Signature(systemModel.getTime()), systemModel.getInteger()));
        system.add(new Operator("DifferenceBetween", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getInteger()));
        system.add(new Operator("DifferenceBetween", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getInteger()));
        system.add(new Operator("DurationBetween", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getInteger()));
        system.add(new Operator("DurationBetween", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getInteger()));
        system.add(new Operator("Now", new Signature(), systemModel.getDateTime()));
        system.add(new Operator("SameAs", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("SameAs", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("SameOrAfter", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("SameOrAfter", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("SameOrBefore", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getBoolean()));
        system.add(new Operator("SameOrBefore", new Signature(systemModel.getTime(), systemModel.getTime()), systemModel.getBoolean()));
        system.add(new Operator("Subtract", new Signature(systemModel.getDateTime(), systemModel.getQuantity()), systemModel.getDateTime()));
        system.add(new Operator("Subtract", new Signature(systemModel.getTime(), systemModel.getQuantity()), systemModel.getTime()));
        system.add(new Operator("Today", new Signature(), systemModel.getDateTime()));
        system.add(new Operator("Time", new Signature(systemModel.getInteger()), systemModel.getTime()));
        system.add(new Operator("Time", new Signature(systemModel.getInteger(), systemModel.getInteger()), systemModel.getTime()));
        system.add(new Operator("Time", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getTime()));
        system.add(new Operator("Time", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger()), systemModel.getTime()));
        system.add(new Operator("Time", new Signature(systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getInteger(), systemModel.getDecimal()), systemModel.getTime()));
        system.add(new Operator("TimeOfDay", new Signature(), systemModel.getTime()));

        // Interval Operators
        // After<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("After", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Before<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("Before", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Collapse<T>(list<interval<T>>) : list<interval<T>>
        system.add(new GenericOperator("Collapse", new Signature(new ListType(new IntervalType(new TypeParameter("T")))), new ListType(new IntervalType(new TypeParameter("T"))), new TypeParameter("T")));
        // Contains<T>(interval<T>, T) : Boolean
        system.add(new GenericOperator("Contains", new Signature(new IntervalType(new TypeParameter("T")), new TypeParameter("T")), systemModel.getBoolean(), new TypeParameter("T")));
        // End<T>(interval<T>) : T
        system.add(new GenericOperator("End", new Signature(new IntervalType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Ends<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("Ends", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Equal<T>(interval<T>, interval<T>) : Boolean
        // Already covered by Equal<T>(T, T)
        //system.add(new GenericOperator("Equal", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Except<T>(interval<T>, interval<T>) : interval<T>
        system.add(new GenericOperator("Except", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), new IntervalType(new TypeParameter("T")), new TypeParameter("T")));
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
        // ProperIncludes<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("ProperIncludes", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIncludedIn<T>(interval<T>, interval<T>) : Boolean
        system.add(new GenericOperator("ProperIncludedIn", new Signature(new IntervalType(new TypeParameter("T")), new IntervalType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
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
        // Already covered by Equal<T>(T, T)
        //system.add(new GenericOperator("Equal", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Except<T>(list<T>, list<T>) : list<T>
        system.add(new GenericOperator("Except", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), new ListType(new TypeParameter("T")), new TypeParameter("T")));
        // Exists<T>(list<T>) : Boolean
        system.add(new GenericOperator("Exists", new Signature(new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // Expand<T>(list<list<T>>) : list<T>
        system.add(new GenericOperator("Expand", new Signature(new ListType(new ListType(new TypeParameter("T")))), new ListType(new TypeParameter("T")), new TypeParameter("T")));
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
        // ProperIncludes<T>(list<T>, list<T>) : Boolean
        system.add(new GenericOperator("ProperIncludes", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // ProperIncludedIn<T>(list<T>, list<T>) : Boolean
        system.add(new GenericOperator("ProperIncludedIn", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), systemModel.getBoolean(), new TypeParameter("T")));
        // SingletonFrom<T>(list<T>) : T
        system.add(new GenericOperator("SingletonFrom", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Union<T>(list<T>, list<T>) : list<T>
        system.add(new GenericOperator("Union", new Signature(new ListType(new TypeParameter("T")), new ListType(new TypeParameter("T"))), new ListType(new TypeParameter("T")), new TypeParameter("T")));

        // Aggregate Operators
        system.add(new Operator("AllTrue", new Signature(new ListType(systemModel.getBoolean())), systemModel.getBoolean()));
        system.add(new Operator("AnyTrue", new Signature(new ListType(systemModel.getBoolean())), systemModel.getBoolean()));
        system.add(new Operator("Avg", new Signature(new ListType(systemModel.getInteger())), systemModel.getDecimal()));
        system.add(new Operator("Avg", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        // Count<T>(list<T>) : Integer
        system.add(new GenericOperator("Count", new Signature(new ListType(new TypeParameter("T"))), systemModel.getInteger(), new TypeParameter("T")));
        // Max<T>(list<T>) : T
        system.add(new GenericOperator("Max", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Min<T>(list<T>) : T
        system.add(new GenericOperator("Min", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Median<T>(list<T>) : T
        system.add(new GenericOperator("Median", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        // Mode<T>(list<T>) : T
        system.add(new GenericOperator("Mode", new Signature(new ListType(new TypeParameter("T"))), new TypeParameter("T"), new TypeParameter("T")));
        system.add(new Operator("PopulationStdDev", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("PopulationVariance", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("StdDev", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("Sum", new Signature(new ListType(systemModel.getInteger())), systemModel.getInteger()));
        system.add(new Operator("Sum", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));
        system.add(new Operator("Sum", new Signature(new ListType(systemModel.getQuantity())), systemModel.getQuantity()));
        system.add(new Operator("Variance", new Signature(new ListType(systemModel.getDecimal())), systemModel.getDecimal()));

        // Clinical
        system.add(new Operator("CalculateAge", new Signature(systemModel.getDateTime()), systemModel.getInteger()));
        system.add(new Operator("CalculateAgeAt", new Signature(systemModel.getDateTime(), systemModel.getDateTime()), systemModel.getInteger()));

        system.add(new Operator("InValueSet", new Signature(systemModel.getString()), systemModel.getBoolean()));
        system.add(new Operator("InValueSet", new Signature(systemModel.getCode()), systemModel.getBoolean()));
        system.add(new Operator("InValueSet", new Signature(systemModel.getConcept()), systemModel.getBoolean()));

        return system;
    }
}
