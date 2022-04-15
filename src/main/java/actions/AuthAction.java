package actions;

import java.io.IOException;

import javax.servlet.ServletException;

import constants.AttributeConst;
import constants.ForwardConst;
import services.EmployeeService;

//認証に関わる処理を行うActionクラス
public class AuthAction extends ActionBase {

    private EmployeeService service;

    //メソッド実行
    @Override
    public void process() throws ServletException, IOException{

        service = new EmployeeService();

        invoke();

        service.close();
    }

    //ログイン画面表示
    public void showLogin() throws ServletException, IOException{

        putRequestScope(AttributeConst.TOKEN, getTokenId());

        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH,flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        forward(ForwardConst.FW_LOGIN);
    }
}