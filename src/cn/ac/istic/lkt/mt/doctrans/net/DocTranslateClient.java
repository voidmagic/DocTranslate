/**
 * 
 */
package cn.ac.istic.lkt.mt.doctrans.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Chongde SHI
 *
 */
public class DocTranslateClient {
	private static String SERVER_IP ;
	private static int SERVER_PORT ;

	public DocTranslateClient(String ip, int port) {
		SERVER_IP = ip;
		SERVER_PORT = port;
	}

	public static Socket createSocket() {
		try {
			Socket ns = new Socket(SERVER_IP, SERVER_PORT);
			System.out.println("Server connected             : [" + SERVER_IP+":"+SERVER_PORT+"]");
			System.out.println("Cliect port                  : [" + ns.getInetAddress()+":"+ns.getLocalPort() + "]");
			return ns;
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
	}
	
	public String time(){
		return "["+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date())+"] ";
	}
	
	class TranslateClientAgent implements Runnable {
		Socket socket = null;
		String ifn;
		String ofn;
		String language;
		String domain;

		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("     -- Request Info --  \n");
			sb.append("     Inputfile : "+ifn+"\n");
			sb.append("    Outputfile : "+ofn+"\n");
			sb.append("      Language : "+language+"\n");
			sb.append("         Domain: "+domain+"\n");
			return sb.toString();
		}
		public TranslateClientAgent(String ifn, String ofn, String language, String domain) {
			this.ifn = ifn;
			this.ofn = ofn;
			this.language = language;
			this.domain = domain;
		}

		@Override
		public void run() {
			try {
				this.socket = createSocket();
				socketTranslate(this.ifn, this.ofn, this.language, this.domain);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (Exception e) {

					}
				}
			}
		}
		
		/**
		 * @param inf
		 * @param outf
		 * @param language
		 * @param mode
		 * @throws Exception
		 */
		public void socketTranslate(String inf, String outf, String language, String domain) throws Exception {
			DataOutputStream dos = null;
			DataInputStream dis = null;
			FileInputStream fis = null;
			FileOutputStream fos = null;
			
			try {

				File file = new File(inf);
				if (file.exists()) {
					fis = new FileInputStream(file);
					dos = new DataOutputStream(socket.getOutputStream());
					System.out.println(this.toString());
					
					System.out.println(time()+"Uploading file... ");
					
					// 先发送 “文件名###语种###文件模式###”
					dos.writeUTF(file.getName() + "###" + language + "###" + domain + "###" + Long.toString(file.length()));
					dos.flush();
					
					// 确认发送的信息正确
					dis = new DataInputStream(socket.getInputStream());
					String good = dis.readUTF();
					if(good.charAt(0) == '0') {
						System.out.println("发送信息与服务器不匹配");
						System.exit(-1);
					}
					

					// 再发送文件数据
					byte[] bytes = new byte[1024];
					int length = 0;
					while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
						dos.write(bytes, 0, length);
						dos.flush();
					}
					System.out.println(time()+"Translating...");
					
					// 接收返回数据
					
					File ofile = new File(outf);
					fos = new FileOutputStream(ofile);
					//String info = dis.readUTF();
					// 先接收信息看是否翻译成功，再接收文件
						
						while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
							fos.write(bytes, 0, length);
							fos.flush();
						}
						System.out.println(time()+"Translation finished and file received");
					

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fis != null)
					fis.close();
				if (fos != null)
					dos.close();
				if (dis != null)
					fis.close();
				if (dos != null)
					dos.close();
			}
		}
	}

	public void startTransThread(String inf, String outf, String language, String domain) {
		
		try {
			new Thread(new TranslateClientAgent(inf, outf, language, domain)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String inf = null;
		String outf = null;
		String language = null;
		if (args.length != 6) {
			System.out.println("usage: java stDocTransClient.jar -ec/-ce/-jc ip port domain input-file output-file");
			return;
		}
		
		// language pair
		if (args[0].equals("-ec")) {
			language = "EN2CN";
		} else if (args[0].equals("-jc")) {
			language = "JP2CN";
		} else if (args[0].equals("-ce"))
			language = "CN2EN";
		else {
			System.err.println("ERROR! Language error.");
			System.exit(-1);
		}

	
		String ip = args[1];
		int port = Integer.parseInt(args[2]);
		String domain = args[3].toUpperCase();
		
		File fin = new File(args[4]);
		if (!fin.exists()) {
			System.err.println("ERROR! File " + fin + " not exists.");
			System.exit(-1);
		}
		inf = fin.getAbsolutePath();

		File fout = new File(args[5]);
		if (fout.exists()) {
			System.err.println("Warning! File " + fin + " exists. It will be overrided.");
		}
		outf = fout.getAbsolutePath();

		try {
			DocTranslateClient sc = new DocTranslateClient(ip, port);
			sc.startTransThread(inf, outf, language, domain);
			//sc.startTransThread("D:/English.pdf", "D:/English_result.pdf", "EN2CN", "REPORT");
			//sc.startTransThread("D:/e5_pic.pdf", "D:/e5_result.pdf", "EN2CN", "REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
