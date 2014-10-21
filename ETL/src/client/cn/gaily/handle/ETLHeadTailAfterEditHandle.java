package cn.gaily.handle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.gaily.pub.vo.BaseInfo;
import nc.gaily.pub.vo.RelationInfo;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import cn.gaily.itf.IETLService;
import cn.gaily.pub.util.CommonUtil;


public class ETLHeadTailAfterEditHandle implements IAppEventHandler<CardHeadTailAfterEditEvent> {

	
	
	
	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		setBodyLines(e);
	}

	/**
	 * <p>方法名称：setBodyLines</p>
	 * <p>方法描述：自动带出标题行，并将源头字段名称、类型、长度赋值</p>
	 * @param e
	 * @author xiaoh
	 * @since  2014-10-21
	 * <p> history 2014-10-21 xiaoh  创建   <p>
	 */
	private void setBodyLines(CardHeadTailAfterEditEvent e) {
		IETLService service = NCLocator.getInstance().lookup(IETLService.class);
		Object tableNameObj = e.getBillCardPanel().getHeadItem(BaseInfo.SRCTABNAME).getValueObject();
		Object dataSourceNameObj = e.getBillCardPanel().getHeadItem(BaseInfo.SRCDATASOURCE).getValueObject();

		if(tableNameObj==null||dataSourceNameObj==null){
			return; //TODO 弹出提示
		}
		
		String tableName = tableNameObj.toString();
		String dataSourceName = dataSourceNameObj.toString();
		if(CommonUtil.isEmpty(tableName)||CommonUtil.isEmpty(dataSourceName)){
			return; //TODO 弹出提示
		}
		
		ArrayList<HashMap<String,String>> list = service.queryColumnInfo(tableName,dataSourceName);
		if(list.isEmpty()){
			return ;
		}
		int rowCount = e.getBillCardPanel().getBodyPanel().getTable().getRowCount();
		if(rowCount>0){
			int arr[] = new int[rowCount];
			for(int i=0;i<rowCount;i++){
				arr[i]=i;
			}
			e.getBillCardPanel().getBodyPanel().delLine(arr);
		}
		e.getBillCardPanel().getBodyPanel().addLine(list.size());
		Map<String,String> map = null;
		for(int i=0;i<list.size();i++){
			map = list.get(i);
			e.getBillCardPanel().setBodyValueAt(map.get("name"), i, RelationInfo.SRCCOLUMN);
			e.getBillCardPanel().setBodyValueAt(map.get("type"), i, RelationInfo.SRCCOLTYPE);
			e.getBillCardPanel().setBodyValueAt(map.get("length"), i, RelationInfo.SRCCOLLENGTH	);
		}
	}
}
