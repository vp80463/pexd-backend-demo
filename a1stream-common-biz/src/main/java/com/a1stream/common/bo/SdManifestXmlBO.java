package com.a1stream.common.bo;
import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Detail")
public class SdManifestXmlBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<SdManifestItemBO> rows;

    @XmlElement(name = "Row")
    public List<SdManifestItemBO> getRows() {
        return rows;
    }

    public void setRows(List<SdManifestItemBO> rows) {
        this.rows = rows;
    }
}
