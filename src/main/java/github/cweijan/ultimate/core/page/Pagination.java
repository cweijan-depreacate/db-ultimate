package github.cweijan.ultimate.core.page;

import java.util.List;

/**
 * @author cweijan
 * @version 2019/7/10/010 9:58
 */
public class Pagination<T> {

    /**
     * 设置起始页，记录页面从哪里开始，可选属性
     */
    private Integer startPage;
    private Integer currentPage;
    private Integer pageSize;
    private Integer count;
    private Integer totalPage;
    private List<T> data;

    /**
     * 修改当前起始页数，默认为当前页
     *
     * @param number 相当于当前页数之前页数
     */
    public void range(Integer number) {
        if (totalPage == null) return;
        this.startPage = totalPage - number < 0 ? 1 : totalPage - number;
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
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Integer getStartPage() {
        return startPage;
    }

    public void setStartPage(Integer startPage) {
        this.startPage = startPage;
    }

}
