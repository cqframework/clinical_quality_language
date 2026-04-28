package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.TemporalHelper
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

/*

Children(argument Any) List<Any>

For structured types, the Children operator returns a list of all the values of the elements of the type.
    List-valued elements are expanded and added to the result individually, rather than as a single list.
For list types, the result is the same as invoking Children on each element in the list and flattening the resulting lists into a single result.
If the source is null, the result is null.

*/
object ChildrenEvaluator {
    private fun addQuantity(list: MutableList<Value?>, quantity: Quantity) {
        list.add(quantity.value?.toCqlDecimal())
        list.add(quantity.unit?.toCqlString())
    }

    private fun addCode(list: MutableList<Value?>, code: Code?) {
        list.add(code!!.system?.toCqlString())
        list.add(code.version?.toCqlString())
        list.add(code.code?.toCqlString())
        list.add(code.system?.toCqlString())
    }

    private fun addConcept(list: MutableList<Value?>, concept: Concept) {
        for (code in concept.codes!!) {
            addCode(list, code)
        }

        list.add(concept.display?.toCqlString())
    }

    private fun addDateTime(list: MutableList<Value?>, dateTime: DateTime) {
        for (i in 0..<dateTime.precision!!.toDateTimeIndex() + 1) {
            list.add(
                dateTime.dateTime!!
                    .get(Precision.fromDateTimeIndex(i).toChronoField())
                    .toCqlInteger()
            )
        }

        list.add(TemporalHelper.zoneToOffset(dateTime.dateTime!!.getOffset()).toCqlDecimal())
    }

    private fun addTime(list: MutableList<Value?>, time: Time) {
        for (i in 0..<time.precision!!.toTimeIndex() + 1) {
            list.add(time.time.get(Precision.fromTimeIndex(i).toChronoField()).toCqlInteger())
        }
    }

    private fun addList(list: MutableList<Value?>, listToProcess: List) {
        for (o in listToProcess) {
            list.add(children(o))
        }
    }

    @JvmStatic
    fun children(source: Value?): List? {
        if (source == null) {
            return null
        }

        val ret: MutableList<Value?> = ArrayList()

        when (source) {
            is Integer,
            is Decimal,
            is String,
            is Boolean -> ret.add(source)

            is Quantity -> addQuantity(ret, source)

            is Code -> addCode(ret, source)

            is Concept -> addConcept(ret, source)

            is DateTime -> addDateTime(ret, source)

            is Time -> addTime(ret, source)

            is List -> addList(ret, source)

            else -> {}
        }

        // TODO: Intervals and Tuples?
        return ret.toCqlList()
    }
}
