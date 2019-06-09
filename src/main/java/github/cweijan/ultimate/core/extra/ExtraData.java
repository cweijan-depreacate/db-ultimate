package github.cweijan.ultimate.core.extra;

import github.cweijan.ultimate.annotation.Column;
import github.cweijan.ultimate.annotation.Primary;
import github.cweijan.ultimate.annotation.Table;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;

@Table("ultimate_extra_data")
public class ExtraData {

    @Primary
    private Integer id;
    private String extraKey;
    @Column(nullable = true)
    private byte[] data;
    private String typeName;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Integer exprieMinute;

    public Integer getExprieMinute() {
        return exprieMinute;
    }

    public void setExprieMinute(Integer exprieMinute) {
        this.exprieMinute = exprieMinute;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

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
