package com.a1stream.domain.bo.master;

import java.util.List;

import com.a1stream.domain.vo.CmmPersonVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM020202BO extends CmmPersonVO {

    private static final long serialVersionUID = 1L;

    private List<CMM020202GridBO> pointList; // 自定义List接收传入参数
}