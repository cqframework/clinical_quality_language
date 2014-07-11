package org.cqframework.cql.poc.translator;

import org.antlr.v4.runtime.misc.NotNull;
import org.cqframework.cql.gen.cqlBaseListener;
import org.cqframework.cql.gen.cqlParser;

public class CqlTranslatorListener extends cqlBaseListener {
    @Override
    public void enterLetStatement(@NotNull cqlParser.LetStatementContext ctx) {
        System.out.println("Entering let statement");
    }

    @Override
    public void exitLetStatement(@NotNull cqlParser.LetStatementContext ctx) {
        System.out.println("Exiting let statement");
    }
}
