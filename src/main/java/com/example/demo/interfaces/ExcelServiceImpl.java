package com.example.demo.interfaces;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.example.demo.model.ValidExcelResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author zhanghao
 * @date 2021-10-11
 */
@Component
@Slf4j
public class ExcelServiceImpl implements ExcelService {
    @Override
    public <T> ValidExcelResult<T> importExcel(File file, Class<?> pojoClass,
                                               Function<List<?>, ValidExcelResult<T>> function) {
        if(file != null){
            try(FileInputStream fileStream = new FileInputStream(file);) {
                List<?>  list = this.analysisExcel(fileStream, pojoClass);
                ValidExcelResult<T> result = function.apply(list);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public <T> ValidExcelResult<T> importExcel(InputStream inputstream, Class<?> pojoClass,
                                               Function<List<?>, ValidExcelResult<T>> function) {
        if(inputstream != null){
            List<?>	list = this.analysisExcel(inputstream, pojoClass);
            ValidExcelResult<T> result = function.apply(list);
            return result;
        }
        return null;
    }

    @Override
    public  <T> ValidExcelResult<T> importExcel(HttpServletRequest request, Class<?> pojoClass,
                                                Function<List<?>, ValidExcelResult<T>> function) {
        try(InputStream in = getFile(request);) {
            if(in != null){
                List<?> list = this.analysisExcel(in, pojoClass);
                ValidExcelResult<T> result = function.apply(list);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取request中文件的流
     * @param request
     * @return
     */
    private InputStream getFile(HttpServletRequest request){
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        if (multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            for (Iterator<String> iterator = multiRequest.getFileNames(); iterator.hasNext();) {
                String key = iterator.next();
                MultipartFile multipartFile = multiRequest.getFile(key);
                if (multipartFile != null) {
					/*String name = multipartFile.getOriginalFilename();
					String uuid = UUID.randomUUID().toString();
					String postFix = name.substring(name.lastIndexOf(".")).toLowerCase();
					String fileName = uuid + postFix;
					String filePath = System.getProperty("java.io.tmpdir") + File.separator + fileName;
					File file = new File(filePath);
					try {
						multipartFile.transferTo(file);
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}*/
                    try {
                        return multipartFile.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 解析excel数据
     * @param inputstream
     * @param pojoClass
     * @param <T>
     * @return
     */
    private <T> List<T> analysisExcel(InputStream inputstream, Class<?> pojoClass){
        List<T> list = null;
        try {
            ImportParams iparam = new ImportParams();
            iparam.setTitleRows(1);
            list = ExcelImportUtil.importExcel(inputstream, pojoClass, iparam);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(inputstream != null){
                try {
                    inputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * 写入数据到excel
     * @param list
     * @return
     */
    private Workbook writeDataToExcel(List<Map<String,Object>> list) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
//        ExcelExportUtil.closeExportBigExcel();
        return workbook;
    }

    /**
     * 写入数据到excel
     * @param entity
     * @param pojoClass
     * @param dataSet
     * @return
     */
    private Workbook writeDataToExcel(ExportParams entity, Class<?> pojoClass, Collection<?> dataSet) {
        entity.setType(ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(entity, pojoClass, dataSet);
//        ExcelExportUtil.closeExportBigExcel();
        return workbook;
    }

    @SneakyThrows
    @Override
    public void exportExcel(ExportParams entity, Class<?> pojoClass, Collection<?> dataSet, HttpServletRequest request,
                            HttpServletResponse response) {
        int count = dataSet.size();
        if(dataSet != null && count <= 1000){
            try {
                Workbook workbook = writeDataToExcel(entity, pojoClass, dataSet);
                writeExcel(request,response,entity.getTitle()+System.currentTimeMillis()+".xlsx",workbook);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将workbook转换成文件流
     * @param request
     * @param response
     * @param fileName
     * @param workbook
     * @throws Exception
     */
    private void writeExcel(HttpServletRequest request, HttpServletResponse response, String fileName,
                            Workbook workbook) throws Exception {
        response.reset();
        response.setContentType("application/json;charset=UTF-8");
        String filenamedisplay = URLEncoder.encode(fileName, "UTF-8");
        if ("FF".equals(getBrowser(request))) {
            // 针对火狐浏览器处理方式不一样了
            filenamedisplay = new String(fileName.getBytes("UTF-8"),
                    "iso-8859-1");
        }
        response.setHeader("Content-Disposition", "attachment;filename="
                + filenamedisplay);
        OutputStream os = response.getOutputStream();
        workbook.write(os);
        os.close();
    }


    /**
     * 获取浏览器对象
     * @param request
     * @return
     */
    private  String getBrowser(HttpServletRequest request) {
        String UserAgent = request.getHeader("USER-AGENT").toLowerCase();
        if (UserAgent != null) {
            if (UserAgent.indexOf("msie") >= 0)
                return "IE";
            if (UserAgent.indexOf("firefox") >= 0)
                return "FF";
            if (UserAgent.indexOf("safari") >= 0)
                return "SF";
        }
        return null;
    }


}
