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
 * Servlet implementation class EmployeesCreateServlet
 */
@WebServlet("/employees/create")
public class EmployeesCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesCreateServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    //CRF対策、NewServletで取得したセッションIDをフォームを介して受け取る
        String _token = (String)request.getParameter("_token");
        //セッションIDが一致した場合
        if(_token != null && _token.equals(request.getSession().getId())){
            //データベースと接続し、DTOをインスタンス化
            EntityManager em = DBUtil.createEntityManager();
            Employee e = new Employee();
            //ブラウザの入力内容をセット
            e.setCode(request.getParameter("code"));
            e.setName(request.getParameter("name"));
            //パスワードのハッシュ化メソッドの実行、アプリケーションスコープにpepperとして登録
            e.setPassword(
                    EncryptUtil.getPasswordEncrypt(
                            request.getParameter("password"),
                                (String)this.getServletContext().getAttribute("pepper")
                                )
                    );
            //アドミフラグを取得し、Integer型にキャスト、0 or 1
            e.setAdmin_flag(Integer.parseInt(request.getParameter("admin_flag")));

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            e.setCreated_at(currentTime);
            e.setUpdated_at(currentTime);
            e.setDelete_flag(0);//デリートフラグ初期値セット

            //バリデーションのエラーをリスト化
            /*引数、Employee e, Boolean codeDuplicateCheckFlag, Boolean passwordCheckFlag
             * 社員番号を入力してください：validateCode(空)
             * 入力された社員番号の情報はすでに存在しています：codeDuplicateCheckFlag(true)
             * 氏名を入力してください：validateName(空)
             * パスワードを入力してください：validatePassword(true)
             */
            List<String> errors = EmployeeValidator.validate(e,  true,  true);

            /*エラーがある場合、データベースを切断し、セッションID、オブジェクトe、エラーリストをnew.jspからformへ
             * 新規登録ページに、エラー表示、入力内容、（_token）が表示される。
             */
            if(errors.size() > 0){
                em.close();

                request.setAttribute("_token",  request.getSession().getId());
                request.setAttribute("employee",  e);
                request.setAttribute("errors",  errors);;
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/new.jsp");
                rd.forward(request,  response);
            }else{
                //エラーがない場合、データベースに値を登録
                //flushメッセージをセッションスコープに登録しindexへ渡す
                em.getTransaction().begin();
                em.persist(e);
                em.getTransaction().commit();
                request.getSession().setAttribute("flush", "登録が完了しました。");
                em.close();

                response.sendRedirect(request.getContextPath() + "/employees/index");
            }
        }
    }

}
