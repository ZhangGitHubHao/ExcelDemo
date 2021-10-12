package com.example.demo.demo;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.example.demo.interfaces.ExcelService;
import com.example.demo.model.ValidExcelResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * @author zhanghao
 * @date 2021-10-11
 */
@Component
public class ExcelTestDemo {
    @Autowired
    private ExcelService excelService;

    /**
     * 实时导出
     * @param request
     * @param response
     */
    public void testExport(HttpServletRequest request, HttpServletResponse response) {
        List<ExcelEntityDemo> list = new ArrayList<ExcelEntityDemo>();
        ExportParams params = new ExportParams("大数据测试", "测试");
        for (int i = 0; i < 50; i++) {
            ExcelEntityDemo client = new ExcelEntityDemo();
            client.setBirthday(new Date());
            client.setClientName("小明" + i);
            client.setClientPhone("18797" + i);
            client.setCreateBy("yanss");
            client.setId("1" + i);
            client.setRemark("测试" + i);
            list.add(client);
        }
        this.excelService.exportExcel(params, ExcelEntityDemo.class, list,request,response);
    }

    /**
     * 导入
     * @param request
     * @param response
     */
    public void testImport(HttpServletRequest request, HttpServletResponse response) {
        Function<List<?>, ValidExcelResult<ExcelEntityDemo>> function = list -> {
            ValidExcelResult<ExcelEntityDemo> er = new ValidExcelResult<ExcelEntityDemo>();
            List<ExcelEntityDemo> successList = new ArrayList<ExcelEntityDemo>();
            for(Object msg : list){
                successList.add((ExcelEntityDemo)msg);
            }
            er.setSuccessList(successList);
            return er;
        };
        ValidExcelResult<ExcelEntityDemo> result= this.excelService.importExcel(request, ExcelEntityDemo.class, function);
    }
}
