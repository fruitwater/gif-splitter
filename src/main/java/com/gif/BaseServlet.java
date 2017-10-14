package com.gif;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public abstract class BaseServlet extends HttpServlet {

	// 指定したURLのJSPファイルをブラウザに表示する
	protected void fowardJsp(HttpServletRequest req, HttpServletResponse resp, String url)
			throws ServletException, IOException {
		ServletContext context = req.getServletContext();
		context.getRequestDispatcher(url).forward(req, resp);
	}

}
