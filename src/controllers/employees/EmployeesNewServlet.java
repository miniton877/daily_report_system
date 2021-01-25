package controllers.employees;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;

/**
 * Servlet implementation class EmployeesNewServlet
 */
@WebServlet("/employees/new")
public class EmployeesNewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesNewServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    //CRF対策、セッションIDの取得
        request.setAttribute("_token", request.getSession().getId());
        //初期データ0のエラー回避、空のインスタンスオブジェクト作成
        request.setAttribute("employee", new Employee());
        //JSPを呼び出す
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/new.jsp");
        rd.forward(request, response);

    }

}
