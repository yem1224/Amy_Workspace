package com.yem.payapi.payment.service;

import java.util.*;

/**
 * ����ó�� Service interface
 * @author ����
 *
 */
public interface PaymentService {
	
	/**
	 * ���� ��û 
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> doPaymentRequest(HashMap<String, Object> requestMap) throws Exception;

	/**
	 * ������� ��û
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> doPaymentCancel(HashMap<String, Object> requestMap) throws Exception;

	/**
	 * StringData ��ȸ 
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> doDataSearch(HashMap<String, Object> requestMap) throws Exception;

}
