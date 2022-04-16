package models.validators;

import java.util.ArrayList;
import java.util.List;

import actions.views.ReportView;
import constants.MessageConst;

//日報インスタンスに設定されている値のバリデーションを行うクラス
public class ReportValidator {

    //日報インスタンスバリデーション
    public static List<String> validate(ReportView rv){

        List<String> errors = new ArrayList<String>();

        String titleError = validateTitle(rv.getTitle());
        if (!titleError.equals("")) {
            errors.add(titleError);
        }

        String contentError = validateContent(rv.getContent());
        if (!contentError.equals("")) {
            errors.add(contentError);
        }

        return errors;
    }

    //タイトル入力値チェック
    private static String validateTitle(String title) {

        if (title == null || title.equals("")) {
            return MessageConst.E_NOTITLE.getMessage();
        }

        return "";
    }

    //内容入力値チェック
    private static String validateContent(String content) {

        if (content == null || content.equals("")) {
            return MessageConst.E_NOCONTENT.getMessage();
        }

        return "";
    }
}
