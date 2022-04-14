package models.validators;

import java.util.ArrayList;
import java.util.List;

import actions.views.EmployeeView;
import constants.MessageConst;
import services.EmployeeService;

//従業員インスタンスに設定されている値のバリデーションを行うクラス
public class EmployeeValidator {

    //各項目についてバリデーション
    public static List<String> validate(EmployeeService service, EmployeeView ev, Boolean codeDuplicateCheckFlag, Boolean passwordCheckFlag){
        List<String> errors = new ArrayList<String>();

        //社員番号
        String codeError = validateCode(service, ev.getCode(), codeDuplicateCheckFlag);
        if (!codeError.equals("")) {
            errors.add(codeError);
        }

        //氏名
        String nameError = validateName(ev.getName());
        if (!nameError.equals("")) {
            errors.add(nameError);
        }

        //パスワード
        String passError = validatePassword(ev.getPassword(), passwordCheckFlag);
        if (!passError.equals("")) {
            errors.add(passError);
        }

        return errors;

    }

    //社員番号入力チェック、エラーメッセージ返却
    private static String validateCode(EmployeeService service, String code, Boolean codeDuplicateCheckFlag) {

        if (code == null || code.equals("")) {
            return MessageConst.E_NOEMP_CODE.getMessage();
        }

        if (codeDuplicateCheckFlag) {

            long employeesCount = isDuplicateEmployee(service, code);

            if (employeesCount > 0) {
                return MessageConst.E_EMP_CODE_EXIST.getMessage();
            }
        }

        return "";

    }

    private static long isDuplicateEmployee(EmployeeService service, String code) {

        long employeesCount = service.countByCode(code);

        return employeesCount;
    }

    //氏名入力値チェック、エラーメッセージ返却
    private static String validateName(String name) {

        if (name == null || name.equals("")) {
            return MessageConst.E_NONAME.getMessage();
        }

        return "";
    }

    //パスワード入力チェック、エラーメッセージ返却
    private static String validatePassword(String password, Boolean passwordCheckFlag) {

        if (passwordCheckFlag && (password == null || password.equals(""))) {
            return MessageConst.E_NOPASSWORD.getMessage();
        }

        return "";
    }
}
