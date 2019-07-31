package github.cweijan.ultimate.test.lucene;

import github.cweijan.ultimate.core.lucene.type.LuceneDocument;
import github.cweijan.ultimate.core.lucene.type.LuceneField;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author cweijan
 * @version 2019/7/31/031 15:35
 */
@LuceneDocument(primaryKeyField = "id")
public class LuceneObject {

    private Integer id;
    private Double price;
    private Float anotherPrice;
    private Long idNumber;
    private String hello;
    @LuceneField(tokenize = true)
    private String text;
    private Boolean vip;
    private byte[] data;
    private LocalDateTime createTime;
    private Date updateTime;

    public Boolean getVip() {
        return vip;
    }

    public void setVip(Boolean vip) {
        this.vip = vip;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(Long idNumber) {
        this.idNumber = idNumber;
    }

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }



    public Float getAnotherPrice() {
        return anotherPrice;
    }

    public void setAnotherPrice(Float anotherPrice) {
        this.anotherPrice = anotherPrice;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
