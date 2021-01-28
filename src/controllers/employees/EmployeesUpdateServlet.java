package controllers.employees;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.validators.EmployeeValidator;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class EmployeesUpdateServlet
 */
@WebServlet("/employees/update")
public class EmployeesUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesUpdateServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    //formを介してeditで取得したセッションID
    String _token = (String)request.getParameter("_token");
    //セッションIDが一致した場合、データベースと接続
    if(_token != null && _token.equals(request.getSession().getId())){
        EntityManager em = DBUtil.createEntityManager();

        //editでセッションスコープに登録したemployee_idで、従業員情報を1件取得、クラス型オブジェクトに格納
        Employee e = em.find(Employee.class, (Integer)(request.getSession().getAttribute("employee_id")));

        //editで社員番号が入力された場合の重複チェックについて
        Boolean codeDuplicateCheckFlag = true; //宣言
        //既に保存済みのcode(e.getCode())、とeditで入力されたcode(getParameter("code"))が一致した場合、重複チェックを行わない
        if(e.getCode().equals(request.getParameter("code"))){
            codeDuplicateCheckFlag = false;
        }else{
            //editで入力されたcodeが新規の場合、そのcodeをセットする
            e.setCode(request.getParameter("code"));
        }

        //editで入力されたパスワードについて
        Boolean passwordCheckFlag = true; //宣言
        String password = request.getParameter("password");
        //editでpasswordの入力がない場合、必須入力をチェックしない
        if(password == null || password.equals("")){
            passwordCheckFlag = false;
        }else{
            /*editでpasswordが入力された場合
             * パスワードのハッシュ化を実行し、セットする
             */
            e.setPassword(
                    EncryptUtil.getPasswordEncrypt(
                            password,
                            (String)this.getServletContext().getAttribute("pepper")
                            )
                    );
        }
        e.setName(request.getParameter("name"));
        e.setAdmin_flag(Integer.parseInt(request.getParameter("admin_flag")));
        e.setUpdated_at(new Timestamp(System.currentTimeMillis()));
        e.setDelete_flag(0);

      //バリデーション
        /*EmployeeValidator.validate()の引数、Employee e, Boolean codeDuplicateCheckFlag, Boolean passwordCheckFlag
         * エラーがある場合：表示内容
         * validateCode(空蘭)：社員番号を入力してください
         * validateName(空蘭)：氏名を入力してください
         */
        List<String> errors = EmployeeValidator.validate(e,  codeDuplicateCheckFlag,  passwordCheckFlag);

      //エラーがある場合、データベースを切断
        if(errors.size() > 0){
            em.close();

            //セッションID、入力情報、エラーリストをedit.jspに渡す（戻す）
            request.setAttribute("_token", request.getSession().getId());
            request.setAttribute("employee",  e);
            request.setAttribute("errors",  errors);
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/edit.jsp");
            rd.forward(request,  response);
        }else{
            //エラーがない場合、データベースの情報を更新
            em.getTransaction().begin();
            em.getTransaction().commit();
            em.close();

            //flushメッセージをセッションスコープに登録しindexに渡す
            request.getSession().setAttribute("flush",  "更新が完了しました。");
            //不要になった古いemployee_idをセッションスコープから削除する
            request.getSession().removeAttribute("employee_id");
            //indexにリダイレクト
            response.sendRedirect(request.getContextPath() + "/employees/index");
        }
    }
    }

}
