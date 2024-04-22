package com.techacademy.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    // レポート詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }

    // レポート新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Integer id) {

        return "reports/new";
    }

    // レポート新規登録処理


    // レポート更新画面
    @GetMapping(value = "/{id}/update")
    // @PathVariable String codeを使用して、URL{code}部分をメソッドの引数として受け取る
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("employee", reportService.findById(id)); // DBから従業員情報を取得し、modelオブジェクトにemployeeという名前で追加
        return "reports/update";
    }

    // レポート更新処理


    // レポート削除処理

    // レポート削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        return "redirect:/reports";

    }

}
