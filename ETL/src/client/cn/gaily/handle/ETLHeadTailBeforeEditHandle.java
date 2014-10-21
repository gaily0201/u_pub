package cn.gaily.handle;

import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;

public class ETLHeadTailBeforeEditHandle implements IAppEventHandler<CardHeadTailBeforeEditEvent> {

	
	@Override
	public void handleAppEvent(CardHeadTailBeforeEditEvent e) {
		e.setReturnValue(true);
		
	}

}
