package github.cweijan.ultimate.core.page;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果对象
 *
 * @author cweijan
 * @version 2019/7/10/010 9:58
 */
public class Pagination<T> implements Serializable {

    /**
     * 每页数量
     */
    private Integer pageSize;
    /**
     * 数据总量
     */
    private Integer total;
    /**
     * 当前页码
     */
    private Integer current;
    /**
     * 页面总数
     */
    private Integer totalPage;
    /**
     * 数据列表
     */
    private List<T> list;
    /**
     * 分页起始页，前端工具属性，可选
     */
    private Integer startPage;
    /**
     * 分页结束页，前端工具属性，可选
     */
    private Integer endPage;

    /**
     * 设置分页的起始页和结束页
     *
     * @param offset  相当于当前页数之前页数
     * @param pageNum 总共显示多少页
     */
    public Pagination<T> range(Integer offset, Integer pageNum) {
        if (current == null) return null;
        this.startPage = current - offset < 0 ? 1 : current - offset;

        if (this.getTotalPage() != null && pageNum != null) {
            int maxPage = pageNum - 1;
            this.endPage = this.startPage + maxPage;
            if (this.endPage > totalPage) {
                this.endPage = totalPage;
            }
        }
        return this;
    }

    /**
     * 获取上一页页码,如果当前是第一页,则返回空
     */
    public Integer getPrePage() {
        if (current != null && current != 1) return current - 1;
        return null;
    }

    /**
     * 获取下一页页码,如果当前是最后一页,则返回空
     */
    public Integer getNextPage() {
        if ((current != null && totalPage != null) && current < totalPage) return current + 1;
        return null;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        if (this.pageSize != null && total!=null) {
            this.totalPage = this.total / this.pageSize;
            if (this.total % this.pageSize != 0) {
                this.totalPage++;
            }
        } else this.totalPage = 1;

        return this.totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getStartPage() {
        return startPage;
    }

    public void setStartPage(Integer startPage) {
        this.startPage = startPage;
    }

    public Integer getEndPage() {
        return endPage;
    }

    public void setEndPage(Integer endPage) {
        this.endPage = endPage;
    }
}
