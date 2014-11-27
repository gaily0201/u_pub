package cn.gaily.pub.trigger.timer;

import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.vo.pub.BusinessException;

/**
 * <p>Title: ETLSynHistoryTimer</P>
 * <p>Description: 定时同步两个库中的数据, 同SynHistoryAction</p>
 * <p>暂未启用该定时任务，可在周末定时检测两边数据库是否同步，不同步可以同步;
 * 					       同时可在'数据同步管理'节点按钮(差异同步)手动启动同步</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-11-26
 */
public class ETLSynHistoryTimer implements IBackgroundWorkPlugin {

//	ISynMaintain service = NCLocator.getInstance().lookup(ISynMaintain.class);
//	
	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc) throws BusinessException {
//		BaseDAO dao = new BaseDAO();
//		
//		List<SynInfoHeadVO> vos = (List<SynInfoHeadVO>) dao.retrieveAll(SynInfoHeadVO.class);
//		if(vos==null||vos.size()<=0){
//			return null;
//		}
//		for(SynInfoHeadVO vo:vos){
//			service.synHistory(vo);  //同步两边数据库
//		}
//		
		return null;
	}

}
