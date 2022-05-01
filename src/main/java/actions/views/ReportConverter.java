package actions.views;

import java.util.ArrayList;
import java.util.List;

import constants.AttributeConst;
import constants.JpaConst;
import models.Report;

//日報データDTOとViewモデルのコンバートを行うクラス
public class ReportConverter {

    //DTOインスタンス作成
    public static Report toModel(ReportView rv) {
        return new Report(
                rv.getId(),
                EmployeeConverter.toModel(rv.getEmployee()),
                rv.getReportDate(),
                rv.getBegin(),
                rv.getFinish(),
                rv.getTitle(),
                rv.getContent(),
                rv.getCreatedAt(),
                rv.getUpdatedAt(),
                rv.getApprovalFlag() == null
                    ? null
                    : rv.getApprovalFlag() == AttributeConst.REP_APPROVAL_OK.getIntegerValue()
                        ? JpaConst.REP_APPROVAL_OK
                        : JpaConst.REP_APPROVAL_NG);
    }

    //Viewモデルインスタンス作成
    public static ReportView toView(Report r) {

        if (r == null) {
            return null;
        }

        return new ReportView(
                r.getId(),
                EmployeeConverter.toView(r.getEmployee()),
                r.getReportDate(),
                r.getBegin(),
                r.getFinish(),
                r.getTitle(),
                r.getContent(),
                r.getCreatedAt(),
                r.getUpdatedAt(),
                r.getApprovalFlag() == null
                    ? null
                    : r.getApprovalFlag() == JpaConst.REP_APPROVAL_OK
                        ? AttributeConst.REP_APPROVAL_OK.getIntegerValue()
                        : AttributeConst.REP_APPROVAL_NG.getIntegerValue());
    }

    //Viewモデルリスト作成
    public static List<ReportView> toViewList(List<Report> list) {
        List<ReportView> evs = new ArrayList<>();

        for (Report r : list) {
            evs.add(toView(r));
        }

        return evs;
    }

    //Viewモデルフィールド内容をDTOフィールドにコピー
    public static void copyViewToModel(Report r, ReportView rv) {
        r.setId(rv.getId());
        r.setEmployee(EmployeeConverter.toModel(rv.getEmployee()));
        r.setReportDate(rv.getReportDate());
        r.setBegin(rv.getBegin());
        r.setFinish(rv.getFinish());
        r.setTitle(rv.getTitle());
        r.setContent(rv.getContent());
        r.setCreatedAt(rv.getCreatedAt());
        r.setUpdatedAt(rv.getUpdatedAt());
        r.setApprovalFlag(rv.getApprovalFlag());

    }
}
