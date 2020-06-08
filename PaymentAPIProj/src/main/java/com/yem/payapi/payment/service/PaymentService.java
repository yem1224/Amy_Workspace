package com.yem.payapi.payment.service;

import java.util.*;

/**
 * 결제처리 Service interface
 * @author 은미
 *
 */
public interface PaymentService {
	
	/**
	 * 결제 요청 
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> doPaymentRequest(HashMap<String, Object> requestMap) throws Exception;

	/**
	 * 결제취소 요청
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> doPaymentCancel(HashMap<String, Object> requestMap) throws Exception;

	/**
	 * StringData 조회 
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> doDataSearch(HashMap<String, Object> requestMap) throws Exception;

}
