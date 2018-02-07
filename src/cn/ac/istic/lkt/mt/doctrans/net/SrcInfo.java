/**
 * 从客户端发给服务器端的请求相关的信息
 */
package cn.ac.istic.lkt.mt.doctrans.net;

/**
 * @author Chongde SHI
 *
 */
public class SrcInfo {
	public String filename = null;
	public String language = null;
	public String domain = null;
	public long filesize = 0;
	public SrcInfo(String filename, String language, String domain, long filesize){
		this.filename=filename;
		this.language=language;
		this.domain = domain;
		this.filesize=filesize;
	}
	public String toString(){
		return "{"+filename+", "+ language+ ", " + domain +", " + filesize+"}";
	}
}
