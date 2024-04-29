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
        if(existingReport == null) {
            return ErrorKinds.BLANK_ERROR;
        }

        report.setId(report.getId());
        report.setReportDate(report.getReportDate());
        report.setEmployee(report.getEmployee());
        report.setTitle(report.getTitle());
        report.setContent(report.getContent());
        report.setUpdatedAt(LocalDateTime.now());
        report.setCreatedAt(existingReport.getCreatedAt());
        report.setDeleteFlg(false);

        reportRepository.save(report); // 従業員情報をDBに保存
        return ErrorKinds.SUCCESS; // 成功した場合に返す
    }

    // 画面で表示中の従業員かつ入力した日付の日報データが存在する場合エラー



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
