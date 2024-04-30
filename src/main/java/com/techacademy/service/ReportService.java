package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;


@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }

    // 日報詳細（１件を検索）
    public Report findById(Integer id) {
        // findByIdで検索
        return reportRepository.findById(id).orElse(null);
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;

    }


    // ログイン中の従業員かつ入力した日付の日報データが存在するかチェック
    public Optional<Report> findByEmployeeAndDate(String employeeCode, LocalDate reportDate) {
        return reportRepository.findByEmployeeCodeAndReportDate(employeeCode, reportDate);
    }


    // 日報更新
    @Transactional
    public ErrorKinds update(Report report) {
        Report existingReport = reportRepository.findById(report.getId()).orElse(null);

        // 日付の重複チェックを行う場合


        // フィールド更新
        existingReport.setReportDate(report.getReportDate());
        existingReport.setEmployee(report.getEmployee());
        existingReport.setTitle(report.getTitle());
        existingReport.setContent(report.getContent());
        existingReport.setUpdatedAt(LocalDateTime.now());  // 更新日時を現在の日時に設定

        reportRepository.save(existingReport);  // 更新した日報情報をDBに保存

        return ErrorKinds.SUCCESS;  // 成功した場合に返す
    }



    // 日報削除
    @Transactional
    public void delete(Integer id) {

        Report report = findById(id);
        reportRepository.deleteById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

    }


}
