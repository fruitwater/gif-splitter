package com.gif;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/image/*")
public class ImageServlet extends BaseServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String dir = req.getParameter("dir");
		int id = Integer.parseInt(req.getParameter("id"));	
		ServletContext sc = getServletContext();		
		String pathString = sc.getRealPath(dir+"/file"+id+".gif");
		Path path = Paths.get(pathString);
		byte[] bytes = Files.readAllBytes(path);
		OutputStream os = resp.getOutputStream();
		resp.setContentType("image/gif");
		os.write(bytes);
	}
	
}
