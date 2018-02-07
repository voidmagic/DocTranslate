/**
 * 从服务器端发给客户端的请求的信息
 */
package cn.ac.istic.lkt.mt.doctrans.net;

/**
 * @author Chongde SHI
 *
 */
public class TgtInfo {
	public String filename = null;
	public long filesize = 0;
	public TgtInfo(String filename, long filesize){
		this.filename=filename;
		this.filesize=filesize;
	}
}
