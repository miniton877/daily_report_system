package controllers.login;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    //ログイン画面を表示
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //セッションIDをリクエストスコープに登録し、login.jspに渡す
        request.setAttribute("_token", request.getSession().getId());
        //falseをリクエストスコープに登録し、hasErrorと名付ける、login.jspに渡す
        request.setAttribute("hasError",  false);

        //セッションスコープにflushメッセージが登録されていた場合、リクエストスコープに渡し、セッションスコープから削除する
        if(request.getSession().getAttribute("flush") != null){
            request.setAttribute("flush",  request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
        rd.forward(request,  response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    //ログイン処理を実行
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //認証結果を格納する変数の宣言
        Boolean check_result = false;

        //リクエストパラメータのcodeとpasswordを取得
        String code = request.getParameter("code");
        String plain_pass = request.getParameter("password");

        //クラス型オブジェクトeにnullを代入（ログアウト状態）
        Employee e = null;

        //code、passが空欄でない場合、データベースと接続
        if(code != null && !code.equals("") && plain_pass != null && !plain_pass.equals("")){
            EntityManager em = DBUtil.createEntityManager();

            //パスワードをハッシュ化し、文字列にする
            String password = EncryptUtil.getPasswordEncrypt(
                    plain_pass,
                    (String)this.getServletContext().getAttribute("pepper")
                    );

            //入力されたcodeとpasswordが正しいかどうか確認
            try{
                /*checkLoginCodeAndPasswordのクエリに
                 * リクエストパラメータcodeとpasswordを代入
                 * データベースと照合し、該当する従業員情報をオブジェクトに格納
                 */
                e = em.createNamedQuery("checkLoginCodeAndPassword", Employee.class)
                        .setParameter("code",  code)
                        .setParameter("pass",  password)
                        .getSingleResult();
            }catch(NoResultException ex){}
            em.close();

            //データベースに該当する従業員情報がある場合、認証結果の変数はtrue
            if(e != null){
                check_result = true;
            }
        }//catch終了

        if(!check_result){
            /*認証できなかった場合（check_result = false)
             * リクエストスコープにセッションID、hasError=true、入力されたcodeが登録され、login.jspに渡される
             * （hasErrorがtrueの場合、login.jspでエラーが表示される）
             */
            request.setAttribute("_token",  request.getSession().getId());
            request.setAttribute("hasError",  true);
            request.setAttribute("code",  code);
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
            rd.forward(request,  response);
        }else{
          //認証された場合、ログイン状態にしてトップページへリダイレクト
            //login_employeeをセッションスコープに登録　（＝ログイン状態）
            request.getSession().setAttribute("login_employee",  e);
            //flushメッセージをセッションスコープに登録して、TopPageIndexServletへ渡す
            request.getSession().setAttribute("flush",  "ログインしました。");
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

}
