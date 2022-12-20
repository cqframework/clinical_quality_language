package org.opencds.cqf.cql.engine.exception;

public class InvalidOperatorArgument extends CqlException
{
    private static final long serialVersionUID = 1L;

    public InvalidOperatorArgument(String message)
    {
        super(message);
    }

    public InvalidOperatorArgument(String expected, String found)
    {
        super(String.format("Expected %s, Found %s", expected, found));
    }
}
