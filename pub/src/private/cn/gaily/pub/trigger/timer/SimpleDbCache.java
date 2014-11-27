package cn.gaily.pub.trigger.timer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import cn.gaily.simplejdbc.SimpleDSMgr;

public class SimpleDbCache implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5154909122846931122L;
	
	public static int initSize = 5;
	
	private static Map<String,List<SimpleDSMgr>> ds = null;
	
	public static Map<String, List<SimpleDSMgr>> getDs() {
		return ds;
	}
	
	public static void setDs(Map<String, List<SimpleDSMgr>> ds) {
		SimpleDbCache.ds = ds;
	}
	
	
	public static void clear(){
		if(ds==null){
			return;
		}
		List<SimpleDSMgr> list = null;
		for(java.util.Iterator<Entry<String, List<SimpleDSMgr>>> it=ds.entrySet().iterator();it.hasNext();){
			list = it.next().getValue();
			for(SimpleDSMgr d: list){
				d.realRelease();
			}
		}
		
	}
	
}
