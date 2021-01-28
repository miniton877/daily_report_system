package models.validators;

import java.util.ArrayList;
import java.util.List;

import models.Report;

public class ReportValidator {
    //errorsリストを作成（Report型rを引数）
    public static List<String> validate(Report r) {
        List<String> errors = new ArrayList<String>();

        //タイトルの必須入力
        String title_error = _validateTitle(r.getTitle());
        if (!title_error.equals("")) {
            errors.add(title_error);
        }

        //コンテントの必須入力
        String content_error = _validateContent(r.getContent());
        if (!content_error.equals("")) {
            errors.add(content_error);
        }
        return errors;
    }

    //入力がない場合のエラーコメント、title_errorへ
    private static String _validateTitle(String title) {
        if (title == null || title.equals("")) {
            return "タイトルを入力してください。";
        }
        return "";
    }

    //入力がない場合のエラーコメント、content_errorへ
    private static String _validateContent(String content) {
        if (content == null || content.equals("")) {
            return "内容を入力してください。";
        }
        return "";
    }
}