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
 * <p>Description: ��ʱͬ���������е�����, ͬSynHistoryAction</p>
 * <p>��δ���øö�ʱ���񣬿�����ĩ��ʱ����������ݿ��Ƿ�ͬ������ͬ������ͬ��;
 * 					       ͬʱ����'����ͬ������'�ڵ㰴ť(����ͬ��)�ֶ�����ͬ��</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
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
//			service.synHistory(vo);  //ͬ���������ݿ�
//		}
//		
		return null;
	}

}
