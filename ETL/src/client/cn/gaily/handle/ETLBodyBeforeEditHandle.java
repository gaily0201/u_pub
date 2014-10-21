package cn.gaily.handle;

import nc.gaily.pub.vo.BaseInfo;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;

public class ETLBodyBeforeEditHandle implements IAppEventHandler<CardBodyBeforeEditEvent> {

	@Override
	public void handleAppEvent(CardBodyBeforeEditEvent e) {
		e.setReturnValue(true);
	}

}
