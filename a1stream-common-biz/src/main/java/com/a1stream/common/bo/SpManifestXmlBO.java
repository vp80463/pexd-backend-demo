package com.a1stream.common.bo;
import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Detail")
public class SpManifestXmlBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<SpManifestItemBO> rows;

    @XmlElement(name = "Row")
    public List<SpManifestItemBO> getRows() {
        return rows;
    }

    public void setRows(List<SpManifestItemBO> rows) {
        this.rows = rows;
    }
}
