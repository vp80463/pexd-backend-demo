package com.a1stream.domain.bo.batch;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SdPSIExportItemXmlBO {

    private HeaderBO header;
    private List<SdPSIExportItemBO> exportItems;
}
