package github.cweijan.ultimate.core.page;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询对象
 * @author cweijan
 * @version 2019/7/10/010 9:58
 */
public class Pagination<T> implements Serializable {

    /**
     * 设置起始页，记录页面从哪里开始，可选属性
     */
    private Integer startPage;
    /**
     * 设置分页从哪里结束
     */
    private Integer endPage;
    private Integer currentPage;
    private Integer pageSize;
    private Integer count;
    private Integer totalPage;
    private List<T> list;

    /**
     * 设置分页的起始页和结束页
     *
     * @param offset  相当于当前页数之前页数
     * @param pageNum 总共显示多少页
     */
    public Pagination<T> range(Integer offset, Integer pageNum) {
        if (currentPage == null) return null;
        this.startPage = currentPage - offset < 0 ? 1 : currentPage - offset;

        if (this.getTotalPage() != null && pageNum != null) {
            int maxPage = pageNum - 1;
            this.endPage = this.startPage + maxPage;
            if (this.endPage > totalPage) {
                this.endPage = totalPage;
            }
        }
        return this;
    }

    public Integer getPrePage() {
        if (currentPage != null && currentPage != 1) return currentPage - 1;
        return null;
    }

    public Integer getNextPage() {
        if ((currentPage != null && totalPage != null) && currentPage < totalPage) return currentPage + 1;
        return null;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        if (this.pageSize != null) {
            this.totalPage = this.count / this.pageSize;
            if (this.count % this.pageSize != 0) {
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
