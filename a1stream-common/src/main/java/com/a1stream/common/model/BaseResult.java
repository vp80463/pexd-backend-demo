package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BaseResult implements Serializable {

    private static final long serialVersionUID = -6854950124996500495L;

    public String message;

    public String[] messageParam;

    public Object data;

    public Object otherProperty;

    public Map<String,List<String>> logMap;

    public Object notifyModel;

    public final static Integer SUCCESS = 200;

    public final static Integer WARNING = 300;

    public final static Integer ERROR = 400;

    public final static String SUCCESS_MESSAGE = "Operate Success!";
    public final static String ERROR_MESSAGE = "Operate Failure";
    public final static String UPDATE_SUCCESS_MESSAGE = "Update Success!";
    public final static String DELETE_SUCCESS_MESSAGE = "Delete Success!";
    public final static String FILE_ERROR_MESSAGE = "File save failed, please contact the administrator!";
    public final static String READ_FILE_ERROR_MESSAGE = "Could not read the file, please contact the administrator!";
    public final static String GET_FILE_ERROR_MESSAGE = "Could not get the file, please contact the administrator!";
    public final static String REQUEST_ERROR_MESSAGE = "The request parameters are incorrect, please contact the administrator!";

    public BaseResult() {}

    public BaseResult(Object data) {
        this.data = data;
    }
}
