package org.opencds.cqf.cql.engine.data;

import java.util.function.Supplier;

import org.opencds.cqf.cql.engine.elm.execution.obfuscate.NoOpPHIObfuscator;
import org.opencds.cqf.cql.engine.elm.execution.obfuscate.PHIObfuscator;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;

public interface DataProvider extends ModelResolver, RetrieveProvider {
    default Supplier<PHIObfuscator> phiObfuscationSupplier() {
        return NoOpPHIObfuscator::new;
    }
}
