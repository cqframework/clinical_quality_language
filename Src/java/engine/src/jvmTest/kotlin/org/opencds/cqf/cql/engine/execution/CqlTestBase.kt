package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlTranslator.Companion.fromText
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.BeforeEach
import org.opencds.cqf.cql.engine.runtime.TemporalHelper

open class CqlTestBase {
    protected open val cqlSubdirectory: String?
        // This can either be null for the root directory, or non-null for a subdirectory
        get() = null

    protected val libraryManager: LibraryManager
        get() = getLibraryManager(createOptionsMin())

    protected fun getLibraryManager(compilerOptions: CqlCompilerOptions): LibraryManager {
        val manager = LibraryManager(modelManager!!, compilerOptions)
        manager.librarySourceLoader.registerProvider(
            TestLibrarySourceProvider(this.cqlSubdirectory)
        )

        return manager
    }

    fun getLibrary(
        libraryId: VersionedIdentifier,
        errors: MutableList<CqlCompilerException>,
        options: CqlCompilerOptions,
    ): Library? {
        return getLibraryManager(options).resolveLibrary(libraryId, errors).library
    }

    fun getLibrary(
        libraryId: VersionedIdentifier,
        errors: MutableList<CqlCompilerException>,
    ): Library? {
        return environment!!.libraryManager!!.resolveLibrary(libraryId, errors).library
    }

    fun getLibrary(libraryId: VersionedIdentifier): Library? {
        return this.getLibrary(libraryId, mutableListOf())
    }

    fun toLibrary(text: String): Library? {
        return toLibrary(text, this.libraryManager)
    }

    fun toLibrary(text: String, libraryManager: LibraryManager): Library? {
        val translator = fromText(text, libraryManager)
        return translator.toELM()
    }

    fun getEngine(cqlCompilerOptions: CqlCompilerOptions): CqlEngine {
        val env = Environment(getLibraryManager(cqlCompilerOptions))
        return CqlEngine(env, mutableSetOf(CqlEngine.Options.EnableTypeChecking))
    }

    var environment: Environment? = null

    private var _engine: CqlEngine? = null
    val engine: CqlEngine
        get() {
            if (_engine == null) {
                _engine =
                    CqlEngine(
                        Environment(libraryManager),
                        mutableSetOf(CqlEngine.Options.EnableTypeChecking),
                    )
            }
            return _engine!!
        }

    @BeforeEach
    protected fun beforeEachMethod() {
        environment = Environment(this.libraryManager)
        _engine = null
    }

    val bigDecimalZoneOffset: BigDecimal
        get() = getBigDecimalZoneOffset(engine.state.evaluationZonedDateTime!!.offset)

    fun getBigDecimalZoneOffset(zoneId: ZoneId): BigDecimal {
        return getBigDecimalZoneOffset(zoneId.rules.getOffset(Instant.now()))
    }

    private fun getBigDecimalZoneOffset(zoneOffset: ZoneOffset): BigDecimal {
        return TemporalHelper.zoneToOffset(zoneOffset)
    }

    /**
     * @param libraryIdentifier the library where the expression is defined
     * @param expressionName the name of the expression to evaluate
     * @param evaluationDateTime the value for "Now()"
     * @return the result of the expression
     */
    protected fun CqlEngine.expression(
        libraryIdentifier: VersionedIdentifier,
        expressionName: String,
        evaluationDateTime: ZonedDateTime? = null,
    ): Any? {
        return evaluate {
                library(libraryIdentifier) { expressions(expressionName) }
                this.evaluationDateTime = evaluationDateTime
            }
            .onlyResultOrThrow[expressionName]!!
            .value
    }

    companion object {
        const val NORTH_AMERICA_MOUNTAIN: String =
            "America/Denver" // This is the baseline:  Normal hour on the hour timezone
        const val NEWFOUNDLAND: String = "America/St_Johns"
        const val INDIA: String = "Asia/Kolkata"
        const val AUSTRALIA_NORTHERN_TERRITORY: String = "Australia/Darwin"
        const val AUSTRALIA_EUCLA: String = "Australia/Eucla"
        const val AUSTRALIA_BROKEN_HILL: String = "Australia/Broken_Hill"
        const val AUSTRALIA_LORD_HOWE: String = "Australia/Lord_Howe"
        const val AUSTRALIA_SOUTH: String = "Australia/Adelaide"
        const val INDIAN_COCOS: String = "Indian/Cocos"
        const val PACIFIC_CHATHAM: String = "Pacific/Chatham"

        @JvmStatic
        fun timezones(): Array<Array<Any>> {
            return arrayOf(
                arrayOf(NORTH_AMERICA_MOUNTAIN),
                arrayOf(NEWFOUNDLAND),
                arrayOf(INDIA),
                arrayOf(AUSTRALIA_NORTHERN_TERRITORY),
                arrayOf(AUSTRALIA_EUCLA),
                arrayOf(AUSTRALIA_BROKEN_HILL),
                arrayOf(AUSTRALIA_LORD_HOWE),
                arrayOf(AUSTRALIA_SOUTH),
                arrayOf(INDIAN_COCOS),
                arrayOf(PACIFIC_CHATHAM),
            )
        }

        protected var modelManager: ModelManager? = null
            get() {
                if (field == null) {
                    field = ModelManager()
                }

                return field
            }
            private set

        @JvmStatic
        fun toElmIdentifier(name: String?): VersionedIdentifier {
            return VersionedIdentifier().withId(name)
        }

        fun toElmIdentifier(name: String?, version: String?): VersionedIdentifier {
            return VersionedIdentifier().withId(name).withVersion(version)
        }

        fun createOptionsMin(): CqlCompilerOptions {
            val result = CqlCompilerOptions()
            result.setOptions(
                CqlCompilerOptions.Options.EnableDateRangeOptimization,
                CqlCompilerOptions.Options.EnableLocators,
                CqlCompilerOptions.Options.EnableResultTypes,
                CqlCompilerOptions.Options.DisableListDemotion,
                CqlCompilerOptions.Options.DisableListPromotion,
                CqlCompilerOptions.Options.DisableMethodInvocation,
            )

            return result
        }
    }
}
