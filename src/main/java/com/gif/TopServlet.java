package com.gif;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

}

