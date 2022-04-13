package actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constants.AttributeConst;
import constants.ForwardConst;
import constants.PropertyConst;

//各actionクラスの親クラス、共通処理
public abstract class ActionBase {
    protected ServletContext context;
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    //サーブレットコンテキスト、リクエスト、レスポンスをクラスフィールドに設定
    public void init(ServletContext servletContext, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        this.context = servletContext;
        this.request = servletRequest;
        this.response = servletResponse;
    }

    //フロントコントローラから呼び出されるメソッド
    public abstract void process() throws ServletException, IOException;

    //パラメータのcommandの値に該当するメソッド実行
    protected void invoke()throws ServletException, IOException{

        Method commandMethod;

        try {

            String command = request.getParameter(ForwardConst.CMD.getValue());

            commandMethod = this.getClass().getDeclaredMethod(command, new Class[0]);
            commandMethod.invoke(this, new Object[0]);

        }catch(NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException| InvocationTargetException | NullPointerException e) {

            e.printStackTrace();

            forward(ForwardConst.FW_ERR_UNKNOWN);

        }
    }

    //指定されたjsp呼び出し
    protected void forward(ForwardConst target) throws ServletException, IOException{

        String forward = String.format("/WEB-INF/views/%s.jsp", target.getValue());
        RequestDispatcher dispatcher = request.getRequestDispatcher(forward);

        dispatcher.forward(request, response);

    }

    //URL構築、リダイレクト
    protected void redirect(ForwardConst action, ForwardConst command)throws ServletException, IOException{

        String redirectUrl = request.getContextPath() + "/?action=" + action.getValue();
        if (command != null) {
            redirectUrl = redirectUrl + "&command=" + command.getValue();
        }

        response.sendRedirect(redirectUrl);

    }

    //CSRF対策
    protected boolean checkToken() throws ServletException, IOException{

        String _token = getRequestParam(AttributeConst.TOKEN);

        if (_token == null || !(_token.equals(getTokenId()))) {

            forward(ForwardConst.FW_ERR_UNKNOWN);

            return false;

        }else {
            return true;
        }

    }

    //セッションID取得
    protected String getTokenId() {
        return request.getSession().getId();
    }

    //リクエストから要求されているページ数の取得、返却
    protected int getPage() {

        int page;
        page = toNumber(request.getParameter(AttributeConst.PAGE.getValue()));

        if (page == Integer.MIN_VALUE) {

            page = 1;

        }
        return page;
    }

    //文字列を数値に変換
    protected int toNumber(String strNumber) {

        int number = 0;

        try {
            number = Integer.parseInt(strNumber);
        } catch (Exception e) {
            number = Integer.MIN_VALUE;
        }
        return number;
    }

    //文字列をLocalDate型に変換
    protected LocalDate toLocalDate(String strDate) {

        if (strDate == null || strDate.equals("")) {

            return LocalDate.now();

        }
        return LocalDate.parse(strDate);
    }

    //リクエストパラメータから引数で指定したパラメータ値の返却
    protected String getRequestParam(AttributeConst key) {

        return request.getParameter(key.getValue());

    }

    //リクエストスコープにパラメータを設定
    protected <V> void putRequestScope(AttributeConst key, V value) {

        request.setAttribute(key.getValue(), value);

    }

    //セッションスコープから指定されたパラメータ値の取得、変換
    @SuppressWarnings("unchecked")
    protected <R> R getSessionScope(AttributeConst key) {

        return (R) request.getSession().getAttribute(key.getValue());

    }

    //セッションスコープにパラメータ設定
    protected <V> void putSessionScope(AttributeConst key, V value) {

        request.getSession().setAttribute(key.getValue(), value);

    }

    //セッションスコープから指定されたパラメータの除去
    protected void removeSessionScope(AttributeConst key) {

        request.getSession().removeAttribute(key.getValue());

    }

    //アプリケーションスコープから指定されたパラメータ値の取得、変換
    @SuppressWarnings("unchecked")
    protected <R> R getContextScope(PropertyConst key) {

        return (R) context.getAttribute(key.getValue());

    }
}
