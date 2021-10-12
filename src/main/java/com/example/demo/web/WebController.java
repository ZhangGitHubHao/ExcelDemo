package com.example.demo.web;

import com.example.demo.demo.ExcelTestDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhanghao
 * @date 2021-10-12
 */
@RestController
@RequestMapping(value = "demo")
public class WebController {

    @Autowired
    ExcelTestDemo excelTestDemo;

    @GetMapping(value = "export")
    public void demo(HttpServletRequest request, HttpServletResponse response){
        excelTestDemo.testExport(request,response);
    }
}
