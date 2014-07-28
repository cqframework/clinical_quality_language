package org.cqframework.cql.poc.translator.expressions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bobd on 7/23/14.
 */
public class TimingExpression extends Expression{

    public static Map<String,String> mapping = new HashMap<String,String>();
    static{
        mapping.put("concurrentwith","CW");
        mapping.put("startsconcurrentwith","SCW");
        mapping.put("startsconcurrentwithstart","SCWS");
        mapping.put("startsconcurrentwithend","SCWE");
        mapping.put("endsconcurrentwith","ECW");
        mapping.put("endsconcurrentwithstart","ECWS");
        mapping.put("endsconcurrentwithend","ECWE");
        mapping.put("concurrentwithstart","CWS");
        mapping.put("concurrentwithend","CWE");

        mapping.put("startsbeforestart","SBS");
        mapping.put("startsbeforeend","SBE");
        mapping.put("endsbeforestart","EBS");
        mapping.put("endsbeforeend","EBE");
        mapping.put("startsafterstart","SAS");
        mapping.put("startsafterend","SAE");
        mapping.put("endsafterstart","EAS");
        mapping.put("endsafterend","EAE");
        mapping.put("overlaps","OVERLAPS");
        mapping.put("overlapsbefore","OB");
        mapping.put("overlapsafter","OA");

        mapping.put("includes","INCLDUES");
        mapping.put("includesstart","INS");
        mapping.put("includesend","INE");
        mapping.put("properlyincludes","PIN");
        mapping.put("properlyincludesstart","PINS");
        mapping.put("properlyincludesend","PINE");
        mapping.put("during","DURING");
        mapping.put("startsduring","SDU");
        mapping.put("endsduring","EDU");
        mapping.put("includedin","INCLUDE");
        mapping.put("startsincludedin","SIN");
        mapping.put("endsincludedin","EIN");
        mapping.put("endsincludedin","EIN");


        mapping.put("meets","MEETS");
        mapping.put("meetsbefore","MB");
        mapping.put("meetsafter","MA");
        mapping.put("starts","STARTS");
        mapping.put("ends","ENDS");
        mapping.put("startedsby","STARTED_BY");
        mapping.put("endedby","ENDED_BY");


    }

    Expression left;
    Expression right;
    String code;
    QuantityLiteral offset;
    boolean properly;

    public TimingExpression(Expression left, Expression right, String code, QuantityLiteral offset, boolean properly) {
        this.left = left;
        this.right = right;
        this.code = code;
        this.offset = offset;
        this.properly = properly;
    }

    public static Map<String, String> getMapping() {
        return mapping;
    }

    public static void setMapping(Map<String, String> mapping) {
        TimingExpression.mapping = mapping;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public QuantityLiteral getOffset() {
        return offset;
    }

    public void setOffset(QuantityLiteral offset) {
        this.offset = offset;
    }

    public boolean isProperly() {
        return properly;
    }

    public void setProperly(boolean properly) {
        this.properly = properly;
    }
}
