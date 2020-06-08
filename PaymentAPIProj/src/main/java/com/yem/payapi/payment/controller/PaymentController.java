package com.yem.payapi.payment.controller;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.yem.payapi.payment.service.PaymentService;

/**
 * ����ó�� Controller
 * @author ����
 *
 */
@Controller
public class PaymentController {
	
	@Autowired
	private PaymentService paymentService;
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	
	/**
	 * ���� ��û
	 */
	@RequestMapping(value = "/payment/doPaymentRequest.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> doPaymentRequest(@RequestBody HashMap<String, Object> requestMap) throws Exception{
		logger.info("���� ��û ����  **************** ");
		logger.info("*** PaymentController.doPaymentRequest **");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = paymentService.doPaymentRequest(requestMap);
		return resultMap;
	}
	
	/**
	 * ������� ��û
	 */
	@RequestMapping(value = "/payment/doPaymentCancel.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> doPaymentCancel(@RequestBody HashMap<String, Object> requestMap) throws Exception{
		logger.info("������� ��û ����  **************** ");
		logger.info("*** PaymentController.doPaymentCancel ***");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = paymentService.doPaymentCancel(requestMap);
		return resultMap;
	}
	
	/**
	 * StringData ��ȸ 
	 */
	@RequestMapping(value = "/payment/doDataSearch.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> doDataSearch(@RequestBody HashMap<String, Object> requestMap) throws Exception{
		logger.info("StringData ��ȸ   **************** ");
		logger.info("*** PaymentController.doDataSearch ***");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = paymentService.doDataSearch(requestMap);
		return resultMap;
	}
	
}
