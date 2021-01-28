package models.validators;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import models.Employee;
import utils.DBUtil;

public class EmployeeValidator {
    public static List<String> validate(Employee e, Boolean codeDuplicateCheckFlag, Boolean passwordCheckFlag) {

        List<String> errors = new ArrayList<String>();
            /*errorsリスト（該当する場合）
             * 社員番号を入力してください：validateCode()
             * 入力された社員番号の情報はすでに存在しています：codeDuplicateCheckFlag()
             * 氏名を入力してください：validateName()
             * パスワードを入力してください：validatePassword()
             */

        String code_error = validateCode(e.getCode(), codeDuplicateCheckFlag);
        if(!code_error.equals("")) {
            errors.add(code_error);
        }

        String name_error = validateName(e.getName());
        if(!name_error.equals("")) {
            errors.add(name_error);
        }

        String password_error = validatePassword(e.getPassword(), passwordCheckFlag);
        if(!password_error.equals("")) {
            errors.add(password_error);
        }

        return errors;
    }

    // 社員番号の空白チェックと、重複チェック
    private static String validateCode(String code, Boolean codeDuplicateCheckFlag) {
        // 必須入力チェック
        if(code == null || code.equals("")) {
            return "社員番号を入力してください。";
        }

        /* すでに登録されている社員番号との重複チェック、codeDuplicateCheckFlagがtrueの時に実行する
         * updateServletでは重複チェックが不要のため：codeDuplicateCheckFlagをfalseにする
         */
        if(codeDuplicateCheckFlag) {  //if()の中にBoolean型の変数
            EntityManager em = DBUtil.createEntityManager();

            //checkResisteredCodeのqueryに入力された社員番号を代入し、データベースで該当する従業員数をlong型で格納
            long employees_count = (long)em.createNamedQuery("checkRegisteredCode", Long.class)
                                           .setParameter("code", code)
                                             .getSingleResult();
            em.close();

            /*employees_countが0より大きい場合、code_errorに文字列代入
             * createのときのみ
             */
            if(employees_count > 0) {
                return "入力された社員番号の情報はすでに存在しています。";
            }
        }//if(codeDuplicateCheckFlag)の終了

        return "";  //codeが入力されcodeDuplicateCheckFlagがfalseのとき、空蘭を返す
    }

    // 社員名の必須入力チェック
    private static String validateName(String name) {
        //入力がない場合name_errorに文字列代入
        if(name == null || name.equals("")) {
            return "氏名を入力してください。";
        }

        return "";
    }

    // パスワードの必須入力チェック、重複チェックはしない
    private static String validatePassword(String password, Boolean passwordCheckFlag) {
        /*passwordCheckFlagがtrueでpassword入力がないとき
         *  createの場合
         *  password_errorに文字列代入
         */

        if(passwordCheckFlag && (password == null || password.equals(""))) {
            return "パスワードを入力してください。";
        }
        return ""; //updateの場合はエラーを出さない、必須入力チェックは不要、passwordCheckFlagがfalse
    }
}