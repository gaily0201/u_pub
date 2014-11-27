package cn.gaily.pub.trigger.timer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.exception.DbException;
import cn.gaily.pub.util.CommonUtils;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;

public class ETLBackgrondDbUtil {
	
	

	public final int initDBSize = 5;
	
	public Map<String,List<SimpleDSMgr>> getAllDs() {
		Map<String,List<SimpleDSMgr>> allDs = SimpleDbCache.getDs();
		if(allDs == null){
			allDs = new HashMap<String,List<SimpleDSMgr>>();
		}
		
		Entry<String, List<SimpleDSMgr>> entry = null;
		Map<String, List<SimpleDSMgr>> result = null;
		List<SimpleDSMgr> list = null;
		boolean reInit = false;
		
		if(allDs.size()>0){
			for(Iterator it=allDs.entrySet().iterator();it.hasNext();){
				entry = (Entry<String, List<SimpleDSMgr>>) it.next();
				list = entry.getValue();
				for(SimpleDSMgr mgr: list){
					if(mgr.conns.size()<initDBSize){
						reInit = true;
						break;
					}
					for(Connection conn: mgr.conns){
						try {
							if(conn==null||conn.isClosed()){
								reInit = true;
								break;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					continue;
				}
			}
			if(reInit){
				for(Iterator it=allDs.entrySet().iterator();it.hasNext();){
					entry = (Entry<String, List<SimpleDSMgr>>) it.next();
					list = entry.getValue();
					for(SimpleDSMgr mgr: list){
						mgr.realRelease();
					}
				}
				SimpleDbCache.clear();
				result = getProperties();
				allDs = result;
				SimpleDbCache.setDs(allDs);
				return allDs;
				
			}
		}else{
			SimpleDbCache.clear();
			result = getProperties();
			allDs = result;
			SimpleDbCache.setDs(allDs);
			return allDs;
		}
		return allDs;
	}
	
	/**
	 * <p>方法名称：getProperties</p>
	 * <p>方法描述：获取数据源属性</p>
	 * @return 	     返回Map<String, SimpleDSMgr[]>， key:pk，value:array
	 * @author xiaoh
	 * @since  2014-11-20
	 * <p> history 2014-11-20 xiaoh  创建   <p>
	 */
	private Map<String, List<SimpleDSMgr>> getProperties(){
		String sql = "SELECT A.PK, A.SRCNAME, A.SCRIP, A.SRCPORT, A.SRCUSERNAME, A.SRCPASSWORD, A.TARNAME, A.TARUSERNAME, A.TARIP, A.TARPORT, A.TARPASSWORD FROM ETL_SYNINFO A WHERE A.DR = 0";
		SimpleDSMgr s1 = null;
		SimpleDSMgr s2 = null;
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String srcname = null;
		String scrip = null;
		String srcport = null;
		String srcusername = null;
		String srcpassword = null;
		String tarname =  null;
		String tarip =  null;
		String tarport =  null;
		String tarusername =  null;
		String tarpassword =  null;
		String pk = null;
		List<SimpleDSMgr> ds = null;
		Map<String, List<SimpleDSMgr>> result = new HashMap<String,List<SimpleDSMgr>>();
		try {
			JdbcSession session = new JdbcSession();
			conn = session.getConnection();
			if(conn==null){
				return null;
			}
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()){
				s1 = new SimpleDSMgr();
				s2 = new SimpleDSMgr();
				ds = new ArrayList<SimpleDSMgr>();
				pk = rs.getString("pk");
				srcname = rs.getString("srcname");
				scrip = rs.getString("scrip");
				srcport = rs.getString("srcport");
				srcusername = rs.getString("srcusername");
				srcpassword = rs.getString("srcpassword");
				s1.setDbName(srcname);
				s1.setIp(scrip);
				s1.setPort(srcport);
				s1.setUserName(srcusername);
				s1.setPassword(srcpassword);
				
				tarname = rs.getString("tarname");
				tarip = rs.getString("tarip");
				tarport = rs.getString("tarport");
				tarusername = rs.getString("tarusername");
				tarpassword = rs.getString("tarpassword");
				s2.setDbName(tarname);
				s2.setIp(tarip);
				s2.setPort(tarport);
				s2.setUserName(tarusername);
				s2.setPassword(tarpassword);
				
				s1.setInitSize(initDBSize);
				s1.init();
				s2.setInitSize(initDBSize);
				s2.init();
				ds.add(s1);
				ds.add(s2);
				result.put(pk, ds);
			}
		} catch (DbException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(conn, st, rs);
		}
		return result;
	}
	
	
	
	/**
	 * <p>方法名称：writeProp</p>
	 * <p>方法描述：写配置文件</p>
	 * @param pk		配置文件唯一名称
	 * @param remotee	
	 * @param locall	
	 * @author xiaoh
	 * @since  2014-11-20
	 * <p> history 2014-11-20 xiaoh  创建   <p>
	 */
	private void writeProp(String pk, SimpleDSMgr remotee, SimpleDSMgr locall) {
		CommonUtils common = new CommonUtils();
		common.setPropFilepath(pk, 1);
		common.setProperty(pk, "remote.dbname", remotee.getDbName());
		common.setProperty(pk, "remote.username", remotee.getUserName());
		common.setProperty(pk, "remote.password", remotee.getPassword());
		common.setProperty(pk, "remote.ip", remotee.getIp());
		common.setProperty(pk, "remote.port", remotee.getPort());
		common.setProperty(pk, "local.dbname", locall.getDbName());
		common.setProperty(pk, "local.username", locall.getUserName());
		common.setProperty(pk, "local.password", locall.getPassword());
		common.setProperty(pk, "local.ip", locall.getIp());
		common.setProperty(pk, "local.port", locall.getPort());
	}
}
