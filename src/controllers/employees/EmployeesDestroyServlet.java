package controllers.employees;

import java.io.IOException;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import utils.DBUtil;

/**
 * Servlet implementation class EmployeesDestroyServlet
 */
@WebServlet("/employees/destroy")
public class EmployeesDestroyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesDestroyServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //formを介してeditで取得したセッションID
        String _token =(String)request.getParameter("_token");
        //セッションIDが一致した場合、データベースに接続
        if(_token != null && _token.equals(request.getSession().getId())){
            EntityManager em = DBUtil.createEntityManager();

            //editで登録したemployee_idで従業員情報を1件情報を取得しオブジェクトに格納
            Employee e = em.find(Employee.class, (Integer)(request.getSession().getAttribute("employee_id")));
            //delete_flagに1をセット
            e.setDelete_flag(1);
            e.setUpdated_at(new Timestamp(System.currentTimeMillis()));

            //データベースの情報更新
            em.getTransaction().begin();
            em.getTransaction().commit();
            em.close();

            //flushメッセージをセッションスコープに登録し、indexに渡す
            request.getSession().setAttribute("flush", "削除が完了しました。");

            //indexにリダイレクト
            response.sendRedirect(request.getContextPath() + "/employees/index");
        }
    }

}
