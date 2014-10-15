package nc.gaily.pub.vo;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggBaseInfoMeta extends AbstractBillMeta {
  public AggBaseInfoMeta() {
    this.init();
  }
  private void init() {
    this.setParent(nc.gaily.pub.vo.BaseInfo.class);
    this.addChildren(nc.gaily.pub.vo.RelationInfo.class);
  }
}