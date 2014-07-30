package org.cqframework.cql.poc.translator.expressions;

import java.util.List;

/**
 * Created by bobd on 7/23/14.
 */
public class SortClause extends Expression{

    public enum SortDirection {
        acs,
        desc;
    }

   SortDirection direction;
   List<SortItem> sortItems;

    public SortClause(SortDirection direction, List<SortItem> sortItems) {
        super();
        this.direction = direction;
        this.sortItems = sortItems;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }

    public List<SortItem> getSortItems() {
        return sortItems;
    }

    public void setSortItems(List<SortItem> sortItems) {
        this.sortItems = sortItems;
    }

    @Override
    public String toCql() {
        StringBuffer buff = new StringBuffer();
        if(direction !=null){
            buff.append(direction.name());
        }else{
            for (SortItem sortItem : sortItems) {
                buff.append("by ");
                buff.append(sortItem.toCql());

            }
        }

        return buff.toString();
    }
}
