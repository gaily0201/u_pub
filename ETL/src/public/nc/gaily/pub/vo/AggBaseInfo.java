package nc.gaily.pub.vo;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.gaily.pub.vo.BaseInfo")
public class AggBaseInfo extends AbstractBill {

  /**
	 * 
	 */
	private static final long serialVersionUID = 8109830895242151223L;

@Override
  public IBillMeta getMetaData() {
    IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggBaseInfoMeta.class);
    return billMeta;
  }

  @Override
  public BaseInfo getParentVO() {
    return (BaseInfo) this.getParent();
  }
}