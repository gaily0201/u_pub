package cn.gaily.pub.ref;

import cn.gaily.pub.vo.PubRef;
import nc.ui.bd.ref.AbstractRefModel;

public class ColumnRefModel extends AbstractRefModel {

	public static String tableName = "pubref";
	public ColumnRefModel(){
		super();
		init();
	}
	
	public void init(){
		setRefNodeName("code");
		setRefTitle("公共代码");
		setFieldCode(new String[]{PubRef.PK,PubRef.CHILDCODE,PubRef.CHILDNAME});
		setFieldName(new String[]{"主键","代码","名称"});
		setHiddenFieldCode(new String[]{PubRef.PK});
		setPkFieldCode(PubRef.PK);
		setRefCodeField(PubRef.CHILDCODE);
		setRefCodeField(PubRef.CHILDNAME);
		setTableName(tableName);
		setOrderPart(PubRef.ORDERPART);
	}
}

/**
insert into bd_refinfo (CODE, DR, ISNEEDPARA, ISSPECIALREF, METADATATYPENAME, MODULENAME, NAME, PARA1, PARA2, PARA3, PK_REFINFO, REFCLASS, REFSYSTEM, REFTYPE, RESERV1, RESERV2, RESERV3, RESID, RESIDPATH, TS, WHEREPART)
values ('A00001', 0, '', '', 'pubref', 'pub', '公共参照(性别)', '', '', '', '0001Z01000000001A001', 'cn.gaily.pub.ref.ColumnRefModel', '', 0, '', '', '', 'PUBREF-000002', 'pubref', '2014-10-21 17:06:23', 'basecode=''sex''');


 */