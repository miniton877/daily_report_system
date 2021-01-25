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
    //formを介してeditServletで取得したセッションIDを宣言（_token）
    String _token = (String)request.getParameter("_token");
    //セッションIDが一致した場合、データベースと接続
    if(_token != null && _token.equals(request.getSession().getId())){
        EntityManager em = DBUtil.createEntityManager();

        //editServletでセッションスコープに登録したemployee_idで、データを1件取得
        Employee e = em.find(Employee.class, (Integer)(request.getSession().getAttribute("employee_id")));

        //editで社員番号が変更されていたら、重複チェックを行う????
        Boolean codeDuplicateCheckFlag = true;
        //if(e.getCode() = じゃないの？
        if(e.getCode().equals(request.getParameter("code"))){
            codeDuplicateCheckFlag = false;
            //trueとfalseの意味は？
        }else{
            //重複がない場合？？editから渡されたcodeをセットする
            e.setCode(request.getParameter("code"));
        }

        //editでパスワードが変更されていたら、何してるの？
        Boolean passwordCheckFlag = true;
        //editで入力されたpassword、キャストしないの？？
        String password = request.getParameter("password");
        //passwordが空の場合、変更市内
        if(password == null || password.equals("")){
            passwordCheckFlag = false;
        }else{
            //パスワードのハッシュ化を実行し、セットする
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

        //バリデーションのエラーリストがある場合
        List<String> errors = EmployeeValidator.validate(e,  codeDuplicateCheckFlag,  passwordCheckFlag);
        if(errors.size() > 0){
            em.close();
            request.setAttribute("_token", request.getSession().getId());
            request.setAttribute("employee",  e);
            request.setAttribute("errors",  errors);
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/edit.jsp");
            rd.forward(request,  response);
        }else{
            em.getTransaction().begin();
            em.getTransaction().commit();
            em.close();
            //flushメッセージ、セッションスコープに登録しindexに渡す
            request.getSession().setAttribute("flush",  "更新が完了しました。");
            //不要になった古いemployee_idを削除する
            request.getSession().removeAttribute("employee_id");
            //indexにリダイレクト
            response.sendRedirect(request.getContextPath() + "/employees/index");
        }
    }
    }

}
