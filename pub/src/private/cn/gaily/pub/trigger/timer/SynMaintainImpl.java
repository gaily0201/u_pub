package cn.gaily.pub.trigger.timer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.vo.pub.BusinessException;
import cn.gaily.pub.trigger.AbstractETLTask;
import cn.gaily.pub.trigger.DefaultETLTask;
import cn.gaily.pub.util.CommonUtils;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;

public class SynMaintainImpl {

	
	static boolean hasChecked = false;
	
	Connection conn = null;
	
	BaseDAO service = null;
	
	public void changeState() throws BusinessException {
		if(hasChecked){
			return;
		}
		Map<String,List<SimpleDSMgr>> allDs = new HashMap<String, List<SimpleDSMgr>>();
		SimpleDSMgr remote = null;
		SimpleDSMgr local  = null;
		ETLBackgrondDbUtil util = new ETLBackgrondDbUtil();
		allDs = util.getAllDs();
		Entry<String,List<SimpleDSMgr>> entry = null;
		for(Iterator it=allDs.entrySet().iterator();it.hasNext();){
			entry = (Entry<String, List<SimpleDSMgr>>) it.next();
			remote = entry.getValue().get(0);
			local = entry.getValue().get(1);
			
			if(remote.conns.size()<=0||local.conns.size()<=0){
				continue;
			}
			
			Connection cc = null;
			PreparedStatement pst = null;
			
			AbstractETLTask task = DefaultETLTask.getInstance();
			String sql = "SELECT TABLENAME,STATUS FROM XFL_TABSTATUS";
			String usql = "UPDATE ETL_SYNTABLE SET EXERESULT = ? WHERE TABLENAME = ?";
			JdbcSession session = null;
			try {
				session = new JdbcSession();
				cc = session.getConnection();
				cc.setAutoCommit(false);
				pst  = cc.prepareStatement(usql);
				ArrayList<Object[]> results = (ArrayList<Object[]>) session.executeQuery(sql, new ArrayListProcessor());
				String tableName = null;
				String status = null;
				for(Object[] result: results){
					tableName = (String) result[0];
					status = (String) result[1];
					if(CommonUtils.isNotEmpty(tableName)&&CommonUtils.isNotEmpty(status)){
						remote = task.enableTrigger(remote, tableName, Integer.valueOf(status));
						local  = task.enableTrigger(local, tableName, Integer.valueOf(status));
						pst.setString(1, "1".equals(status)? "启用":"停用");
						pst.setString(2, tableName.toUpperCase().trim());
						pst.addBatch();
						pst.clearParameters();
					}
				}
				pst.executeBatch();
				cc.commit();
			} catch (DbException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				SimpleJdbc.release(cc, pst, null);
			}
		}
		hasChecked = true;
	}
	
	public static void setHasChecked(boolean hasCheckedd) {
		hasChecked = hasCheckedd;
	}

//	@Override
//	public void synHistory(SynInfoHeadVO vo) {
//
//		SimpleDSMgr remote = new SimpleDSMgr();
//		SimpleDSMgr local  = new SimpleDSMgr();
//		int count = 0;
//		Map<String,List<SimpleDSMgr>> map = SimpleDbCache.getDs();
//		List<SimpleDSMgr> list = null;
//		boolean reInit = false;
//		if(map!=null){
//			list = map.get(vo.getPk());
//			if(list==null){
//				reInit = true;
//			}else if(list.size()!=2){
//				reInit = true;
//			}else if(list.size()==2){
//				for(SimpleDSMgr m :list){
//					if(m.conns.size()<=SimpleDbCache.initSize){
//						reInit = true;
//						break;
//					}
//				}
//				if(reInit){
//					for(SimpleDSMgr m: list){
//						m.realRelease();
//					}
//				}
//			}else{
//				reInit = true;
//			}
//			
//			if(!reInit){
//				remote = list.get(0);
//				local = list.get(1);
//			}
//		}
//		if(reInit==true||map==null){
//			String srcdbname = vo.getSrcname();
//			String srcip = vo.getScrip();
//			String srcuname = vo.getSrcusername();
//			String srcpwd  = vo.getSrcpassword();
//			String srcport = vo.getSrcport();
//			
//			String destdbname = vo.getTarname();
//			String destip = vo.getTarip();
//			String destuname = vo.getTarusername();
//			String destpwd = vo.getTarpassword();
//			String destport = vo.getTarport();
//			
//			remote.setDbName(destdbname);
//			remote.setIp(destip);
//			remote.setUserName(destuname);
//			remote.setPassword(destpwd);
//			remote.setPort(destport);
//			
//			local.setDbName(srcdbname);
//			local.setIp(srcip);
//			local.setUserName(srcuname);
//			local.setPassword(srcpwd);
//			local.setPort(srcport);
//			
//			remote.setInitSize(SimpleDbCache.initSize);
//			local.setInitSize(SimpleDbCache.initSize);
//			remote.init();
//			local.init();
//			
//		}
//		
//		Entry<String, List<SimpleDSMgr>> entry = null;
//		String fileName = null;
//		if(remote!=null&&local!=null&&remote.conns.size()>=SimpleDbCache.initSize&&local.conns.size()>=SimpleDbCache.initSize){
//			TaskExecutor task = new TaskExecutor();
//			task.executeSyn(remote, local);
//		}else{
//			synHistory(vo);
//			count ++;
//			if(count>=10){
//				throw new RuntimeException("同步历史数据出错! "+ fileName);
//			}
//		}
//	}
}


















