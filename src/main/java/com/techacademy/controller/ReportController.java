package com.techacademy.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
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

    // 日報一覧画面
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

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }


    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        Employee employee = userDetail.getEmployee();
        report.setEmployee(employee);
        model.addAttribute("report", report);

        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        if(res.hasErrors()) {
            return create(report, model, userDetail);
        }

        LocalDate inputDate = report.getReportDate();
        Employee employee = userDetail.getEmployee();
        report.setEmployee(employee);


        // 業務チェック
        Optional<Report> existingReport = reportService.findByEmployeeAndDate(employee.getCode(), inputDate);
        if (existingReport.isPresent()) {
            model.addAttribute("errorMessage", "既に登録されている日付です");
            return create(report, model, userDetail); // 新規登録画面に戻る
        }

        try {
            reportService.save(report);
        }catch(Exception e){
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(report, model, userDetail);

        }

        return "redirect:/reports"; // 保存成功時は日報一覧画面にリダイレクト
    }

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        reportService.delete(id);


        return "redirect:/reports";
    }

    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    // @PathVariableを使用して、URL{id}部分をメソッドの引数として受け取る
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("report", reportService.findById(id)); // DBから日報情報を取得し、modelオブジェクトにreportという名前で追加
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping(value = "/{id}/update")
    // @Validated アノテーションを使用して入力値の検証を行う。結果はBindingResult res に格納される。
    public String post(@PathVariable("id") Integer id, @Validated Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {

        // 入力チェック
        if (res.hasErrors()) {
            model.addAttribute("report", report);
            return "reports/update";
        }

        // 業務チェック
        Optional<Report> existingReport = reportService.findByEmployeeAndDate(userDetail.getEmployee().getCode(), report.getReportDate());
        if (existingReport.isPresent() && !existingReport.get().getId().equals(report.getId())) {
            model.addAttribute("errorMessage", "既に登録されている日付です");
            return edit(id, model); // 重複エラーがあれば更新画面に戻る
        }



        reportService.update(report);

        return "redirect:/reports";

    }


}
