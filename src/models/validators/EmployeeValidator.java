package models.validators;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import models.Employee;
import utils.DBUtil;

public class EmployeeValidator {
    public static List<String> validate(Employee e, Boolean codeDuplicateCheckFlag, Boolean passwordCheckFlag) {
        //errorsリストを作成する
        List<String> errors = new ArrayList<String>();

            //下記で実行するvalidateCode(String , Boolean)の引数をcode_errorに代入
            //Stringは「社員番号を入力してください。」または""
            //Booleanは
        String code_error = validateCode(e.getCode(), codeDuplicateCheckFlag);
            //code_errorに値がある場合、文字列をリストに追加
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

    //バリデーション・社員番号
    //validateCode()を実行し、String型をcode_errorに戻す
    private static String validateCode(String code, Boolean codeDuplicateCheckFlag) {
        // codeが空の場合、code_errorに「社員番号を入力してください」を戻す
        if(code == null || code.equals("")) {
            return "社員番号を入力してください。";
        }

        /*すでに登録されている社員番号との重複チェック、trueの場合
         * パスワードの入力値チェックと社員番号の重複チェックを行う
         * 後ほど作成するコントローラの方でフォームの入力状態を確認し、バリデーションを実行する・しないを決めます。
         */

        if(codeDuplicateCheckFlag) {
           //データベースに接続し、登録code件数をlong型で取得
            //パラメータにcodeを登録？？？
            EntityManager em = DBUtil.createEntityManager();
            long employees_count = (long)em.createNamedQuery("checkResisteredCode", Long.class)
                    .setParameter("code", code)
                    .getSingleResult();
            em.close();
            //employees_countが空の場合、？？
            if(employees_count > 0){
                return "入力された社員番号の情報はすでに存在しています。";
            } //if終了

            } //if(codeDuplicateCheckFlag)終了

        return "";
    }//validateCode（）終了

    // バリデーション・社員名
    private static String validateName(String name) {
        if(name == null || name.equals("")) {
            return "氏名を入力してください。";
        }

        return "";
    }

  //バリデーション・パスワード
    //validaPassword()を実行し、String型をpassword_errorに戻す
    private static String validatePassword(String password, Boolean passwordCheckFlag){
 // passwordが空かつpasswordCheckFlagがtrue場合、password_errorに「パスワードを入力してください」を戻す
        if(passwordCheckFlag && (password == null || password.equals(""))){
            return "パスワードを入力してください。";
        }
        return"";
}
}