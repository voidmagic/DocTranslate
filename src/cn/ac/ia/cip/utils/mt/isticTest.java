package cn.ac.ia.cip.utils.mt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;


public class isticTest {
	private STMTWeb stmt;
	protected int thcount;
	
	private static isticTest instance = null;
	
	public static synchronized isticTest getInstance() {
		if(instance == null) {
			instance = new isticTest();
		}
		return instance;
	}
	
	public isticTest(){
		stmt = new STMTWebService().getSTMTWebPort();		
	}
	
	public String test(String lang, String sent){
		String trans = stmt.getTranslation(lang,"UNKNOWN", "ABSTRACT", sent);
		System.out.println(lang);
		System.out.println("[src] "+ sent);
		System.out.println("[tgt] "+ trans);
		return trans;
	}
	
	public String test(String lang, String domain, String sent){
		String trans = stmt.getTranslation(lang, domain, "ABSTRACT", sent);
		System.out.println(lang+" "+ domain);
		System.out.println("[src] "+ sent);
		System.out.println("[tgt] "+ trans);
		return trans;
	}
	
	public static void writeFileByLines(String fn, List<String> sents){
		File file = new File(fn);
        BufferedWriter w = null;
        try {
            w = new BufferedWriter(new FileWriter(file));
            for (String s: sents){
            	w.write(s+"\n");
            }
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	
	public static Vector<String> readFileByLines(String fileName) {
		Vector<String> lines = new Vector<String>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	lines.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return lines;
    }

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String jsent = "この出願は、米国仮出願第60/194615号（2000年4月5日出願）および米国仮出願第60/158773号（1999年10月12日出願）からの優先権を主張する米国出願第09/679027号（2000年10月4日出願）の一部継続出願である。";
//		String esent = "The formation of pneumatoceles in adult pulmonary tuberculosis can occur before, during or after anti-tuberculosis treatment. A case of pneumatocele formation in a 19-year young female following pulmonary tuberculosis is reported. The left lung was completely replaced by pneumatocele. ";
		String csent = "C-芳基葡糖苷SGLT2抑制剂和方法本申请是2000年10月4日递交的申请序列号为09/679,027的美国专利申请的部分继续申请，所述美国专利申请所要求的优先权是2000年4月5日递交的申请号为60/194,615的美国临时申请和1999年10月12日递交的申请号为60/158,773的美国临时申请。\r\n" + 
				"  ";
//		
		
		
		isticTest t = new isticTest();
		t.test("CN2JP", csent);
	}

	

}
