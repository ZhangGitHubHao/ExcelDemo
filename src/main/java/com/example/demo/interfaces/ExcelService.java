package com.example.demo.interfaces;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.example.demo.model.ValidExcelResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;

public interface ExcelService {
    /**
     * 接收文件导入excel，并返回验证结果
     * @param file
     * @param pojoClass 对应的数据对象
     * @param function 自实现数据的验证逻辑
     * @param <T> 返回的数据对象
     * @return
     */
    <T> ValidExcelResult<T> importExcel(File file, Class<?> pojoClass, Function<List<?>, ValidExcelResult<T>> function);

    /**
     * 接受文件流导入excel，并返回验证结果
     * @param inputstream
     * @param pojoClass 对应的数据对象
     * @param function 自实现数据的验证逻辑
     * @param <T> 返回的数据对象
     * @return
     */
    <T> ValidExcelResult<T> importExcel(InputStream inputstream, Class<?> pojoClass, Function<List<?>,ValidExcelResult<T>> function);

    /**
     * 接收request导入excel，并返回验证结果
     * @param request
     * @param pojoClass 对应的数据对象
     * @param function 自实现数据的验证逻辑
     * @param <T> 返回的数据对象
     * @return
     */
    <T> ValidExcelResult<T> importExcel(HttpServletRequest request, Class<?> pojoClass, Function<List<?>,ValidExcelResult<T>> function);


    /**
     * 同步导出直接返回文件流
     * @param entity 导出表头定义，传入标题和sheet名称
     * @param pojoClass 对应的数据对象
     * @param dataSet 传入的数据
     * @param request
     * @param response
     */
    void exportExcel(ExportParams entity, Class<?> pojoClass,Collection<?> dataSet,HttpServletRequest request, HttpServletResponse response);

}
