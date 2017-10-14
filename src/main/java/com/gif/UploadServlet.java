package com.gif;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
@WebServlet("/upload")
public class UploadServlet extends BaseServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		FileItem item = getFileItem(req);

		if (item == null) {
			throwErrorException(resp, "ファイルアイテムが存在しないかファイルサイズが大きすぎます");
		}
		BufferedImage image = ImageIO.read(item.getInputStream());
		// もし画像データがアップロードされたものではなかった場合
		if (image == null) {
			throwErrorException(resp, "アップロードされたもファイルは画像ではありません");
		}
		Map<String, Object> properties = getProperties(item);
		checkAvailableEx((String) properties.get("ex"), resp);
		//ディレクトリを作成する
		makeDir(properties,resp);
		// ファイルを作成する
		makeFile(properties, resp);
		// 結果を表示するサーブレットにリダイレクトする
		resp.sendRedirect(getServletContext().getContextPath()+"/result?id="+properties.get("id")+"&delay="+properties.get("delay"));
	}

	// ユーザがアップロードしたファイルを取得する
	// 存在しなければnullを返す
	private FileItem getFileItem(HttpServletRequest req) {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(5 * 1024 * 1024);

		try {
			List<FileItem> list = upload.parseRequest(req);
			Iterator<FileItem> iterator = list.iterator();
			if (!iterator.hasNext()) {
				return null;
			}
			return iterator.next();
		} catch (FileUploadException e) {
			return null;
		}
	}

	// 拡張子がgifであるかどうかチェックする
	private void checkAvailableEx(String arg, HttpServletResponse resp) throws UploadException {
		if (arg == null || !"gif".equals(arg.toLowerCase())) {
			throwErrorException(resp, "拡張子がgifではありません");
		}
	}

	// 拡張子を取得する
	private String getEx(FileItem item) {
		String fileName = item.getName();

		if (fileName == null) {
			return "";
		}
		int i = fileName.lastIndexOf(".");

		if (i != -1) {
			return fileName.substring(i + 1);
		}
		return fileName;
	}

	// gifファイルのフレーム数、アニメーションの間隔時間、拡張子をマップにして格納する
	private Map<String, Object> getProperties(FileItem item) throws IOException {
		String ex = getEx(item);
		int frame = 1;
		int delay = -1;

		if (ex.equals("gif")) {
			ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(readAll(item)));
			ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
			reader.setInput(iis);
			frame = reader.getNumImages(true);
			// stackoverflowを参考：https://stackoverflow.com/questions/20077913/read-delay-between-frames-in-animated-gif
			IIOMetadata meta = reader.getImageMetadata(0);
			String format = meta.getNativeMetadataFormatName();
			IIOMetadataNode rootNode = (IIOMetadataNode) meta.getAsTree(format);

			Supplier<IIOMetadataNode> supplier = new Supplier<IIOMetadataNode>() {

				@Override
				public IIOMetadataNode get() {
					for (int i = 0; i < rootNode.getLength(); i++) {
						if (rootNode.item(i).getNodeName().compareToIgnoreCase("GraphicControlExtension") == 0) {
							return (IIOMetadataNode) rootNode.item(i);
						}
					}
					IIOMetadataNode node = new IIOMetadataNode(format);
					rootNode.appendChild(node);
					return node;
				}
			};
			IIOMetadataNode graphicsNode = supplier.get();
			delay = Integer.valueOf(graphicsNode.getAttribute("delayTime"));
		}

		byte[] data = readAll(item);
		BufferedImage image = ImageIO.read(item.getInputStream());

		Map<String, Object> map = new HashMap<>();
		map.put("frame", frame);
		map.put("delay", delay);
		map.put("ex", ex);
		map.put("data", data);
		map.put("width", image.getWidth());
		map.put("height", image.getHeight());
		return map;
	}

	// ファイルアイテムをバイト配列化する
	private byte[] readAll(FileItem item) throws IOException {
		InputStream inputStream = item.getInputStream();

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		while (true) {
			int len = inputStream.read(buffer);
			if (len < 0) {
				break;
			}
			bout.write(buffer, 0, len);
		}
		return bout.toByteArray();
	}

	//生成したファイルを格納するためのディレクトリを作成する
	private void makeDir(Map<String, Object> properties, HttpServletResponse resp) {
		// 生成したファイルを格納するディレクトリを作成する
		ServletContext sc = getServletContext();
		// ランダムなディレクトリ名を生成する
		String id = UUID.randomUUID().toString();
		String path = sc.getRealPath(id);
		properties.put("id",id);
		properties.put("path", path);
		File dir = new File(path);

		if (!dir.mkdir()) {
			throwErrorException(resp, "ディレクトリの作成に失敗しました");
		}
	}

	private void makeFile(Map<String, Object> properties, HttpServletResponse resp) throws IOException {

		ImageInputStream iis = ImageIO
				.createImageInputStream(new ByteArrayInputStream((byte[]) properties.get("data")));
		ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
		reader.setInput(iis);

		// gifファイルのフレーム枚数を取得
		int frame = (int) properties.get("frame");

		// １フレーム目から１枚ずつ出力
		for (int i = 0; i < frame; i++) {
			BufferedImage bi = null;
			BufferedImage master = null;
			IIOMetadata metadata = null;

			try {
				bi = reader.read(i);
				metadata = reader.getImageMetadata(i);
			} catch (RuntimeException re) {
				re.printStackTrace();
			}

			String[] imageatt = new String[] { "imageLeftPosition", "imageTopPosition", "imageWidth", "imageHeight" };

			// gif画像のメタデータを取得する
			// この処理を挟まないとpreloadjsサイドで、gif画像を認識しない
			// 参考元：https://stackoverflow.com/questions/8933893/convert-each-animated-gif-frame-to-a-separate-bufferedimage
			Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
			NodeList children = tree.getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {
				Node nodeItem = children.item(j);
				if (nodeItem.getNodeName().equals("ImageDescriptor")) {
					Map<String, Integer> imageAttr = new HashMap<>();
					for (int k = 0; k < imageatt.length; k++) {
						NamedNodeMap attr = nodeItem.getAttributes();
						Node attnode = attr.getNamedItem(imageatt[k]);
						imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
					}
					master = new BufferedImage((int) properties.get("width"), (int) properties.get("height"),
							BufferedImage.TYPE_INT_ARGB);
					master.getGraphics().drawImage(bi, imageAttr.get("imageLeftPosition"),
							imageAttr.get("imageTopPosition"), null);

					// ファイルを作成する
					boolean result = ImageIO.write(master, "gif", new File(properties.get("path") + "/file" + i + ".gif"));

					if (!result) {
						throwErrorException(resp, "ファイルの作成に失敗しました");
					}

				}
			}
		}

	}

	// エラーメッセージを画面に表示する
	private void throwErrorException(HttpServletResponse resp, String message) throws UploadException {
		resp.setCharacterEncoding("utf-8");
		PrintWriter out;
		try {
			out = resp.getWriter();
		} catch (IOException e) {
			throw new UploadException();
		}
		out.println("<!DOCTYPE html><head><meta charset='UTF-8'></head><body>");
		out.println(message);
		out.println("</body></body>");
		throw new UploadException();
	}
}
