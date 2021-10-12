package com.example.demo.model;

import java.util.List;

/**
 * @author zhanghao
 * @date 2021-10-11
 */
public class ValidExcelResult<T> {
    private List<FailMsg> failList;

    private Integer totalCount;

    private Integer successCount;

    private Integer failCount;

    private List<T> successList;

    public List<T> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<T> successList) {
        this.successList = successList;
    }

    public List<FailMsg> getFailList() {
        return failList;
    }

    public void setFailList(List<FailMsg> failList) {
        this.failList = failList;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }


}
