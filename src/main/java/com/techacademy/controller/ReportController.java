package com.techacademy.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Employee.Role;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // レポート一覧画面
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail ) {
        // userDetailから情報をとってくる
        Employee employee = userDetail.getEmployee();
        Role role = employee.getRole();

        if (role.equals(Role.ADMIN)) {
            model.addAttribute("listSize", reportService.findAll().size());
            model.addAttribute("reportList", reportService.findAll());
        }else {
            List<Report> reports = reportService.findByEmployee(employee);
            model.addAttribute("listSize", reports.size());
            model.addAttribute("reportList", reports);
        }
        return "reports/list";

    }

}
