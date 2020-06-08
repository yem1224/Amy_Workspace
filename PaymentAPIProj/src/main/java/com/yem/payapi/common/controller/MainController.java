package com.yem.payapi.common.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 메인화면에서 이동 controller
 */
@Controller
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	/**
	 * 메인 화면
	 */
	@RequestMapping(value = "/main.do", method = RequestMethod.GET)
	public String boardList() throws Exception{
		Date date = new Date();
		logger.info("메인 화면  **************** " + date);
		return "main";
	}
	
	/**
	 * 결제 요청 화면
	 */
	@RequestMapping(value = "/payment/paymentRequest.do", method = RequestMethod.GET)
	public String paymentRequest() throws Exception{
		Date date = new Date();
		logger.info("결제 요청 화면  **************** " + date);
		return "payment_request";
	}
	
	/**
	 * 결제취소 요청 화면
	 */
	@RequestMapping(value = "/payment/paymentCancel.do", method = RequestMethod.GET)
	public String paymenCancel() throws Exception{
		Date date = new Date();
		logger.info("결제취소 요청 화면  **************** " + date);
		return "payment_cancel";
	}
	
	/**
	 * 데이터조회 화면 
	 */
	@RequestMapping(value = "/search/dataSearch.do", method = RequestMethod.GET)
	public String dataSearch() throws Exception{
		Date date = new Date();
		logger.info("데이터조회 화면  **************** " + date);
		return "data_search";
	}
}
