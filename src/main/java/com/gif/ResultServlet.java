package com.gif;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/result")
public class ResultServlet extends BaseServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String id = req.getParameter("id");
		String delay = req.getParameter("delay");
		ServletContext sc = getServletContext();
		String path = sc.getRealPath(id);
		File dir = new File(path);
		File[] files = dir.listFiles();
		List<String> URLs = new ArrayList<>();
		
		for(int i = 0; i < files.length;i++){
			URLs.add("image/file.gif?id="+i+"&dir="+id);
		}
		
		req.setAttribute("id", id);
		req.setAttribute("delay", delay);
		req.setAttribute("URLs", URLs);		
		fowardJsp(req, resp, "/WEB-INF/result.jsp");	
	}
		
}
