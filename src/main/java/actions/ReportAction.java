package actions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import services.ReportService;

//日報に関する処理を行うActionクラス
public class ReportAction extends ActionBase {

    private ReportService service;

    //メソッド実行
    @Override
    public void process() throws ServletException, IOException{

        service = new ReportService();

        invoke();
        service.close();
    }

    //一覧画面表示
    public void index() throws ServletException, IOException{

        int page = getPage();
        List<ReportView> reports = service.getAllPerPage(page);

        long reportsCount = service.countAll();

        putRequestScope(AttributeConst.REPORTS, reports);
        putRequestScope(AttributeConst.REP_COUNT, reportsCount);
        putRequestScope(AttributeConst.PAGE, page);
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE);

        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        forward(ForwardConst.FW_REP_INDEX);
    }

    //新規登録画面表示
    public void entryNew() throws ServletException, IOException{

        putRequestScope(AttributeConst.TOKEN, getTokenId());

        ReportView rv = new ReportView();
        rv.setReportDate(LocalDate.now());
        putRequestScope(AttributeConst.REPORT, rv);

        forward(ForwardConst.FW_REP_NEW);
    }

    //新規登録
    public void create() throws ServletException, IOException{

        if (checkToken()) {

            LocalDate day = null;

            if (getRequestParam(AttributeConst.REP_DATE) == null
                    || getRequestParam(AttributeConst.REP_DATE).equals("")) {
                day = LocalDate.now();
            } else {
                day = LocalDate.parse(getRequestParam(AttributeConst.REP_DATE));
            }

            EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

            ReportView rv = new ReportView(
                    null,
                    ev,
                    day,
                    getRequestParam(AttributeConst.REP_BEGIN),
                    getRequestParam(AttributeConst.REP_FINISH),
                    getRequestParam(AttributeConst.REP_TITLE),
                    getRequestParam(AttributeConst.REP_CONTENT),
                    null,
                    null,
                    AttributeConst.REP_APPROVAL_NG.getIntegerValue());

            List<String> errors = service.create(rv);

            if (errors.size() > 0) {

                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.REPORT, rv);
                putRequestScope(AttributeConst.ERR, errors);

                forward(ForwardConst.FW_REP_NEW);
            }else {

                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

                redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
            }
        }
    }

    //詳細画面表示
    public void show() throws ServletException, IOException{

        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

        if (rv == null) {

            forward(ForwardConst.FW_ERR_UNKNOWN);
        }else {

            putRequestScope(AttributeConst.REPORT, rv);
            putRequestScope(AttributeConst.TOKEN, getTokenId());

            forward(ForwardConst.FW_REP_SHOW);
        }
    }

    //編集画面表示
    public void edit() throws ServletException, IOException{

        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        if (rv == null || ev.getId() != rv.getEmployee().getId()) {

            forward(ForwardConst.FW_ERR_UNKNOWN);
        }else {

            putRequestScope(AttributeConst.TOKEN, getTokenId());
            putRequestScope(AttributeConst.REPORT, rv);

            forward(ForwardConst.FW_REP_EDIT);
        }
    }

    //更新
    public void update() throws ServletException, IOException{

        if (checkToken()) {

            ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

            rv.setReportDate(toLocalDate(getRequestParam(AttributeConst.REP_DATE)));
            rv.setTitle(getRequestParam(AttributeConst.REP_TITLE));
            rv.setContent(getRequestParam(AttributeConst.REP_CONTENT));
            rv.setBegin(getRequestParam(AttributeConst.REP_BEGIN));
            rv.setFinish(getRequestParam(AttributeConst.REP_FINISH));

            List<String> errors = service.update(rv);

            if (errors.size() > 0) {

                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.REPORT, rv);
                putRequestScope(AttributeConst.REPORT, rv);

                forward(ForwardConst.FW_REP_EDIT);
            }else {

                putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());

                redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
            }
        }
    }

    //承認
    public void approval() throws ServletException, IOException {

        if(checkToken()) {

            service.approval(toNumber(getRequestParam(AttributeConst.REP_ID)));

            putSessionScope(AttributeConst.FLUSH, MessageConst.I_APPROVALED.getMessage());

            redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
        }
    }
}