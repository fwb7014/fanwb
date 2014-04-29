package com.wyx.web.receiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.wyx.dto.receive.ReceiveMsg;
import com.wyx.service.DoFirstService;
import com.wyx.service.msg.handler.AbsMsgHandler;

/**
 * 消息的处理类
 * 
 * @author Administrator
 * 
 */
@Controller
public class MsgController {

	private static final Logger logger = Logger.getLogger(MsgController.class);

	@Autowired
	private DoFirstService doFirst;

	@Autowired
	private List<AbsMsgHandler> msgHandler;

	/**
	 * 消息的处理器
	 */
	private XStream xstream;

	public void init() {
		xstream = new XStream(new DomDriver());
		xstream.processAnnotations(ReceiveMsg.class);
	}

	/**
	 * 开通的第一步 激活开发者模式
	 * 
	 * @param signature
	 * @param timestampba
	 * 
	 * @param nonce
	 * @param echostr
	 * @return
	 */
	@ResponseBody
	@RequestMapping("receive.do")
	public String ktWinXin(HttpServletRequest request) {
		// doFirstService(request);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					request.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			logger.info("socket消息 " + sb.toString());

			ReceiveMsg receiveMsg = getReceiveMsg(sb.toString());
			boolean findHandler = false;
			for (AbsMsgHandler handle : msgHandler) {
				if (handle.getMsgHandlerType().equals(receiveMsg.getMsgType())) {
					handle.doMsgSender(receiveMsg);
					findHandler = true;
					break;
				}
			}

			if (!findHandler) { // 没有可以用的处理类
				logger.error(" 没有找到可以处理消息的handler receiveMsg 类型为"
						+ receiveMsg.getMsgType());
			}
			return "succ";
		} catch (Exception e) {
			logger.error("异常", e);
			return null;
		}
	}

	/**
	 * 解析参数
	 * 
	 * @param requestMsg
	 * @return
	 */
	private ReceiveMsg getReceiveMsg(String requestMsg) throws Exception {
		ReceiveMsg msg = (ReceiveMsg) xstream.fromXML(requestMsg);
		return msg;
	}

	/**
	 * 验证
	 * 
	 * @param request
	 * @return
	 */
	public String doFirstService(HttpServletRequest request) {
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		logger.info("timestamp=" + timestamp + ",nonce=" + nonce + ",echostr="
				+ echostr + ",signature=" + signature);
		return doFirst.doFirstService(signature, timestamp, nonce, echostr);

	}

}