package controllers.reports;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsIndexServlet
 */
@WebServlet("/reports/index")
public class ReportsIndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsIndexServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        //ページネーション
        int page;  //宣言
        //ページの取得、失敗したら初期値1
        try{
            page = Integer.parseInt(request.getParameter("page"));
        }catch(Exception e){
            page = 1;
        }
        //ページの最初の結果を取得、最大15件、リスト化してreportsに格納する
        List<Report> reports = em.createNamedQuery("getAllReports", Report.class)
                                    .setFirstResult(15 * (page - 1))
                                    .setMaxResults(15)
                                    .getResultList();

                //登録された全件数を取得
                long reports_count = (long)em.createNamedQuery("getReportsCount", Long.class)
                        .getSingleResult();

                em.close();

                //reportsリスト、登録件数、ページ、flushメッセージをjspに渡す
                request.setAttribute("reports", reports);
                request.setAttribute("reports_count",  reports_count);
                request.setAttribute("page",  page);
                if(request.getSession().getAttribute("flush") != null){
                    request.setAttribute("flush",  request.getSession().getAttribute("flush"));
                    request.getSession().removeAttribute("flush");
                }
          RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/index.jsp");
          rd.forward(request,  response);
    }

}
