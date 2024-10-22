package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050202RetrieveBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SDM050202HeaderBO header = new SDM050202HeaderBO();
    private List<SDM050202BO> detailList = new ArrayList<>();

}
