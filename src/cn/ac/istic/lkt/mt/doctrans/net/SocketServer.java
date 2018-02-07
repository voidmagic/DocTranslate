/**
 * 
 */
package cn.ac.istic.lkt.mt.doctrans.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.baiyyang.global.Global;
import com.baiyyang.main.Translate;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import cn.ac.istic.lkt.mt.utils.RandomString;

public class SocketServer extends ServerSocket {
	public static int pid = 0;
	private static Logger logger = Logger.getLogger(SocketServer.class);

	/*
	 * 需要翻译的文件列表
	 */
	private static LinkedBlockingQueue<SrcInfo> filePool = new LinkedBlockingQueue<SrcInfo>();
	/*
	 * 翻译器实例
	 */
	public Translate translate = new Translate();

	public SocketServer(int port) throws Exception {
		super(port);
		logger.info("Translation Server started, listen at port: " + super.getLocalPort());
	}

	/**
	 * 使用线程处理每个客户端传输的文件
	 * 
	 * @throws Exception
	 */
	public void load() throws Exception {
		// 启动翻译线程
		new Thread(new QueueTranslator()).start();
		
		while (true) {
			// server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
			Socket socket = this.accept();
			/**
			 * 我们的服务端处理客户端的连接请求是同步进行的， 每次接收到来自客户端的连接请求后，
			 * 都要先跟当前的客户端通信完之后才能再处理下一个连接请求。 这在并发比较多的情况下会严重影响程序的性能，
			 * 为此，我们可以把它改为如下这种异步处理与客户端通信的方式
			 */
			// 每接收到一个Socket就建立一个新的线程来处理它
			new Thread(new TranslateServerAgent(socket)).start();
		}
	}
	
	
	/*
	 * 翻译队列中的文件
	 */
	class QueueTranslator implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (filePool.isEmpty()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					SrcInfo si = null;
					try {
						si = filePool.take();
					} catch (Exception e) {
						e.printStackTrace();
					} 
					System.out.println("Start Translating: "+ si.filename);

					if (si != null){
						String winf = Global.absPath(si.filename);
						String woutf = Global.absTransPath(si.filename);
						boolean success = false;
						
						/*
						 * 临时改为都用SOCI翻译文档
						 */
						//success = translate.fileTranslate(winf, woutf, si.language, si.domain);
						try {
							translate.fileTranslate(winf, woutf, si.language, "SOCI", "REPORT");
							success = true;
						}catch(Exception e) {
							generateFailPdf(woutf, e.toString());
							e.printStackTrace();
							success = false;
						}
						//创建空文档，标记
						try{
							if (success){
								File donef=new File(Global.absDonePath(si.filename));
								donef.createNewFile();
							}else{
								File errorf=new File(Global.absErrorPath(si.filename));
								errorf.createNewFile();
							}
						}catch(IOException e){
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * 处理客户端传输过来的文件线程类
	 */
	class TranslateServerAgent implements Runnable {
		private Socket socket;
		private int tid = 0;
		public TranslateServerAgent(Socket socket) {
			logger.info("Socket connected: "+ socket.getRemoteSocketAddress());
			this.socket = socket;
			tid = pid;
			pid += 1;
		}
		
		@Override
		public void run() {
        	DataOutputStream dos = null; 
        	DataInputStream dis = null;
        	FileInputStream fis = null;
        	FileOutputStream fos = null;
			try {
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				
				String fileName = null;
				String language = null;
				String domain = null;
				// 从socket读取文件信息
				String strInfo = dis.readUTF();
				String[] sl = strInfo.split("###");
				
				if (sl.length != 4){
					logger.error("接口字符串错误"+ strInfo);
					dos.writeUTF("0###接口字符串违规!");
				}
				else{
					
					logger.info("Request detail:"+ strInfo);
					// 返回正确信息
					dos.writeUTF("1###success!");
					dos.flush();
					
					fileName = sl[0].trim();
					language = sl[1].trim();
					domain = sl[2].trim();
					long fileLength = Long.parseLong(sl[3]);
					String ifn = RandomString.getTimeString(4) + "." + fileName;				
					File directory = new File(Global.FILED);
					
					if (!directory.exists()) {
						directory.mkdir();
					}
					File file = new File(directory.getAbsolutePath() + File.separatorChar + ifn);
					fos = new FileOutputStream(file);
					System.out.println(file.getAbsolutePath());
					// 开始接收文件
					byte[] bytes = new byte[1024];
					int length = 0;
					long total = 0;
					while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
						fos.write(bytes, 0, length);
						fos.flush();
						total += length;
						if(total == fileLength){
							break;
						}
					}
					logger.info("File Receivied, saved as: [" + file.getAbsolutePath() + "] [Size：" + fileLength);
					SrcInfo si = new SrcInfo(ifn, language, domain, fileLength);
					filePool.put(si);
					logger.info("File in Queue: ["+ si.toString()+"]");
					
					// 检查是否已经翻译完成
					File donf = new File(Global.absDonePath(ifn));
					File errf = new File(Global.absErrorPath(ifn));
					
					while(true){
						if (donf.exists()){ // 翻译完成
							fis = new FileInputStream(Global.absTransPath(ifn));
			                  
							dos.writeUTF("1###success"); //发送成功标签
							dos.flush();
							// 将翻译文件发送给client
							while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
								dos.write(bytes, 0, length);
								dos.flush();
							}
							logger.info("Translation sent successful. ["+ ifn+"]");
							break;
						}else if(errf.exists()){ // 翻译失败
							dos.writeUTF("0###fail"); //发送失败标签
							break;
						}else{
							try{
								Thread.sleep(1000);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
						
					}
					Global.deleteFiles(ifn);
				}
				
			} catch (Exception e) {
				System.err.println("Tid: "+tid);
				e.printStackTrace();
			} finally {
				try {
					if (fos != null)
						fos.close();
					if (fis != null)
						fis.close();
					if (dos != null)
						dos.close();
					if (dis != null)
						dis.close();
					socket.close();
				} catch (Exception e) {
				}
			}
		}
	}
	
	public void generateFailPdf(String path, String failInfo){
		try {
			PdfWriter writer = new PdfWriter(path);
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document doc = new Document(pdfDoc);
			String CFONT = "C:/windows/fonts/msyh.ttf";
			PdfFont cfont = PdfFontFactory.createFont(CFONT, PdfEncodings.IDENTITY_H, true);
			doc.setFont(cfont).setFontSize(16);
			doc.add(new Paragraph(failInfo));
			doc.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	};
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Global.WORKD=System.getProperty("user.dir");
		Global.prepareDir();
		
		String port = args[0];
		SocketServer server = null;
		try {
			server = new SocketServer(Integer.parseInt(port)); // 启动服务端
			server.load();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(server != null) {
				try {
					server.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
