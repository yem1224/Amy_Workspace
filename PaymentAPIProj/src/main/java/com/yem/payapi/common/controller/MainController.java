package com.yem.payapi.common.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * ����ȭ�鿡�� �̵� controller
 */
@Controller
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	/**
	 * ���� ȭ��
	 */
	@RequestMapping(value = "/main.do", method = RequestMethod.GET)
	public String boardList() throws Exception{
		Date date = new Date();
		logger.info("���� ȭ��  **************** " + date);
		return "main";
	}
	
	/**
	 * ���� ��û ȭ��
	 */
	@RequestMapping(value = "/payment/paymentRequest.do", method = RequestMethod.GET)
	public String paymentRequest() throws Exception{
		Date date = new Date();
		logger.info("���� ��û ȭ��  **************** " + date);
		return "payment_request";
	}
	
	/**
	 * ������� ��û ȭ��
	 */
	@RequestMapping(value = "/payment/paymentCancel.do", method = RequestMethod.GET)
	public String paymenCancel() throws Exception{
		Date date = new Date();
		logger.info("������� ��û ȭ��  **************** " + date);
		return "payment_cancel";
	}
	
	/**
	 * ��������ȸ ȭ�� 
	 */
	@RequestMapping(value = "/search/dataSearch.do", method = RequestMethod.GET)
	public String dataSearch() throws Exception{
		Date date = new Date();
		logger.info("��������ȸ ȭ��  **************** " + date);
		return "data_search";
	}
}
