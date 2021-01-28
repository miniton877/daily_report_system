package controllers.reports;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;
import models.validators.ReportValidator;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsUpdateServlet
 */
@WebServlet("/reports/update")
public class ReportsUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsUpdateServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //CRF対策
        //editから渡されたセッションid
        String _token = (String)request.getParameter("_token");
        if(_token != null && _token.equals(request.getSession().getId())){
            EntityManager em = DBUtil.createEntityManager();

            //editから渡されたレポートidでレポート情報を1件検索しクラス型オブジェクトrに代入
            Report r = em.find(Report.class, (Integer)(request.getSession().getAttribute("report_id")));

            //_form.jspのレポート情報をセット
            r.setReport_date(Date.valueOf(request.getParameter("report_date")));
            r.setTitle(request.getParameter("title"));
            r.setContent(request.getParameter("content"));
            r.setUpdated_at(new Timestamp(System.currentTimeMillis()));

            //バリデーション、エラーリストがある場合
            List<String> errors = ReportValidator.validate(r);
            if(errors.size() > 0){
                em.close();
                //セッションid、レポート情報、エラーリストをeditに渡す（戻す）
                request.setAttribute("_token",  request.getSession().getId());
                request.setAttribute("report", r);
                request.setAttribute("errors", errors);
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/edit.jsp");
                rd.forward(request,  response);
            }else{
                //エラーがない場合、データベースを更新する
                em.getTransaction().begin();
                em.getTransaction().commit();
                em.close();

                //flushメッセージをセッションスコープに登録
                request.getSession().setAttribute("flush",  "更新が完了しました。");
                //不要になったレポートidをセッションスコープから削除
                request.getSession().removeAttribute("report_id");

                //indexにリダイレクト
                response.sendRedirect(request.getContextPath() + "/reports/index");
            }
        }
    }

}
