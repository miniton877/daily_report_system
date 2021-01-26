package controllers.employees;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import utils.DBUtil;

/**
 * Servlet implementation class EmployeesIndexServlet
 */
@WebServlet("/employees/index")
public class EmployeesIndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesIndexServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //データベースと接続
        EntityManager em = DBUtil.createEntityManager();

        //ページネーション
        //pageの取得
        int page = 1; //初期設定
        try {
            //getParameter()でリクエストパラメータpageの値を取得
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
        }
        //Employeeクラス型リストを作成、15件分
        List<Employee> employees = em.createNamedQuery("getAllEmployees", Employee.class)
                .setFirstResult(15 * (page - 1)) //ページの最初のデータを決める　0, 15, 30,,,
                .setMaxResults(15) //最初のデータから15件取得
                .getResultList(); //15件分をリスト化する
        //全登録件数をlong型で取得
        long employees_count = (long) em.createNamedQuery("getEmployeesCount", Long.class)
                .getSingleResult();
        em.close();
        //リクエストスコープで値を渡す
        request.setAttribute("employees", employees); //15件分のリスト
        request.setAttribute("employees_count", employees_count); //全登録件数
        request.setAttribute("page", page); //page

        /*flushメッセージ
         * create, update, destroy実行時に、セッションスコープでflushを受け取ったら、
         * リクエストスコープに渡してセッションスコープから削除する
         */

        if (request.getSession().getAttribute("flush") != null) {
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }
        //viewの呼び出し
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/index.jsp");
        rd.forward(request, response);
    }

}
