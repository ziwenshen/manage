package com.server.manage.dto.common;

import lombok.Data;

import java.util.List;

/**
 * 分页结果DTO
 */
@Data
public class PageResult<T> {

    /**
     * 数据列表
     */
    private List<T> data;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 总页数
     */
    private Integer totalPages;

    public PageResult() {}

    public PageResult(List<T> data, Long total, Integer page, Integer size) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) total / size);
    }

    public static <T> PageResult<T> of(List<T> data, Long total, Integer page, Integer size) {
        return new PageResult<>(data, total, page, size);
    }
}
