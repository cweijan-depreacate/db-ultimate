package github.cweijan.ultimate.core.extra;

import github.cweijan.ultimate.annotation.Column;
import github.cweijan.ultimate.annotation.Primary;
import github.cweijan.ultimate.annotation.Table;

@Table("ultimate_extra_data")
public class ExtraData {

    @Primary
    private Integer id;
    private String extraKey;
    @Column(nullable = true)
    private byte[] data;
    private String typeName;

    public String getExtraKey() {
        return extraKey;
    }

    public void setExtraKey(String extraKey) {
        this.extraKey = extraKey;
    }


    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
