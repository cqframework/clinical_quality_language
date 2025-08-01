package org.cqframework.cql.cql2elm.model

import kotlin.jvm.JvmStatic
import org.cqframework.cql.cql2elm.TypeBuilder
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.NamedType
import org.hl7.cql.model.TypeParameter
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.OperandDef
import org.hl7.elm.r1.VersionedIdentifier

@Suppress("LargeClass", "LongMethod")
object SystemLibraryHelper {
    @JvmStatic
    fun load(systemModel: SystemModel, tb: TypeBuilder): CompiledLibrary {
        val system = CompiledLibrary()
        system.identifier = VersionedIdentifier().withId("System").withVersion("1.0")

        // Logical Operators
        add(
            system,
            tb,
            Operator(
                "And",
                Signature(systemModel.boolean, systemModel.boolean),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Or", Signature(systemModel.boolean, systemModel.boolean), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "Xor",
                Signature(systemModel.boolean, systemModel.boolean),
                systemModel.boolean
            )
        )
        add(system, tb, Operator("Not", Signature(systemModel.boolean), systemModel.boolean))
        add(
            system,
            tb,
            Operator(
                "Implies",
                Signature(systemModel.boolean, systemModel.boolean),
                systemModel.boolean
            )
        )

        // Nullological Operators
        add(system, tb, Operator("IsNull", Signature(systemModel.any), systemModel.boolean))
        add(system, tb, Operator("IsTrue", Signature(systemModel.boolean), systemModel.boolean))
        add(system, tb, Operator("IsFalse", Signature(systemModel.boolean), systemModel.boolean))
        // Coalesce<T>(list<T>)
        // Coalesce<T>(T, T)
        // Coalesce<T>(T, T, T)
        // Coalesce<T>(T, T, T, T)
        // Coalesce<T>(T, T, T, T, T)
        add(
            system,
            tb,
            GenericOperator(
                "Coalesce",
                Signature(ListType(TypeParameter("T"))),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )
        add(
            system,
            tb,
            GenericOperator(
                "Coalesce",
                Signature(TypeParameter("T"), TypeParameter("T")),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )
        add(
            system,
            tb,
            GenericOperator(
                "Coalesce",
                Signature(TypeParameter("T"), TypeParameter("T"), TypeParameter("T")),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )
        add(
            system,
            tb,
            GenericOperator(
                "Coalesce",
                Signature(
                    TypeParameter("T"),
                    TypeParameter("T"),
                    TypeParameter("T"),
                    TypeParameter("T")
                ),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )
        add(
            system,
            tb,
            GenericOperator(
                "Coalesce",
                Signature(
                    TypeParameter("T"),
                    TypeParameter("T"),
                    TypeParameter("T"),
                    TypeParameter("T"),
                    TypeParameter("T")
                ),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )

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
        val booleanToString =
            Operator("ToString", Signature(systemModel.boolean), systemModel.string)
        add(system, tb, booleanToString)
        add(system, tb, Conversion(booleanToString, false))
        val integerToString =
            Operator("ToString", Signature(systemModel.integer), systemModel.string)
        add(system, tb, integerToString)
        add(system, tb, Conversion(integerToString, false))
        val longToString = Operator("ToString", Signature(systemModel.long), systemModel.string)
        add(system, tb, longToString)
        add(system, tb, Conversion(longToString, false))
        val decimalToString =
            Operator("ToString", Signature(systemModel.decimal), systemModel.string)
        add(system, tb, decimalToString)
        add(system, tb, Conversion(decimalToString, false))
        val dateTimeToString =
            Operator("ToString", Signature(systemModel.dateTime), systemModel.string)
        add(system, tb, dateTimeToString)
        add(system, tb, Conversion(dateTimeToString, false))
        val dateToString = Operator("ToString", Signature(systemModel.date), systemModel.string)
        add(system, tb, dateToString)
        add(system, tb, Conversion(dateToString, false))
        val timeToString = Operator("ToString", Signature(systemModel.time), systemModel.string)
        add(system, tb, timeToString)
        add(system, tb, Conversion(timeToString, false))
        val quantityToString =
            Operator("ToString", Signature(systemModel.quantity), systemModel.string)
        add(system, tb, quantityToString)
        add(system, tb, Conversion(quantityToString, false))
        val ratioToString = Operator("ToString", Signature(systemModel.ratio), systemModel.string)
        add(system, tb, ratioToString)
        add(system, tb, Conversion(ratioToString, false))

        // Operator stringToString = new Operator("ToString", new
        // Signature(systemModel.getString()),
        // systemModel.getString());
        // add(system, tb, stringToString);
        // add(system, tb, new Conversion(stringToString, false));

        // ToBoolean(Boolean) : Boolean
        // ToBoolean(Integer) : Boolean
        // ToBoolean(Decimal) : Boolean
        // ToBoolean(Long) : Boolean
        // ToBoolean(String) : Boolean
        val stringToBoolean =
            Operator("ToBoolean", Signature(systemModel.string), systemModel.boolean)
        add(system, tb, stringToBoolean)
        add(system, tb, Conversion(stringToBoolean, false))
        val integerToBoolean =
            Operator("ToBoolean", Signature(systemModel.integer), systemModel.boolean)
        add(system, tb, integerToBoolean)
        add(system, tb, Conversion(integerToBoolean, false))
        val decimalToBoolean =
            Operator("ToBoolean", Signature(systemModel.decimal), systemModel.boolean)
        add(system, tb, decimalToBoolean)
        add(system, tb, Conversion(decimalToBoolean, false))
        val longToBoolean = Operator("ToBoolean", Signature(systemModel.long), systemModel.boolean)
        add(system, tb, longToBoolean)
        add(system, tb, Conversion(longToBoolean, false))

        // Operator booleanToBoolean = new Operator("ToBoolean", new
        // Signature(systemModel.getBoolean()),
        // systemModel.getBoolean());
        // add(system, tb, booleanToBoolean);
        // add(system, tb, new Conversion(booleanToBoolean, false));

        // ToChars(String) : List(String)
        val toChars =
            Operator("ToChars", Signature(systemModel.string), ListType(systemModel.string))
        add(system, tb, toChars)
        add(system, tb, Conversion(toChars, false))

        // ToInteger(String) : Integer
        // ToInteger(Boolean) : Integer
        // ToInteger(Long) : Integer
        // ToInteger(Integer) : Integer
        val stringToInteger =
            Operator("ToInteger", Signature(systemModel.string), systemModel.integer)
        add(system, tb, stringToInteger)
        add(system, tb, Conversion(stringToInteger, false))
        val longToInteger = Operator("ToInteger", Signature(systemModel.long), systemModel.integer)
        add(system, tb, longToInteger)
        add(system, tb, Conversion(longToInteger, false))
        val booleanToInteger =
            Operator("ToInteger", Signature(systemModel.boolean), systemModel.integer)
        add(system, tb, booleanToInteger)
        add(system, tb, Conversion(booleanToInteger, false))

        // Operator integerToInteger = new Operator("ToInteger", new
        // Signature(systemModel.getInteger()),
        // systemModel.getInteger());
        // add(system, tb, integerToInteger);
        // add(system, tb, new Conversion(integerToInteger, false));

        // ToLong(Boolean) : Long
        // ToLong(String) : Long
        // ToLong(Integer) : Long
        // ToLong(Long) : Long
        val stringToLong = Operator("ToLong", Signature(systemModel.string), systemModel.long)
        add(system, tb, stringToLong)
        add(system, tb, Conversion(stringToLong, false))
        val integerToLong = Operator("ToLong", Signature(systemModel.integer), systemModel.long)
        add(system, tb, integerToLong)
        add(system, tb, Conversion(integerToLong, true))
        // Operator longToLong = new Operator("ToLong", new Signature(systemModel.getLong()),
        // systemModel.getLong());
        // add(system, tb, longToLong);
        // add(system, tb, new Conversion(longToLong, false));
        val booleanToLong = Operator("ToLong", Signature(systemModel.boolean), systemModel.long)
        add(system, tb, booleanToLong)
        add(system, tb, Conversion(booleanToLong, false))

        // ToDecimal(Boolean) : Decimal
        // ToDecimal(String) : Decimal
        // ToDecimal(Integer) : Decimal
        // ToDecimal(Long) : Decimal
        // ToDecimal(Decimal) : Decimal
        val stringToDecimal =
            Operator("ToDecimal", Signature(systemModel.string), systemModel.decimal)
        add(system, tb, stringToDecimal)
        add(system, tb, Conversion(stringToDecimal, false))
        val integerToDecimal =
            Operator("ToDecimal", Signature(systemModel.integer), systemModel.decimal)
        add(system, tb, integerToDecimal)
        add(system, tb, Conversion(integerToDecimal, true))
        val longToDecimal = Operator("ToDecimal", Signature(systemModel.long), systemModel.decimal)
        add(system, tb, longToDecimal)
        add(system, tb, Conversion(longToDecimal, true))
        // Operator decimalToDecimal = new Operator("ToDecimal", new
        // Signature(systemModel.getDecimal()),
        // systemModel.getDecimal());
        // add(system, tb, decimalToDecimal);
        // add(system, tb, new Conversion(decimalToDecimal, false));
        val booleanToDecimal =
            Operator("ToDecimal", Signature(systemModel.boolean), systemModel.decimal)
        add(system, tb, booleanToDecimal)
        add(system, tb, Conversion(booleanToDecimal, false))

        // ToDateTime(String) : DateTime
        // ToDateTime(Date) : DateTime
        // ToDateTime(DateTime) : DateTime
        val stringToDateTime =
            Operator("ToDateTime", Signature(systemModel.string), systemModel.dateTime)
        add(system, tb, stringToDateTime)
        add(system, tb, Conversion(stringToDateTime, false))
        val dateToDateTime =
            Operator("ToDateTime", Signature(systemModel.date), systemModel.dateTime)
        add(system, tb, dateToDateTime)
        add(system, tb, Conversion(dateToDateTime, true))

        // Operator dateTimeToDateTime = new Operator("ToDateTime", new
        // Signature(systemModel.getDateTime()),
        // systemModel.getDateTime());
        // add(system, tb, dateTimeToDateTime);
        // add(system, tb, new Conversion(dateTimeToDateTime, false));

        // ToDate(DateTime) : Date
        // ToDate(String) : Date
        // ToDate(Date) : Date
        val stringToDate = Operator("ToDate", Signature(systemModel.string), systemModel.date)
        add(system, tb, stringToDate)
        add(system, tb, Conversion(stringToDate, false))
        val dateTimeToDate = Operator("ToDate", Signature(systemModel.dateTime), systemModel.date)
        add(system, tb, dateTimeToDate)
        add(system, tb, Conversion(dateTimeToDate, false))

        // Operator dateToDate = new Operator("ToDate", new Signature(systemModel.getDate()),
        // systemModel.getDate());
        // add(system, tb, dateToDate);
        // add(system, tb, new Conversion(dateToDate, false));

        // ToTime(String) : Time
        // ToTime(Time) : Time
        val stringToTime = Operator("ToTime", Signature(systemModel.string), systemModel.time)
        add(system, tb, stringToTime)
        add(system, tb, Conversion(stringToTime, false))

        // Operator timeToTime = new Operator("ToTime", new Signature(systemModel.getTime()),
        // systemModel.getTime());
        // add(system, tb, timeToTime);
        // add(system, tb, new Conversion(timeToTime, false));

        // ToQuantity(String) : Quantity
        // ToQuantity(Integer) : Quantity
        // ToQuantity(Ratio) : Quantity
        // ToQuantity(Decimal) : Quantity
        // ToQuantity(Quantity) : Quantity
        val stringToQuantity =
            Operator("ToQuantity", Signature(systemModel.string), systemModel.quantity)
        add(system, tb, stringToQuantity)
        add(system, tb, Conversion(stringToQuantity, false))
        val ratioToQuantity =
            Operator("ToQuantity", Signature(systemModel.ratio), systemModel.quantity)
        add(system, tb, ratioToQuantity)
        add(system, tb, Conversion(ratioToQuantity, false))
        val integerToQuantity =
            Operator("ToQuantity", Signature(systemModel.integer), systemModel.quantity)
        add(system, tb, integerToQuantity)
        add(system, tb, Conversion(integerToQuantity, true))
        val decimalToQuantity =
            Operator("ToQuantity", Signature(systemModel.decimal), systemModel.quantity)
        add(system, tb, decimalToQuantity)
        add(system, tb, Conversion(decimalToQuantity, true))

        // Operator quantityToQuantity = new Operator("ToQuantity", new
        // Signature(systemModel.getQuantity()),
        // systemModel.getQuantity());
        // add(system, tb, quantityToQuantity);
        // add(system, tb, new Conversion(quantityToQuantity, false));

        // ToRatio(String) : Ratio
        // ToRatio(Ratio) : Ratio
        val stringToRatio = Operator("ToRatio", Signature(systemModel.string), systemModel.ratio)
        add(system, tb, stringToRatio)
        add(system, tb, Conversion(stringToRatio, false))

        // Operator ratioToRatio = new Operator("ToRatio", new Signature(systemModel.getRatio()),
        // systemModel.getRatio());
        // add(system, tb, ratioToRatio);
        // add(system, tb, new Conversion(ratioToRatio, false));

        // ConvertsToBoolean(Any): Boolean
        var convertsTo =
            Operator("ConvertsToBoolean", Signature(systemModel.any), systemModel.boolean)
        add(system, tb, convertsTo)
        // ConvertsToInteger(Any): Boolean
        convertsTo = Operator("ConvertsToInteger", Signature(systemModel.any), systemModel.boolean)
        add(system, tb, convertsTo)
        // ConvertsToLong(Any): Boolean
        convertsTo = Operator("ConvertsToLong", Signature(systemModel.any), systemModel.boolean)
        add(system, tb, convertsTo)
        // ConvertsToDecimal
        convertsTo = Operator("ConvertsToDecimal", Signature(systemModel.any), systemModel.boolean)
        add(system, tb, convertsTo)
        // ConvertsToDateTime
        convertsTo = Operator("ConvertsToDateTime", Signature(systemModel.any), systemModel.boolean)
        add(system, tb, convertsTo)
        // ConvertsToDate
        convertsTo = Operator("ConvertsToDate", Signature(systemModel.any), systemModel.boolean)
        add(system, tb, convertsTo)
        // ConvertsToTime
        convertsTo = Operator("ConvertsToTime", Signature(systemModel.any), systemModel.boolean)
        add(system, tb, convertsTo)
        // ConvertsToString
        convertsTo = Operator("ConvertsToString", Signature(systemModel.any), systemModel.boolean)
        add(system, tb, convertsTo)
        // ConvertsToQuantity
        convertsTo = Operator("ConvertsToQuantity", Signature(systemModel.any), systemModel.boolean)
        add(system, tb, convertsTo)
        // ConvertsToRatio
        convertsTo = Operator("ConvertsToRatio", Signature(systemModel.any), systemModel.boolean)
        add(system, tb, convertsTo)

        // CanConvertQuantity
        val canConvertToQuantity =
            Operator(
                "CanConvertQuantity",
                Signature(systemModel.quantity, systemModel.string),
                systemModel.boolean
            )
        add(system, tb, canConvertToQuantity)

        // ConvertQuantity
        val convertToQuantity =
            Operator(
                "ConvertQuantity",
                Signature(systemModel.quantity, systemModel.string),
                systemModel.quantity
            )
        add(system, tb, convertToQuantity)

        // Comparison Operators
        // Equal<T : value>(T, T) : Boolean
        // TypeParameter T = new TypeParameter("T", TypeParameter.TypeParameterConstraint.VALUE,
        // null);
        // add(system, tb, new GenericOperator("Equal", new Signature(T, T),
        // systemModel.getBoolean(), T));
        // Equal<C : class>(C, C) : Boolean
        var C = TypeParameter("C", TypeParameter.TypeParameterConstraint.CLASS, null)
        add(system, tb, GenericOperator("Equal", Signature(C, C), systemModel.boolean, C))
        // Equal<R : tuple>(R, R) : Boolean
        var R = TypeParameter("R", TypeParameter.TypeParameterConstraint.TUPLE, null)
        add(system, tb, GenericOperator("Equal", Signature(R, R), systemModel.boolean, R))
        // Equal<H : choice>(H, H) : Boolean
        var H = TypeParameter("H", TypeParameter.TypeParameterConstraint.CHOICE, null)
        add(system, tb, GenericOperator("Equal", Signature(H, H), systemModel.boolean, H))
        // Equal(Any, Any) : Boolean
        // add(system, tb, new Operator("Equal", new Signature(systemModel.getAny(),
        // systemModel.getAny()),
        // systemModel.getBoolean()));
        // Equivalent<T : value>(T, T) : Boolean
        // T = new TypeParameter("T", TypeParameter.TypeParameterConstraint.VALUE, null);
        // add(system, tb, new GenericOperator("Equivalent", new Signature(T, T),
        // systemModel.getBoolean(), T));
        // Equivalent<C : class>(C, C) : Boolean
        C = TypeParameter("C", TypeParameter.TypeParameterConstraint.CLASS, null)
        add(system, tb, GenericOperator("Equivalent", Signature(C, C), systemModel.boolean, C))
        // Equivalent<R : tuple>(R, R) : Boolean
        R = TypeParameter("R", TypeParameter.TypeParameterConstraint.TUPLE, null)
        add(system, tb, GenericOperator("Equivalent", Signature(R, R), systemModel.boolean, R))
        // Equivalent<H : choice>(H, H) : Boolean
        H = TypeParameter("H", TypeParameter.TypeParameterConstraint.CHOICE, null)
        add(system, tb, GenericOperator("Equivalent", Signature(H, H), systemModel.boolean, H))

        // Equivalent(Any, Any) : Boolean
        // add(system, tb, new Operator("Equivalent", new Signature(systemModel.getAny(),
        // systemModel.getAny()),
        // systemModel.getBoolean()));
        add(
            system,
            tb,
            Operator(
                "Equal",
                Signature(systemModel.boolean, systemModel.boolean),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equivalent",
                Signature(systemModel.boolean, systemModel.boolean),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equal",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equivalent",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Less",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "LessOrEqual",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Greater",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "GreaterOrEqual",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Equal", Signature(systemModel.long, systemModel.long), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "Equivalent",
                Signature(systemModel.long, systemModel.long),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Less", Signature(systemModel.long, systemModel.long), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "LessOrEqual",
                Signature(systemModel.long, systemModel.long),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Greater", Signature(systemModel.long, systemModel.long), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "GreaterOrEqual",
                Signature(systemModel.long, systemModel.long),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equal",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equivalent",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Less",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "LessOrEqual",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Greater",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "GreaterOrEqual",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equal",
                Signature(systemModel.string, systemModel.string),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equivalent",
                Signature(systemModel.string, systemModel.string),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Less", Signature(systemModel.string, systemModel.string), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "LessOrEqual",
                Signature(systemModel.string, systemModel.string),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Greater",
                Signature(systemModel.string, systemModel.string),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "GreaterOrEqual",
                Signature(systemModel.string, systemModel.string),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equal",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equivalent",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Less",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "LessOrEqual",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Greater",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "GreaterOrEqual",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Equal", Signature(systemModel.date, systemModel.date), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "Equivalent",
                Signature(systemModel.date, systemModel.date),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Less", Signature(systemModel.date, systemModel.date), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "LessOrEqual",
                Signature(systemModel.date, systemModel.date),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Greater", Signature(systemModel.date, systemModel.date), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "GreaterOrEqual",
                Signature(systemModel.date, systemModel.date),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Equal", Signature(systemModel.time, systemModel.time), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "Equivalent",
                Signature(systemModel.time, systemModel.time),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Less", Signature(systemModel.time, systemModel.time), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "LessOrEqual",
                Signature(systemModel.time, systemModel.time),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Greater", Signature(systemModel.time, systemModel.time), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "GreaterOrEqual",
                Signature(systemModel.time, systemModel.time),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equal",
                Signature(systemModel.quantity, systemModel.quantity),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equivalent",
                Signature(systemModel.quantity, systemModel.quantity),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Less",
                Signature(systemModel.quantity, systemModel.quantity),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "LessOrEqual",
                Signature(systemModel.quantity, systemModel.quantity),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Greater",
                Signature(systemModel.quantity, systemModel.quantity),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "GreaterOrEqual",
                Signature(systemModel.quantity, systemModel.quantity),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Equal", Signature(systemModel.ratio, systemModel.ratio), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "Equivalent",
                Signature(systemModel.ratio, systemModel.ratio),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Equal", Signature(systemModel.code, systemModel.code), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "Equivalent",
                Signature(systemModel.code, systemModel.code),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equal",
                Signature(systemModel.concept, systemModel.concept),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Equivalent",
                Signature(systemModel.concept, systemModel.concept),
                systemModel.boolean
            )
        )

        // Arithmetic Operators
        add(system, tb, Operator("Abs", Signature(systemModel.integer), systemModel.integer))
        add(system, tb, Operator("Abs", Signature(systemModel.long), systemModel.long))
        add(system, tb, Operator("Abs", Signature(systemModel.decimal), systemModel.decimal))
        add(system, tb, Operator("Abs", Signature(systemModel.quantity), systemModel.quantity))

        add(
            system,
            tb,
            Operator(
                "Add",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator("Add", Signature(systemModel.long, systemModel.long), systemModel.long)
        )
        add(
            system,
            tb,
            Operator(
                "Add",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.decimal
            )
        )
        add(
            system,
            tb,
            Operator(
                "Add",
                Signature(systemModel.quantity, systemModel.quantity),
                systemModel.quantity
            )
        )

        add(system, tb, Operator("Ceiling", Signature(systemModel.decimal), systemModel.integer))

        add(
            system,
            tb,
            Operator(
                "Divide",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.decimal
            )
        )
        // add(system, tb, new Operator("Divide", new Signature(systemModel.getQuantity(),
        // systemModel.getDecimal()),
        // systemModel.getQuantity()));
        add(
            system,
            tb,
            Operator(
                "Divide",
                Signature(systemModel.quantity, systemModel.quantity),
                systemModel.quantity
            )
        )

        add(system, tb, Operator("Exp", Signature(systemModel.decimal), systemModel.decimal))

        add(system, tb, Operator("Floor", Signature(systemModel.decimal), systemModel.integer))

        add(
            system,
            tb,
            Operator(
                "HighBoundary",
                Signature(systemModel.decimal, systemModel.integer),
                systemModel.decimal
            )
        )
        add(
            system,
            tb,
            Operator(
                "HighBoundary",
                Signature(systemModel.date, systemModel.integer),
                systemModel.date
            )
        )
        add(
            system,
            tb,
            Operator(
                "HighBoundary",
                Signature(systemModel.dateTime, systemModel.integer),
                systemModel.dateTime
            )
        )
        add(
            system,
            tb,
            Operator(
                "HighBoundary",
                Signature(systemModel.time, systemModel.integer),
                systemModel.time
            )
        )

        add(
            system,
            tb,
            Operator(
                "Log",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.decimal
            )
        )

        add(
            system,
            tb,
            Operator(
                "LowBoundary",
                Signature(systemModel.decimal, systemModel.integer),
                systemModel.decimal
            )
        )
        add(
            system,
            tb,
            Operator(
                "LowBoundary",
                Signature(systemModel.date, systemModel.integer),
                systemModel.date
            )
        )
        add(
            system,
            tb,
            Operator(
                "LowBoundary",
                Signature(systemModel.dateTime, systemModel.integer),
                systemModel.dateTime
            )
        )
        add(
            system,
            tb,
            Operator(
                "LowBoundary",
                Signature(systemModel.time, systemModel.integer),
                systemModel.time
            )
        )

        add(system, tb, Operator("Ln", Signature(systemModel.decimal), systemModel.decimal))

        // MaxValue<T>() : T
        // MinValue<T>() : T
        add(
            system,
            tb,
            Operator(
                "Modulo",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator("Modulo", Signature(systemModel.long, systemModel.long), systemModel.long)
        )
        add(
            system,
            tb,
            Operator(
                "Modulo",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.decimal
            )
        )
        add(
            system,
            tb,
            Operator(
                "Modulo",
                Signature(systemModel.quantity, systemModel.quantity),
                systemModel.quantity
            )
        )

        add(
            system,
            tb,
            Operator(
                "Multiply",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator("Multiply", Signature(systemModel.long, systemModel.long), systemModel.long)
        )
        add(
            system,
            tb,
            Operator(
                "Multiply",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.decimal
            )
        )
        add(
            system,
            tb,
            Operator(
                "Multiply",
                Signature(systemModel.quantity, systemModel.quantity),
                systemModel.quantity
            )
        )

        add(system, tb, Operator("Negate", Signature(systemModel.integer), systemModel.integer))
        add(system, tb, Operator("Negate", Signature(systemModel.long), systemModel.long))
        add(system, tb, Operator("Negate", Signature(systemModel.decimal), systemModel.decimal))
        add(system, tb, Operator("Negate", Signature(systemModel.quantity), systemModel.quantity))

        add(system, tb, Operator("Precision", Signature(systemModel.decimal), systemModel.integer))
        add(system, tb, Operator("Precision", Signature(systemModel.date), systemModel.integer))
        add(system, tb, Operator("Precision", Signature(systemModel.dateTime), systemModel.integer))
        add(system, tb, Operator("Precision", Signature(systemModel.time), systemModel.integer))

        add(
            system,
            tb,
            Operator("Predecessor", Signature(systemModel.integer), systemModel.integer)
        )
        add(system, tb, Operator("Predecessor", Signature(systemModel.long), systemModel.long))
        add(
            system,
            tb,
            Operator("Predecessor", Signature(systemModel.decimal), systemModel.decimal)
        )
        add(system, tb, Operator("Predecessor", Signature(systemModel.date), systemModel.date))
        add(
            system,
            tb,
            Operator("Predecessor", Signature(systemModel.dateTime), systemModel.dateTime)
        )
        add(system, tb, Operator("Predecessor", Signature(systemModel.time), systemModel.time))
        add(
            system,
            tb,
            Operator("Predecessor", Signature(systemModel.quantity), systemModel.quantity)
        )

        add(
            system,
            tb,
            Operator(
                "Power",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator("Power", Signature(systemModel.long, systemModel.long), systemModel.long)
        )
        add(
            system,
            tb,
            Operator(
                "Power",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.decimal
            )
        )

        add(system, tb, Operator("Round", Signature(systemModel.decimal), systemModel.decimal))
        add(
            system,
            tb,
            Operator(
                "Round",
                Signature(systemModel.decimal, systemModel.integer),
                systemModel.decimal
            )
        )

        add(
            system,
            tb,
            Operator(
                "Subtract",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator("Subtract", Signature(systemModel.long, systemModel.long), systemModel.long)
        )
        add(
            system,
            tb,
            Operator(
                "Subtract",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.decimal
            )
        )
        add(
            system,
            tb,
            Operator(
                "Subtract",
                Signature(systemModel.quantity, systemModel.quantity),
                systemModel.quantity
            )
        )

        add(system, tb, Operator("Successor", Signature(systemModel.integer), systemModel.integer))
        add(system, tb, Operator("Successor", Signature(systemModel.long), systemModel.long))
        add(system, tb, Operator("Successor", Signature(systemModel.decimal), systemModel.decimal))
        add(system, tb, Operator("Successor", Signature(systemModel.date), systemModel.date))
        add(
            system,
            tb,
            Operator("Successor", Signature(systemModel.dateTime), systemModel.dateTime)
        )
        add(system, tb, Operator("Successor", Signature(systemModel.time), systemModel.time))
        add(
            system,
            tb,
            Operator("Successor", Signature(systemModel.quantity), systemModel.quantity)
        )

        add(system, tb, Operator("Truncate", Signature(systemModel.decimal), systemModel.integer))

        add(
            system,
            tb,
            Operator(
                "TruncatedDivide",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator(
                "TruncatedDivide",
                Signature(systemModel.long, systemModel.long),
                systemModel.long
            )
        )
        add(
            system,
            tb,
            Operator(
                "TruncatedDivide",
                Signature(systemModel.decimal, systemModel.decimal),
                systemModel.decimal
            )
        )
        add(
            system,
            tb,
            Operator(
                "TruncatedDivide",
                Signature(systemModel.quantity, systemModel.quantity),
                systemModel.quantity
            )
        )

        // String operators
        add(
            system,
            tb,
            Operator("Add", Signature(systemModel.string, systemModel.string), systemModel.string)
        )
        add(
            system,
            tb,
            Operator("Combine", Signature(ListType(systemModel.string)), systemModel.string)
        )
        add(
            system,
            tb,
            Operator(
                "Combine",
                Signature(ListType(systemModel.string), systemModel.string),
                systemModel.string
            )
        )
        add(
            system,
            tb,
            Operator(
                "Concatenate",
                Signature(systemModel.string, systemModel.string),
                systemModel.string
            )
        )
        add(
            system,
            tb,
            Operator(
                "EndsWith",
                Signature(systemModel.string, systemModel.string),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Indexer",
                Signature(systemModel.string, systemModel.integer),
                systemModel.string
            )
        )
        add(
            system,
            tb,
            Operator(
                "LastPositionOf",
                Signature(systemModel.string, systemModel.string),
                systemModel.integer
            )
        )
        add(system, tb, Operator("Length", Signature(systemModel.string), systemModel.integer))
        add(system, tb, Operator("Lower", Signature(systemModel.string), systemModel.string))
        add(
            system,
            tb,
            Operator(
                "Matches",
                Signature(systemModel.string, systemModel.string),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "PositionOf",
                Signature(systemModel.string, systemModel.string),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator(
                "ReplaceMatches",
                Signature(systemModel.string, systemModel.string, systemModel.string),
                systemModel.string
            )
        )
        add(
            system,
            tb,
            Operator(
                "Split",
                Signature(systemModel.string, systemModel.string),
                ListType(systemModel.string)
            )
        )
        add(
            system,
            tb,
            Operator(
                "SplitOnMatches",
                Signature(systemModel.string, systemModel.string),
                ListType(systemModel.string)
            )
        )
        add(
            system,
            tb,
            Operator(
                "StartsWith",
                Signature(systemModel.string, systemModel.string),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Substring",
                Signature(systemModel.string, systemModel.integer),
                systemModel.string
            )
        )
        add(
            system,
            tb,
            Operator(
                "Substring",
                Signature(systemModel.string, systemModel.integer, systemModel.integer),
                systemModel.string
            )
        )
        add(system, tb, Operator("Upper", Signature(systemModel.string), systemModel.string))

        // Date/Time Operators
        add(
            system,
            tb,
            Operator(
                "Add",
                Signature(systemModel.dateTime, systemModel.quantity),
                systemModel.dateTime
            )
        )
        add(
            system,
            tb,
            Operator("Add", Signature(systemModel.date, systemModel.quantity), systemModel.date)
        )
        add(
            system,
            tb,
            Operator("Add", Signature(systemModel.time, systemModel.quantity), systemModel.time)
        )
        add(
            system,
            tb,
            Operator(
                "After",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("After", Signature(systemModel.date, systemModel.date), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator("After", Signature(systemModel.time, systemModel.time), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "Before",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("Before", Signature(systemModel.date, systemModel.date), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator("Before", Signature(systemModel.time, systemModel.time), systemModel.boolean)
        )
        add(system, tb, Operator("DateTime", Signature(systemModel.integer), systemModel.dateTime))
        add(
            system,
            tb,
            Operator(
                "DateTime",
                Signature(systemModel.integer, systemModel.integer),
                systemModel.dateTime
            )
        )
        add(
            system,
            tb,
            Operator(
                "DateTime",
                Signature(systemModel.integer, systemModel.integer, systemModel.integer),
                systemModel.dateTime
            )
        )
        add(
            system,
            tb,
            Operator(
                "DateTime",
                Signature(
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer
                ),
                systemModel.dateTime
            )
        )
        add(
            system,
            tb,
            Operator(
                "DateTime",
                Signature(
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer
                ),
                systemModel.dateTime
            )
        )
        add(
            system,
            tb,
            Operator(
                "DateTime",
                Signature(
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer
                ),
                systemModel.dateTime
            )
        )
        add(
            system,
            tb,
            Operator(
                "DateTime",
                Signature(
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer
                ),
                systemModel.dateTime
            )
        )
        add(
            system,
            tb,
            Operator(
                "DateTime",
                Signature(
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.decimal
                ),
                systemModel.dateTime
            )
        )
        add(system, tb, Operator("Date", Signature(systemModel.integer), systemModel.date))
        add(
            system,
            tb,
            Operator("Date", Signature(systemModel.integer, systemModel.integer), systemModel.date)
        )
        add(
            system,
            tb,
            Operator(
                "Date",
                Signature(systemModel.integer, systemModel.integer, systemModel.integer),
                systemModel.date
            )
        )
        add(system, tb, Operator("DateFrom", Signature(systemModel.dateTime), systemModel.date))
        add(system, tb, Operator("TimeFrom", Signature(systemModel.dateTime), systemModel.time))
        add(
            system,
            tb,
            Operator("TimezoneFrom", Signature(systemModel.dateTime), systemModel.decimal)
        )
        add(
            system,
            tb,
            Operator("TimezoneOffsetFrom", Signature(systemModel.dateTime), systemModel.decimal)
        )
        add(
            system,
            tb,
            Operator("DateTimeComponentFrom", Signature(systemModel.dateTime), systemModel.integer)
        )
        add(
            system,
            tb,
            Operator("DateTimeComponentFrom", Signature(systemModel.date), systemModel.integer)
        )
        add(
            system,
            tb,
            Operator("DateTimeComponentFrom", Signature(systemModel.time), systemModel.integer)
        )
        add(
            system,
            tb,
            Operator(
                "DifferenceBetween",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator(
                "DifferenceBetween",
                Signature(systemModel.date, systemModel.date),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator(
                "DifferenceBetween",
                Signature(systemModel.time, systemModel.time),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator(
                "DurationBetween",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator(
                "DurationBetween",
                Signature(systemModel.date, systemModel.date),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator(
                "DurationBetween",
                Signature(systemModel.time, systemModel.time),
                systemModel.integer
            )
        )
        add(system, tb, Operator("Now", Signature(), systemModel.dateTime))
        add(system, tb, Operator("now", Signature(), systemModel.dateTime))
        add(
            system,
            tb,
            Operator(
                "SameAs",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("SameAs", Signature(systemModel.date, systemModel.date), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator("SameAs", Signature(systemModel.time, systemModel.time), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "SameOrAfter",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "SameOrAfter",
                Signature(systemModel.date, systemModel.date),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "SameOrAfter",
                Signature(systemModel.time, systemModel.time),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "SameOrBefore",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "SameOrBefore",
                Signature(systemModel.date, systemModel.date),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "SameOrBefore",
                Signature(systemModel.time, systemModel.time),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "Subtract",
                Signature(systemModel.dateTime, systemModel.quantity),
                systemModel.dateTime
            )
        )
        add(
            system,
            tb,
            Operator(
                "Subtract",
                Signature(systemModel.date, systemModel.quantity),
                systemModel.date
            )
        )
        add(
            system,
            tb,
            Operator(
                "Subtract",
                Signature(systemModel.time, systemModel.quantity),
                systemModel.time
            )
        )
        add(system, tb, Operator("Today", Signature(), systemModel.date))
        add(system, tb, Operator("today", Signature(), systemModel.date))
        add(system, tb, Operator("Time", Signature(systemModel.integer), systemModel.time))
        add(
            system,
            tb,
            Operator("Time", Signature(systemModel.integer, systemModel.integer), systemModel.time)
        )
        add(
            system,
            tb,
            Operator(
                "Time",
                Signature(systemModel.integer, systemModel.integer, systemModel.integer),
                systemModel.time
            )
        )
        add(
            system,
            tb,
            Operator(
                "Time",
                Signature(
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer,
                    systemModel.integer
                ),
                systemModel.time
            )
        )
        add(system, tb, Operator("TimeOfDay", Signature(), systemModel.time))
        add(system, tb, Operator("timeOfDay", Signature(), systemModel.time))

        // Interval Operators
        // After<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "After",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Before<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Before",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Collapse<T>(list<interval<T>>) : list<interval<T>>
        // Collapse<T>(list<interval<T>>, Quantity) : list<interval<T>>
        add(
            system,
            tb,
            GenericOperator(
                "Collapse",
                Signature(ListType(IntervalType(TypeParameter("T"))), systemModel.quantity),
                ListType(IntervalType(TypeParameter("T"))),
                TypeParameter("T")
            )
        )
        // Contains<T>(interval<T>, T) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Contains",
                Signature(IntervalType(TypeParameter("T")), TypeParameter("T")),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // End<T>(interval<T>) : T
        add(
            system,
            tb,
            GenericOperator(
                "End",
                Signature(IntervalType(TypeParameter("T"))),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )
        // Ends<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Ends",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Equal<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Equal",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Equivalent<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Equivalent",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Except<T>(interval<T>, interval<T>) : interval<T>
        add(
            system,
            tb,
            GenericOperator(
                "Except",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                IntervalType(TypeParameter("T")),
                TypeParameter("T")
            )
        )
        // Expand<T>(list<interval<T>>) : list<interval<T>>
        // Expand<T>(list<interval<T>>, Quantity) : list<interval<T>>
        // Expand<T>(interval<T>) : List<T>
        // Expand<T>(interval<T>, Quantity) : list<T>
        add(
            system,
            tb,
            GenericOperator(
                "Expand",
                Signature(ListType(IntervalType(TypeParameter("T"))), systemModel.quantity),
                ListType(IntervalType(TypeParameter("T"))),
                TypeParameter("T")
            )
        )
        add(
            system,
            tb,
            GenericOperator(
                "Expand",
                Signature(IntervalType(TypeParameter("T")), systemModel.quantity),
                ListType(TypeParameter("T")),
                TypeParameter("T")
            )
        )
        // In<T>(T, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "In",
                Signature(TypeParameter("T"), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Includes<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Includes",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // IncludedIn<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "IncludedIn",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Intersect<T>(interval<T>, interval<T>) : interval<T>
        add(
            system,
            tb,
            GenericOperator(
                "Intersect",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                IntervalType(TypeParameter("T")),
                TypeParameter("T")
            )
        )
        // Meets<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Meets",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // MeetsBefore<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "MeetsBefore",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // MeetsAfter<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "MeetsAfter",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Overlaps<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Overlaps",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // OverlapsBefore<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "OverlapsBefore",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // OverlapsAfter<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "OverlapsAfter",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // PointFrom<T>(interval<T>) : T
        val pointFrom =
            GenericOperator(
                "PointFrom",
                Signature(IntervalType(TypeParameter("T"))),
                TypeParameter("T"),
                TypeParameter("T")
            )
        add(system, tb, pointFrom)
        // ProperContains<T>(interval<T>, T) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "ProperContains",
                Signature(IntervalType(TypeParameter("T")), TypeParameter("T")),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // ProperIn<T>(T, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "ProperIn",
                Signature(TypeParameter("T"), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // ProperIncludes<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "ProperIncludes",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // ProperIncludedIn<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "ProperIncludedIn",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // SameAs<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "SameAs",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // SameOrAfter<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "SameOrAfter",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // SameOrBefore<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "SameOrBefore",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Size<T>(interval<T>) : T
        add(
            system,
            tb,
            GenericOperator(
                "Size",
                Signature(IntervalType(TypeParameter("T"))),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )
        // Start<T>(interval<T>) : T
        add(
            system,
            tb,
            GenericOperator(
                "Start",
                Signature(IntervalType(TypeParameter("T"))),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )
        // Starts<T>(interval<T>, interval<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Starts",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Union<T>(interval<T>, interval<T>) : interval<T>
        add(
            system,
            tb,
            GenericOperator(
                "Union",
                Signature(IntervalType(TypeParameter("T")), IntervalType(TypeParameter("T"))),
                IntervalType(TypeParameter("T")),
                TypeParameter("T")
            )
        )
        // Width<T>(interval<T>) : T
        add(
            system,
            tb,
            GenericOperator(
                "Width",
                Signature(IntervalType(TypeParameter("T"))),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )

        // List Operators
        // Contains<T>(list<T>, T) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Contains",
                Signature(ListType(TypeParameter("T")), TypeParameter("T")),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Distinct<T>(list<T>) : list<T>
        add(
            system,
            tb,
            GenericOperator(
                "Distinct",
                Signature(ListType(TypeParameter("T"))),
                ListType(TypeParameter("T")),
                TypeParameter("T")
            )
        )
        // Equal<T>(list<T>, list<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Equal",
                Signature(ListType(TypeParameter("T")), ListType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Equivalent<T>(list<T>, list<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Equivalent",
                Signature(ListType(TypeParameter("T")), ListType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Except<T>(list<T>, list<T>) : list<T>
        add(
            system,
            tb,
            GenericOperator(
                "Except",
                Signature(ListType(TypeParameter("T")), ListType(TypeParameter("T"))),
                ListType(TypeParameter("T")),
                TypeParameter("T")
            )
        )
        // Exists<T>(list<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Exists",
                Signature(ListType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Flatten<T>(list<list<T>>) : list<T>
        add(
            system,
            tb,
            GenericOperator(
                "Flatten",
                Signature(ListType(ListType(TypeParameter("T")))),
                ListType(TypeParameter("T")),
                TypeParameter("T")
            )
        )
        // First<T>(list<T>) : T
        add(
            system,
            tb,
            GenericOperator(
                "First",
                Signature(ListType(TypeParameter("T"))),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )
        // In<T>(T, list<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "In",
                Signature(TypeParameter("T"), ListType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Includes<T>(list<T>, list<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "Includes",
                Signature(ListType(TypeParameter("T")), ListType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // IncludedIn<T>(list<T>, list<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "IncludedIn",
                Signature(ListType(TypeParameter("T")), ListType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // Indexer<T>(list<T>, integer) : T
        add(
            system,
            tb,
            GenericOperator(
                "Indexer",
                Signature(ListType(TypeParameter("T")), systemModel.integer),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )
        // IndexOf<T>(list<T>, T) : Integer
        add(
            system,
            tb,
            GenericOperator(
                "IndexOf",
                Signature(ListType(TypeParameter("T")), TypeParameter("T")),
                systemModel.integer,
                TypeParameter("T")
            )
        )
        // Intersect<T>(list<T>, list<T>) : list<T>
        add(
            system,
            tb,
            GenericOperator(
                "Intersect",
                Signature(ListType(TypeParameter("T")), ListType(TypeParameter("T"))),
                ListType(TypeParameter("T")),
                TypeParameter("T")
            )
        )
        // Last<T>(list<T>) : T
        add(
            system,
            tb,
            GenericOperator(
                "Last",
                Signature(ListType(TypeParameter("T"))),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )
        // Length<T>(list<T>) : Integer
        add(
            system,
            tb,
            GenericOperator(
                "Length",
                Signature(ListType(TypeParameter("T"))),
                systemModel.integer,
                TypeParameter("T")
            )
        )
        // ProperContains<T>(list<T>, T) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "ProperContains",
                Signature(ListType(TypeParameter("T")), TypeParameter("T")),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // ProperIn<T>(T, list<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "ProperIn",
                Signature(TypeParameter("T"), ListType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // ProperIncludes<T>(list<T>, list<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "ProperIncludes",
                Signature(ListType(TypeParameter("T")), ListType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // ProperIncludedIn<T>(list<T>, list<T>) : Boolean
        add(
            system,
            tb,
            GenericOperator(
                "ProperIncludedIn",
                Signature(ListType(TypeParameter("T")), ListType(TypeParameter("T"))),
                systemModel.boolean,
                TypeParameter("T")
            )
        )
        // SingletonFrom<T>(list<T>) : T
        val singletonFrom =
            GenericOperator(
                "SingletonFrom",
                Signature(ListType(TypeParameter("T"))),
                TypeParameter("T"),
                TypeParameter("T")
            )
        add(system, tb, singletonFrom)
        //// NOTE: FHIRPath Implicit List Demotion
        // Generic conversions turned out to be computationally expensive, so we added explicit list
        // promotion/demotion
        // in the conversion map directly instead.
        // add(system, tb, new Conversion(singletonFrom, true));
        // Skip(list<T>, Integer): list<T>
        add(
            system,
            tb,
            GenericOperator(
                "Skip",
                Signature(ListType(TypeParameter("T")), systemModel.integer),
                ListType(TypeParameter("T")),
                TypeParameter("T")
            )
        )
        // Tail(list<T>): list<T>
        add(
            system,
            tb,
            GenericOperator(
                "Tail",
                Signature(ListType(TypeParameter("T"))),
                ListType(TypeParameter("T")),
                TypeParameter("T")
            )
        )
        // Take(list<T>, Integer): list<T>
        add(
            system,
            tb,
            GenericOperator(
                "Take",
                Signature(ListType(TypeParameter("T")), systemModel.integer),
                ListType(TypeParameter("T")),
                TypeParameter("T")
            )
        )
        // Union<T>(list<T>, list<T>) : list<T>
        add(
            system,
            tb,
            GenericOperator(
                "Union",
                Signature(ListType(TypeParameter("T")), ListType(TypeParameter("T"))),
                ListType(TypeParameter("T")),
                TypeParameter("T")
            )
        )

        // NOTE: FHIRPath Implicit List Promotion operator
        // GenericOperator toList = new GenericOperator("List", new Signature(new
        // TypeParameter("T")), new ListType(new
        // TypeParameter("T")), new TypeParameter("T"));
        // add(system, tb, toList);
        // add(system, tb, new Conversion(toList, true));

        // Aggregate Operators
        add(
            system,
            tb,
            Operator("AllTrue", Signature(ListType(systemModel.boolean)), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator("AnyTrue", Signature(ListType(systemModel.boolean)), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator("Avg", Signature(ListType(systemModel.decimal)), systemModel.decimal)
        )
        add(
            system,
            tb,
            Operator("Avg", Signature(ListType(systemModel.quantity)), systemModel.quantity)
        )
        // Count<T>(list<T>) : Integer
        add(
            system,
            tb,
            GenericOperator(
                "Count",
                Signature(ListType(TypeParameter("T"))),
                systemModel.integer,
                TypeParameter("T")
            )
        )
        //// Count(list<Any>) : Integer
        // add(system, tb, new Operator("Count", new Signature(new ListType(systemModel.getAny())),
        // systemModel.getInteger()));
        add(
            system,
            tb,
            Operator("GeometricMean", Signature(ListType(systemModel.decimal)), systemModel.decimal)
        )
        add(
            system,
            tb,
            Operator("Max", Signature(ListType(systemModel.integer)), systemModel.integer)
        )
        add(system, tb, Operator("Max", Signature(ListType(systemModel.long)), systemModel.long))
        add(
            system,
            tb,
            Operator("Max", Signature(ListType(systemModel.decimal)), systemModel.decimal)
        )
        add(
            system,
            tb,
            Operator("Max", Signature(ListType(systemModel.quantity)), systemModel.quantity)
        )
        add(
            system,
            tb,
            Operator("Max", Signature(ListType(systemModel.dateTime)), systemModel.dateTime)
        )
        add(system, tb, Operator("Max", Signature(ListType(systemModel.date)), systemModel.date))
        add(system, tb, Operator("Max", Signature(ListType(systemModel.time)), systemModel.time))
        add(
            system,
            tb,
            Operator("Max", Signature(ListType(systemModel.string)), systemModel.string)
        )
        add(
            system,
            tb,
            Operator("Min", Signature(ListType(systemModel.integer)), systemModel.integer)
        )
        add(system, tb, Operator("Min", Signature(ListType(systemModel.long)), systemModel.long))
        add(
            system,
            tb,
            Operator("Min", Signature(ListType(systemModel.decimal)), systemModel.decimal)
        )
        add(
            system,
            tb,
            Operator("Min", Signature(ListType(systemModel.quantity)), systemModel.quantity)
        )
        add(
            system,
            tb,
            Operator("Min", Signature(ListType(systemModel.dateTime)), systemModel.dateTime)
        )
        add(system, tb, Operator("Min", Signature(ListType(systemModel.date)), systemModel.date))
        add(system, tb, Operator("Min", Signature(ListType(systemModel.time)), systemModel.time))
        add(
            system,
            tb,
            Operator("Min", Signature(ListType(systemModel.string)), systemModel.string)
        )
        add(
            system,
            tb,
            Operator("Median", Signature(ListType(systemModel.decimal)), systemModel.decimal)
        )
        add(
            system,
            tb,
            Operator("Median", Signature(ListType(systemModel.quantity)), systemModel.quantity)
        )
        // Mode<T>(list<T>) : T
        add(
            system,
            tb,
            GenericOperator(
                "Mode",
                Signature(ListType(TypeParameter("T"))),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )
        add(
            system,
            tb,
            Operator(
                "PopulationStdDev",
                Signature(ListType(systemModel.decimal)),
                systemModel.decimal
            )
        )
        add(
            system,
            tb,
            Operator(
                "PopulationStdDev",
                Signature(ListType(systemModel.quantity)),
                systemModel.quantity
            )
        )
        add(
            system,
            tb,
            Operator(
                "PopulationVariance",
                Signature(ListType(systemModel.decimal)),
                systemModel.decimal
            )
        )
        add(
            system,
            tb,
            Operator(
                "PopulationVariance",
                Signature(ListType(systemModel.quantity)),
                systemModel.quantity
            )
        )
        add(
            system,
            tb,
            Operator("Product", Signature(ListType(systemModel.integer)), systemModel.integer)
        )
        add(
            system,
            tb,
            Operator("Product", Signature(ListType(systemModel.long)), systemModel.long)
        )
        add(
            system,
            tb,
            Operator("Product", Signature(ListType(systemModel.decimal)), systemModel.decimal)
        )
        add(
            system,
            tb,
            Operator("Product", Signature(ListType(systemModel.quantity)), systemModel.quantity)
        )
        add(
            system,
            tb,
            Operator("StdDev", Signature(ListType(systemModel.decimal)), systemModel.decimal)
        )
        add(
            system,
            tb,
            Operator("StdDev", Signature(ListType(systemModel.quantity)), systemModel.quantity)
        )
        add(
            system,
            tb,
            Operator("Sum", Signature(ListType(systemModel.integer)), systemModel.integer)
        )
        add(system, tb, Operator("Sum", Signature(ListType(systemModel.long)), systemModel.long))
        add(
            system,
            tb,
            Operator("Sum", Signature(ListType(systemModel.decimal)), systemModel.decimal)
        )
        add(
            system,
            tb,
            Operator("Sum", Signature(ListType(systemModel.quantity)), systemModel.quantity)
        )
        add(
            system,
            tb,
            Operator("Variance", Signature(ListType(systemModel.decimal)), systemModel.decimal)
        )
        add(
            system,
            tb,
            Operator("Variance", Signature(ListType(systemModel.quantity)), systemModel.quantity)
        )

        // Clinical
        // ToConcept(Code)
        val codeToConcept = Operator("ToConcept", Signature(systemModel.code), systemModel.concept)
        add(system, tb, codeToConcept)
        add(system, tb, Conversion(codeToConcept, true))
        // ToConcept(list<Code>)
        val codesToConcept =
            Operator("ToConcept", Signature(ListType(systemModel.code)), systemModel.concept)
        add(system, tb, codesToConcept)
        add(system, tb, Conversion(codesToConcept, false))

        add(
            system,
            tb,
            Operator("CalculateAge", Signature(systemModel.dateTime), systemModel.integer)
        )
        add(system, tb, Operator("CalculateAge", Signature(systemModel.date), systemModel.integer))
        add(
            system,
            tb,
            Operator(
                "CalculateAgeAt",
                Signature(systemModel.dateTime, systemModel.dateTime),
                systemModel.integer
            )
        )
        add(
            system,
            tb,
            Operator(
                "CalculateAgeAt",
                Signature(systemModel.date, systemModel.date),
                systemModel.integer
            )
        )

        add(system, tb, Operator("InValueSet", Signature(systemModel.string), systemModel.boolean))
        add(system, tb, Operator("InValueSet", Signature(systemModel.code), systemModel.boolean))
        add(system, tb, Operator("InValueSet", Signature(systemModel.concept), systemModel.boolean))

        add(
            system,
            tb,
            Operator(
                "InValueSet",
                Signature(systemModel.string, systemModel.valueSet),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "InValueSet",
                Signature(systemModel.code, systemModel.valueSet),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "InValueSet",
                Signature(systemModel.concept, systemModel.valueSet),
                systemModel.boolean
            )
        )

        add(
            system,
            tb,
            Operator("AnyInValueSet", Signature(ListType(systemModel.string)), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator("AnyInValueSet", Signature(ListType(systemModel.code)), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator("AnyInValueSet", Signature(ListType(systemModel.concept)), systemModel.boolean)
        )

        add(
            system,
            tb,
            Operator(
                "AnyInValueSet",
                Signature(ListType(systemModel.string), systemModel.valueSet),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "AnyInValueSet",
                Signature(ListType(systemModel.code), systemModel.valueSet),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "AnyInValueSet",
                Signature(ListType(systemModel.concept), systemModel.valueSet),
                systemModel.boolean
            )
        )

        add(
            system,
            tb,
            Operator("InCodeSystem", Signature(systemModel.string), systemModel.boolean)
        )
        add(system, tb, Operator("InCodeSystem", Signature(systemModel.code), systemModel.boolean))
        add(
            system,
            tb,
            Operator("InCodeSystem", Signature(systemModel.concept), systemModel.boolean)
        )

        add(
            system,
            tb,
            Operator(
                "InCodeSystem",
                Signature(systemModel.string, systemModel.codeSystem),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "InCodeSystem",
                Signature(systemModel.code, systemModel.codeSystem),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "InCodeSystem",
                Signature(systemModel.concept, systemModel.codeSystem),
                systemModel.boolean
            )
        )

        add(
            system,
            tb,
            Operator(
                "AnyInCodeSystem",
                Signature(ListType(systemModel.string)),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator("AnyInCodeSystem", Signature(ListType(systemModel.code)), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "AnyInCodeSystem",
                Signature(ListType(systemModel.concept)),
                systemModel.boolean
            )
        )

        add(
            system,
            tb,
            Operator(
                "AnyInCodeSystem",
                Signature(ListType(systemModel.string), systemModel.codeSystem),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "AnyInCodeSystem",
                Signature(ListType(systemModel.code), systemModel.codeSystem),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "AnyInCodeSystem",
                Signature(ListType(systemModel.concept), systemModel.codeSystem),
                systemModel.boolean
            )
        )

        val expandValueSet =
            Operator("ExpandValueSet", Signature(systemModel.valueSet), ListType(systemModel.code))
        add(system, tb, expandValueSet)
        add(system, tb, Conversion(expandValueSet, true))

        add(
            system,
            tb,
            Operator("Subsumes", Signature(systemModel.code, systemModel.code), systemModel.boolean)
        )
        add(
            system,
            tb,
            Operator(
                "Subsumes",
                Signature(systemModel.concept, systemModel.concept),
                systemModel.boolean
            )
        )

        add(
            system,
            tb,
            Operator(
                "SubsumedBy",
                Signature(systemModel.code, systemModel.code),
                systemModel.boolean
            )
        )
        add(
            system,
            tb,
            Operator(
                "SubsumedBy",
                Signature(systemModel.concept, systemModel.concept),
                systemModel.boolean
            )
        )

        // Errors
        // Message(source T, condition Boolean, code String, severity String, message String) T
        add(
            system,
            tb,
            GenericOperator(
                "Message",
                Signature(
                    TypeParameter("T"),
                    systemModel.boolean,
                    systemModel.string,
                    systemModel.string,
                    systemModel.string
                ),
                TypeParameter("T"),
                TypeParameter("T")
            )
        )

        return system
    }

    private fun add(systemLibrary: CompiledLibrary, tb: TypeBuilder, operator: Operator) {
        // In the case that an operator is added directly, manufacture a FunctionDef so it can be
        // referred to in ELM
        // Analysis
        val fd = FunctionDef()
        fd.name = operator.name
        var n = 0
        for (dataType in operator.signature.operandTypes) {
            n++
            val od = OperandDef().withName("param${n}")
            if (dataType is NamedType) {
                od.operandType = tb.dataTypeToQName(dataType)
            } else {
                od.operandTypeSpecifier = tb.dataTypeToTypeSpecifier(dataType)
            }
            od.resultType = dataType
            fd.operand.add(od)
        }
        operator.functionDef = fd

        systemLibrary.add(fd, operator)
    }

    @Suppress("UnusedParameter")
    private fun add(systemLibrary: CompiledLibrary, tb: TypeBuilder, conversion: Conversion) {
        systemLibrary.add(conversion)
    }
}
