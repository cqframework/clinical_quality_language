package org.cqframework.cql.elm.requirements

class ElmQuerySelectivity {
    enum class Coverage(val coverage: String) {
        TOTAL(
            "total"
        ), // Indicates the query selectivity completely covers the criteria in the query (i.e. the
        // query is in CNF/DNF, and all terms are sargeable)
        PARTIAL(
            "partial"
        ), // Indicates the query selectivity partially covers the criteria in the query (i.e. the
        // query is in CNF/DNF, but not all terms are sargeable)
        NON(
            "non"
        ), // Indicates the query selectivity cannot be determined for the query (i.e. the query is
        // not in CNF/DNF)
    }

    var coverage: Coverage? =
        null // total | partial selectivity, if the determination is non-selective, nothing is

    // reported

    enum class Inclusivity(val inclusivity: String) {
        INCLUSION(
            "inclusion"
        ), // Indicates the query selectivity represents an inclusion criteria with respect to the
        // context of the query
        EXCLUSION(
            "exclusion"
        ), // Indicates the query selectivity represents an exclusion criteria with respect to the
        // context of the query
        INDETERMINATE(
            "indeterminate"
        ), // Indicates that whether the query selectivity represents inclusion or exclusion criteria
        // cannot be determined
    }

    var inclusivity: Inclusivity? =
        null // inclusion | exclusion, if no determination is made for type, the selectivity cannot

    // be used as a filter

    enum class Form(val form: String) {
        CONJUNCTIVE(
            "conjunctive"
        ), // Indicates the query selectivity represents criteria in Conjunctive Normal Form (CNF)
        // (i.e. a conjunction of disjunctions)
        DISJUNCTIVE(
            "disjunctive"
        ), // Indicates the query selectivity represents criteria in Disjunctive Normal Form (DNF)
        // (i.e. a disjunction of conjunctions)
    }

    var form: Form? = null // conjunctive | disjunctive

    class Clause {
        val terms: MutableList<ElmDataRequirement> = ArrayList()
            get() = field
    }

    val clause: MutableList<Clause> = ArrayList()
        get() = field

    fun isValid(): Boolean {
        var hasClause = false
        var hasTerms = false
        for (clause in clause) {
            hasClause = true
            for (term in clause.terms) {
                hasTerms = true
                if (term.retrieve.dataType == null) {
                    hasTerms = false
                    break
                }
            }
            if (!hasTerms) {
                break
            }
        }

        return hasClause && hasTerms
    }
}
