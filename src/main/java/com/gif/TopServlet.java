package com.gif;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


@SuppressWarnings("serial")
@WebServlet("/top")
public class TopServlet extends BaseServlet{

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
		fowardJsp(req, resp, "/WEB-INF/top.jsp");
		
		}catch(Throwable e){

			System.out.println("[ERROR] 標準エラー出力：エラーが発生しました。");
			e.printStackTrace(System.out);
		}
	}
       
        public static void main(String args[]) throws Exception{
System.out.println("hoge is hoge");
try{
	Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
	context.setResourseBase("src/main/webapp");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new TopServlet()),"/*");
        server.start();
        server.join();
}catch(Exception e){
System.out.println("error");
e.printStackTrace(System.out);
}        
	}

}

