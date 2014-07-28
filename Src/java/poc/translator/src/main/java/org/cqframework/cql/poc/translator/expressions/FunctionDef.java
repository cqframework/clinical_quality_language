package org.cqframework.cql.poc.translator.expressions;

import java.util.List;

/**
 * Created by bobd on 7/23/14.
 */
public class FunctionDef extends Expression{

    List<OperandDefinition> arguments;
    String identifier;
    Expression _return;

    public FunctionDef(String identifier, List<OperandDefinition> arguments, Expression _return) {
        this.arguments = arguments;
        this.identifier = identifier;
        this._return = _return;
    }

    public List<OperandDefinition> getArguments() {
        return arguments;
    }

    public void setArguments(List<OperandDefinition> arguments) {
        this.arguments = arguments;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Expression getReturn() {
        return _return;
    }

    public void setReturn(Expression _return) {
        this._return = _return;
    }
}
