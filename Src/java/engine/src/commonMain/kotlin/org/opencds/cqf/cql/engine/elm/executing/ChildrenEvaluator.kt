package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.*

/*

Children(argument Any) List<Any>

For structured types, the Children operator returns a list of all the values of the elements of the type.
    List-valued elements are expanded and added to the result individually, rather than as a single list.
For list types, the result is the same as invoking Children on each element in the list and flattening the resulting lists into a single result.
If the source is null, the result is null.

*/
object ChildrenEvaluator {
    private fun addQuantity(list: MutableList<Any?>, quantity: Quantity) {
        list.add(quantity.value)
        list.add(quantity.unit)
    }

    private fun addCode(list: MutableList<Any?>, code: Code?) {
        list.add(code!!.system)
        list.add(code.version)
        list.add(code.code)
        list.add(code.system)
    }

    private fun addConcept(list: MutableList<Any?>, concept: Concept) {
        for (code in concept.codes!!) {
            addCode(list, code)
        }

        list.add(concept.display)
    }

    private fun addDateTime(list: MutableList<Any?>, dateTime: DateTime) {
        for (i in 0..<dateTime.precision!!.toDateTimeIndex() + 1) {
            list.add(dateTime.dateTime!!.get(Precision.fromDateTimeIndex(i).toChronoField()))
        }

        list.add(TemporalHelper.zoneToOffset(dateTime.dateTime!!.getOffset()))
    }

    private fun addTime(list: MutableList<Any?>, time: Time) {
        for (i in 0..<time.precision!!.toTimeIndex() + 1) {
            list.add(time.time.get(Precision.fromTimeIndex(i).toChronoField()))
        }
    }

    private fun addList(list: MutableList<Any?>, listToProcess: Iterable<*>) {
        for (o in listToProcess) {
            list.add(children(o))
        }
    }

    @JvmStatic
    fun children(source: Any?): Any? {
        if (source == null) {
            return null
        }

        val ret: MutableList<Any?> = ArrayList()

        when (source) {
            is Int,
            is BigDecimal,
            is String,
            is Boolean -> ret.add(source)

            is Quantity -> addQuantity(ret, source)

            is Code -> addCode(ret, source)

            is Concept -> addConcept(ret, source)

            is DateTime -> addDateTime(ret, source)

            is Time -> addTime(ret, source)

            is Iterable<*> -> addList(ret, source)
        }

        // TODO: Intervals and Tuples?
        return ret
    }
}
