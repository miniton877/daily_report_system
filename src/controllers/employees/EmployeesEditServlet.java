package controllers.employees;

import java.io.IOException;

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
 * Servlet implementation class EmployeesEdigServlet
 */
@WebServlet("/employees/edit")
public class EmployeesEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesEditServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //データベースと接続、indexで選択したidのデータを1件取得
        EntityManager em = DBUtil.createEntityManager();
        Employee e = em.find(Employee.class, Integer.parseInt(request.getParameter("id")));
        em.close();

        //取得したデータをjspに渡す
        request.setAttribute("employee",  e);;
        //セッションIDを取得してjspに渡す
        request.setAttribute("_token",  request.getSession().getId());
        //セッションスコープにemployee_idとしてIDを登録する
        request.getSession().setAttribute("employee_id", e.getId());

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/edit.jsp");
        rd.forward(request,  response);
    }

}
