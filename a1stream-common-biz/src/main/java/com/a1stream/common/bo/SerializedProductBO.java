package com.a1stream.common.bo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.a1stream.domain.vo.CmmRegistrationDocumentVO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class SerializedProductBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5491753288566195693L;

    private CmmRegistrationDocumentVO cmmRegistrationDocumentVO;

    private List<CmmRegistrationDocumentVO> cmmRegistrationDocumentVOList;
}
