package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Report;
import models.validators.ReportValidator;

//日報テーブルの操作に関わる処理を行うクラス
public class ReportService extends ServiceBase {

    //作成した日報データを一覧画面に表示する分取得、リストで返却
    public List<ReportView> getMinePerPage(EmployeeView employee, int page) {

        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL_MINE, Report.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();

        return ReportConverter.toViewList(reports);
    }

    //作成した日報データの件数を取得、返却
    public long countAllMine(EmployeeView employee) {

        long count = (long) em.createNamedQuery(JpaConst.Q_REP_COUNT_ALL_MINE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .getSingleResult();

        return count;
    }

    //一覧画面に表示する日報データを取得、返却
    public List<ReportView> getAllPerPage(int page) {

        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL, Report.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReportConverter.toViewList(reports);
    }

    //日報テーブルのデータ件数を取得、返却
    public long countAll() {

        long reports_count = (long) em.createNamedQuery(JpaConst.Q_REP_COUNT, Long.class)
                .getSingleResult();

        return reports_count;
    }

    //idを条件に取得したデータをReportViewのインスタンスで返却
    public ReportView findOne(int id) {
        return ReportConverter.toView(findOneInternal(id));
    }

    //画面から入力された日報の登録内容を元にデータを1件作成、日報テーブルに登録
    public List<String> create(ReportView rv){

        List<String> errors = ReportValidator.validate(rv);

        if (errors.size() == 0) {

            LocalDateTime ldt = LocalDateTime.now();
            rv.setCreatedAt(ldt);
            rv.setUpdatedAt(ldt);
            createInternal(rv);
        }

        return errors;
    }

    //画面から入力された日報の登録内容を元に、日報データを更新
    public List<String> update(ReportView rv){

        List<String> errors = ReportValidator.validate(rv);

        if (errors.size() == 0) {

            LocalDateTime ldt = LocalDateTime.now();
            rv.setUpdatedAt(ldt);

            updateInternal(rv);
        }

        return errors;
    }

    //日報の承認
    public void approval(Integer id) {

        ReportView savedReport = findOne(id);

        savedReport.setApprovalFlag(JpaConst.REP_APPROVAL_OK);

        update(savedReport);
    }


    //idを条件にデータを1件取得
    private Report findOneInternal(int id) {
        return em.find(Report.class, id);
    }

    //日報データを1件取得
    private void createInternal(ReportView rv) {

        em.getTransaction().begin();
        em.persist(ReportConverter.toModel(rv));
        em.getTransaction().commit();

    }

    //日報データ更新
    private void updateInternal(ReportView rv) {

        em.getTransaction().begin();
        Report r = findOneInternal(rv.getId());
        ReportConverter.copyViewToModel(r, rv);
        em.getTransaction().commit();

    }
}
