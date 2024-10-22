package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ010702BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SDQ010702HeaderBO header = new SDQ010702HeaderBO();
    private List<SDQ010702DetailBO> detailList = new ArrayList<>();

}
