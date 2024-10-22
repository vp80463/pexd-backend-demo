package com.a1stream.common.utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("rawtypes")
public class GroupListResult extends HashMap {

    private static final long serialVersionUID = 7469421259524239375L;

    public GroupListResult(){
        super();
    }

    public Object getModelObject(){
        return this.get(ListFilterUtil.GET_MODEL_KEY);
    }

    public Object getModelObjectList(){
        return this.get(ListFilterUtil.GET_MODEL_LIST_KEY);
    }

    public Object getCountResult(){
        return this.get(ListFilterUtil.GET_COUNT_RESULT_KEY);
    }

    public Object getSumResult(String fieldName){
        Object reObj = this.get(ListFilterUtil.GET_SUM_RESULT_KEY + fieldName);
        if(reObj==null){
            throw new RuntimeException("This peroperty :[" + fieldName + "] is not exist.");
        }

        return reObj;
    }

    public List getFilterMatchList(){
        return (ArrayList)this.get(ListFilterUtil.GET_FILTER_MATCH_LIST_KEY);
    }

    public List getFilterUnMatchList(){
        return (ArrayList)this.get(ListFilterUtil.GET_FILTER_UN_MATCH_LIST_KEY);
    }
}
