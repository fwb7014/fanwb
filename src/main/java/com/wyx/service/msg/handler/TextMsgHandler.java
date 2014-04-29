package com.wyx.service.msg.handler;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.wyx.dto.KeyMsg;
@Component
public class TextMsgHandler extends AbsMsgHandler {
	private static final Logger logger = Logger.getLogger(TextMsgHandler.class);
	
	public TextMsgHandler(){
		System.out.println("***************************************************");
	}

	/**
	 * 处理普通的消息
	 * 
	 * @param msg
	 */

	@Override
	public String getMsgHandlerType() {
		return "text";
	}

	@Override
	@Async
	public void handleMsg(KeyMsg keyMsg) {
		// 现在开始处理消息
		logger.info("我要发送返回请求咯");
	}

}
