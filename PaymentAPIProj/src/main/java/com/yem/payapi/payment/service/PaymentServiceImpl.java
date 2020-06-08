package com.yem.payapi.payment.service;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yem.payapi.common.AES256Util;
import com.yem.payapi.common.vo.CommonVO;
import com.yem.payapi.payment.db.PaymentDAO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

/*******
 * ����ó�� Service
 * @author ����
 *
 */
@Service
public class PaymentServiceImpl implements PaymentService{
	
	@Autowired
	private PaymentDAO paymentDAO;
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

	/**
	 * ���� ��û
	 */
	@Override
	public HashMap<String, Object> doPaymentRequest(HashMap<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub
		logger.info("****** PaymentServiceImpl.doPaymentRequest ******");
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		CommonVO commonVO = new CommonVO(); 
		
		/*******REQUEST VALUES *******/
		String uniqueId = ""; //������ȣ 
		String transDv = "PAYMENT"; //�ŷ����� 
		String cardNo = requestMap.get("card_no").toString(); //ī���ȣ 
		String expDt = requestMap.get("exp_dt").toString(); //��ȿ�Ⱓ 
		String cvc = requestMap.get("cvc").toString(); //cvc 
		String instmMonth = requestMap.get("instm_month").toString(); //�Һΰ�����
		String transAmt = requestMap.get("trans_amt").toString(); //�ŷ��ݾ�
		String valAddTax = requestMap.get("val_add_tax").toString(); //�ΰ���ġ�� 
		String stringData = ""; //string������
		String regUsr = "YEOM"; //�����
		
		/******* RESPONSE VALUES *******/
		String response_code = ""; //�����ڵ� 
		String response_msg = ""; //����޼��� 
		
		/******* ������ȣ ä�� *******/
		String maxUniqueId ="000000000000000000200";
		//= paymentDAO.selectMaxUniqueId();
		if(maxUniqueId.equals("")) {
			uniqueId = "000000000000000000001"; //�ʱⰪ ���� 
		}else {
			uniqueId = String.format("%020d",Integer.parseInt(maxUniqueId) + 1);
		} 
		logger.info("������ȣ ä��  ::::: "+uniqueId);
		
		/******* ī������ ��ȣȭ *******/ // 
		AES256Util aes256Util = new AES256Util();
		String encryptCardInfo = aes256Util.encrypt(String.format("%-20s", cardNo)+"|"+String.format("%-4s", expDt)+"|"+String.format("%-3s", cvc));
		logger.info("ī������ ��ȣȭ ������ ::::: "+encryptCardInfo);
		
		
		/******* �ΰ���ġ�� ��� *******/
		if(valAddTax.equals("")) {  //���� ���� ���� ���, �ڵ���� 
			int iValAddTax = (int) Math.round(Double.parseDouble(transAmt)/11); //�Ҽ������� �ݿø� 
			valAddTax = Integer.toString(iValAddTax); 
			logger.info("�ΰ���ġ�� ��� ::::: "+valAddTax);
		}

		/******* string������ ���� *******/ 
		stringData 
		= String.format("%-10s", transDv) //�����ͱ��� 
		+ String.format("%20s", uniqueId) //������ȣ
		+ String.format("%-20s", cardNo) //ī���ȣ 
		+ String.format("%02d", Integer.parseInt(instmMonth)) //�Һΰ�����
		+ String.format("%-4s", expDt) //ī����ȿ�Ⱓ
		+ String.format("%-3s", cvc) //CVC
		+ String.format("%10s", transAmt) //�ŷ��ݾ�
		+ String.format("%010d", Integer.parseInt(valAddTax)) //�ΰ���ġ��
		+ String.format("%20s", "") //���ŷ�������ȣ (�����ÿ��� ����)
		+ String.format("%-300s", encryptCardInfo) //��ȣȭ��ī������
		+ String.format("%-47s", "") //�����ʵ� 
	    ;	
		//������ ���� �߰� 
		stringData = String.format("%4s", stringData.length()) + stringData;
		logger.info("ī��翡 ���۵�  stringData ::::: " + stringData);
		
		/******* ���� VO�� ����  *******/
		commonVO.setTransDv(transDv); 
		commonVO.setCardNo(Integer.parseInt(cardNo));
		commonVO.setExpDt(Integer.parseInt(expDt));
		commonVO.setCvc(Integer.parseInt(cvc));
		commonVO.setInstmMonth(String.format("%02d", Integer.parseInt(instmMonth)));
		commonVO.setTransAmt(Integer.parseInt(transAmt));
		commonVO.setValAddTax(Integer.parseInt(valAddTax));
		commonVO.setOrgUniqueId("");
		commonVO.setStringData(stringData);
		commonVO.setRegUsr(regUsr);
		
		/******* ������ ���� üũ *******/
		chkData(commonVO);
		response_code = commonVO.getResponseCode();
		response_msg = commonVO.getResponseMsg();
		
		if(response_code.equals("0000")) { //������ 
			/******* �ŷ��⺻ ���̺� insert *******/
			paymentDAO.insertPayTransBase(commonVO);
			
			/******* �ŷ����� ���̺� insert *******/
			paymentDAO.insertPayTransDtls(commonVO);
			
			/******* json format���� ���  *******/
			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();
			JSONObject uniqueIdObj = new JSONObject();
			
			uniqueIdObj.put("������ȣ", uniqueId);
			obj.put("required",uniqueIdObj);
			obj.put("optional",uniqueIdObj);
			
			arr.add(obj);
			
			//����� ���� 
			resultMap.put("result",arr.toString());
		}
		
		/******* ������ ���� *******/
		resultMap.put("response_code",response_code);
		resultMap.put("response_msg",response_msg);
		return resultMap;
	}

	/**
	 * ������� ��û (�κ���� ����) 
	 */
	@Override
	public HashMap<String, Object> doPaymentCancel(HashMap<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub
		logger.info("****** PaymentServiceImpl.doPaymentCancel ******");
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		CommonVO commonVO = new CommonVO(); 
		
		/******* REQUEST VALUES *******/
		String uniqueId = ""; //������ȣ
		String transDv = "CANCEL"; //�ŷ����� 
		String orgUniqueId = requestMap.get("unique_id").toString(); //���ŷ�������ȣ  
		String transAmt = requestMap.get("cancal_amt").toString(); //�ŷ��ݾ�(��ұݾ�)
		String valAddTax = requestMap.get("val_add_tax").toString(); //�ΰ���ġ�� 
		String stringData = ""; //string������
		String regUsr = "YEOM"; //�����
		
		/******* RESPONSE VALUES *******/
		String response_code = ""; //�����ڵ� 
		String response_msg = ""; //����޼��� 
		
		/******* ���ŷ����� ��ȸ *******/
		commonVO.setUniqueId(orgUniqueId);
		paymentDAO.selectPayTransDtls(commonVO);

		/*******������ȣ ä�� *******/
		String maxUniqueId ="000000000000000000200";
		//= paymentDAO.selectMaxUniqueId();
		if(maxUniqueId.equals("")) {
			uniqueId = "000000000000000000001"; //�ʱⰪ ���� 
		}else {
			uniqueId = String.format("%020d",Integer.parseInt(maxUniqueId) + 1);
		} 
		logger.info("������ȣ ä��  ::::: "+uniqueId);
		
		/******* ī������ ��ȣȭ *******/
		String cardNo = Integer.toString(commonVO.getCardNo());
		String expDt = Integer.toString(commonVO.getExpDt());
		String cvc = Integer.toString(commonVO.getCvc());
		AES256Util aes256Util = new AES256Util();
		String encryptCardInfo = aes256Util.encrypt(String.format("%-20s", cardNo)+"|"+String.format("%-4s", expDt)+"|"+String.format("%-3s", cvc));
		logger.info("ī������ ��ȣȭ ������ ::::: "+encryptCardInfo);
		
		
		/******* �ΰ���ġ�� ��� *******/
		if(valAddTax.equals("")) {  //���� ���� ���� ���, ������������ �ΰ���ġ���� ����  
			int iValAddTax = (int) Math.round(Double.parseDouble(transAmt)/11); //�Ҽ������� �ݿø� 
			valAddTax = Integer.toString(iValAddTax); 
			logger.info("�ΰ���ġ�� ��� ::::: "+valAddTax);
		}
		
		/******* string������ ���� *******/ 
		stringData 
		= String.format("%-10s", transDv) //�����ͱ��� 
		+ uniqueId //������ȣ
		+ String.format("%-20s", cardNo) //ī���ȣ (�������Ϳ� ����)
		+ "00" //�Һΰ����� (��ҽÿ��� �Ͻú�"00"���� ����)
		+ String.format("%-4s", expDt) //ī����ȿ�Ⱓ (�������Ϳ� ����)
		+ String.format("%-3s", cvc) //CVC (�������Ϳ� ����)
		+ String.format("%10s", transAmt) //�ŷ��ݾ�
		+ String.format("%010d", Integer.parseInt(valAddTax)) //�ΰ���ġ��
		+ String.format("%-20s",orgUniqueId) //���ŷ�������ȣ (�����ÿ��� ����)
		+ String.format("%-300s", encryptCardInfo) //��ȣȭ��ī������
		+ String.format("%-47s", "") //�����ʵ� 
	    ;	
		//������ ���� �߰� 
		stringData = String.format("%4s", stringData.length()) + stringData;
		logger.info("ī��翡 ���۵� stringData :::::" + stringData);
		
		/******* ���� VO�� ���� *******/ 
		commonVO.setUniqueId(uniqueId);
		commonVO.setTransDv(transDv); 
		commonVO.setInstmMonth("00");
		commonVO.setTransAmt(Integer.parseInt(transAmt));
		commonVO.setValAddTax(Integer.parseInt(valAddTax));
		commonVO.setOrgUniqueId(orgUniqueId);
		commonVO.setStringData(stringData);
		commonVO.setRegUsr(regUsr);
		
		
		/******* ������ ���� üũ *******/
		chkData(commonVO);
		response_code = commonVO.getResponseCode();
		response_msg = commonVO.getResponseMsg();
		
		
		if(response_code.equals("0000")) {
			
			//�ݾ� ��� 
			commonVO.setPayAmt(commonVO.getPayAmt()-Integer.parseInt(transAmt));
			//commonVO.setPayValAddTax(pay_val_add_tax);
			
			/******* �ŷ��⺻ ���̺� insert *******/
			paymentDAO.insertPayTransBase(commonVO);
			/******* �ŷ����� ���̺� insert *******/
			paymentDAO.insertPayTransDtls(commonVO);
			
			/******* json format���� ���  *******/
			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();
			JSONObject uniqueIdObj = new JSONObject();
			
			uniqueIdObj.put("������ȣ", uniqueId);
			obj.put("required",uniqueIdObj);
			obj.put("optional",uniqueIdObj);
			
			arr.add(obj);
			
			//����� ���� 
			resultMap.put("result",arr.toString());
		}
		
		/******* ������ ���� *******/ 
		resultMap.put("response_code",response_code);
		resultMap.put("response_msg",response_msg);
		return resultMap;
	}
	
	/**
	 * StringData ��ȸ 
	 */
	@Override
	public HashMap<String, Object> doDataSearch(HashMap<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub
		logger.info("****** PaymentServiceImpl.doDataSearch ******");
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		CommonVO commonVO = new CommonVO(); 
		
		/******* REQUEST VALUES  *******/
		String uniqueId = requestMap.get("unique_id").toString(); //������ȣ  
		
		/******* RESPONSE VALUES  *******/
		String response_code = ""; //�����ڵ� 
		String response_msg = ""; //����޼��� 
		String stringData = ""; //string ������ 
		String cardNo = ""; //ī���ȣ 
		String expDt = ""; //��ȿ�Ⱓ 
		String cvc = ""; //cvc 
		String transDv = ""; //�ŷ����� 
		String transAmt = ""; //�ŷ��ݾ�
		String valAddTax = ""; //�ΰ���ġ�� 
		
		/******* ���� VO�� ����  *******/
		commonVO.setUniqueId(uniqueId);

		/******* ������ ���� üũ *******/
		chkData(commonVO);
		response_code = commonVO.getResponseCode();
		response_msg = commonVO.getResponseMsg();
		
		if(response_code.equals("0000")) {
			
			/******* string������ ��ȸ  *******/
			//commonVO = paymentDAO.selectPayTransBase(commonVO);
			stringData = " 446PAYMENT   000000000000000002015555555555          0555  5        55550000000505                    mC0tRrujsgxZ5fKMAs39G450mxaUk1b32R6nHbSWvYM=                                                                                                                                                                                                                                                                                                               \r\n" + 
					""; 
					//commonVO.getStringData();
			logger.info("ī��翡 ���۵� stringData :::::" + stringData);
			
			/******* String ������  ���䰪���� ����  *******/
			uniqueId = stringData.substring(14, 34); //������ȣ
			transDv = stringData.substring(4, 14); //�ŷ����� 
			transAmt = stringData.substring(63, 73); //�ŷ��ݾ�
			valAddTax = stringData.substring(73, 83); //�ΰ���ġ�� 
			
			/******* ��ȣȭ�� ī������ ��ȣȭ  *******/
			String encryptCardInfo = stringData.substring(103, 403);
			logger.info("��ȣȭ�� ī������ :::::" + encryptCardInfo);
			AES256Util aes256Util = new AES256Util();
			String decryptCardInfo = aes256Util.decrypt(encryptCardInfo);
			logger.info("ī������  ��ȣȭ  ������ ::::: "+decryptCardInfo);
			
			String[] CardInfo= decryptCardInfo.split("\\|");
			cardNo = CardInfo[0]; //ī���ȣ 
			expDt = CardInfo[1]; //��ȿ�Ⱓ 
			cvc = CardInfo[2]; //cvc 

			/******* json format���� ���  *******/
			JSONArray arr = new JSONArray();
			JSONObject uniqueIdObj = new JSONObject();
			uniqueIdObj.put("������ȣ", uniqueId);
			
			JSONObject cardObj = new JSONObject();
			JSONObject cardInfoObj = new JSONObject();
			cardInfoObj.put("ī���ȣ", cardNo.trim());
			cardInfoObj.put("��ȿ�Ⱓ", expDt.trim());
			cardInfoObj.put("cvc", cvc.trim());
			cardObj.put("ī������", cardInfoObj);
			
			JSONObject transDvObj = new JSONObject();
			transDvObj.put("����/��� ����", transDv.trim());
			
			JSONObject amtObj = new JSONObject();
			JSONObject amtInfoObj = new JSONObject();
			amtInfoObj.put("����/��ұݾ�", transAmt.trim());
			amtInfoObj.put("�ΰ���ġ��", Integer.parseInt(valAddTax));
			amtObj.put("�ݾ�����", amtInfoObj);
			
			arr.add(uniqueIdObj);
			arr.add(cardObj);
			arr.add(transDvObj);
			arr.add(amtObj);
			
			/******* ����� ����  *******/
			resultMap.put("result",arr.toString());
		}
		
		/******* ������ ����  *******/
		resultMap.put("response_code",response_code);
		resultMap.put("response_msg",response_msg);
		
		
		return  resultMap;
	}
	
	/**
	 * ������ ���� üũ 
	 * @param commonVO
	 * @return
	 * @throws Exception
	 */
	public void chkData(CommonVO commonVO) throws Exception {
		logger.info("****** PaymentServiceImpl.chkData ******");
		String code = "0000";
		String msg = "";
		
		//������ ��ȸ �� ������ȣ üũ
		if(commonVO.getTransDv().equals("")) {
			if(commonVO.getUniqueId().equals("")) {
				code = "0009";
				msg = "��������ȸ �� ������ȣ�� �ʼ��Դϴ�.";
			}
			if(commonVO.getUniqueId().length() != 20) {
				code = "0010";
				msg = "��ȿ�������� ������ȣ �Դϴ�.";
			}
		}else {
			//ī���ȣ �ڸ��� üũ 
			if(10 > (int)(Math.log10(commonVO.getCardNo())+1) || 20 < (int)(Math.log10(commonVO.getCardNo())+1)) {
				code = "0001";
				msg = "ī���ȣ�� �ּ�10�ڸ�, �ִ�20�ڸ����� �մϴ�.";
			}
			
			//���� �ŷ��� ��� 
			if(commonVO.getTransDv().equals("PAYMENT")){
				//�ŷ��ݾ� üũ  
				if(100 > commonVO.getTransAmt()) {
					code = "0002";
					msg = "�����ݾ��� 100�� �̻��̿��� �մϴ�.";
				}
				if(1000000000 < commonVO.getTransAmt()) {
					code = "0007";
					msg = "�����ݾ��� 10��� ���Ͽ��� �մϴ�.";
				}
				//�ΰ���ġ�� üũ  
				if(commonVO.getValAddTax() > commonVO.getTransAmt()) {
					code = "0003";
					msg = "�ΰ���ġ���� �ŷ��ݾ׺��� �۾ƾ��մϴ�.";
				}
				//���ŷ�������ȣ 
				if(!commonVO.getOrgUniqueId().equals("")) {
					code = "0006";
					msg = "���� �ÿ��� ���ŷ�������ȣ�� �����̿��� �մϴ�.";
				}
			}
			//��� �ŷ��� ��� 
			else if(commonVO.getTransDv().equals("CANCEL")){
				//�ŷ��ݾ� üũ  
				if(commonVO.getTransAmt() > commonVO.getPayAmt()) {
					code = "0004";
					msg = "���� �ݾ׺��� �۾ƾ��մϴ�.";
				}
				//�ΰ���ġ�� üũ   
				if(commonVO.getValAddTax() > commonVO.getTransAmt()) {
					code = "0005";
					msg = "�ΰ���ġ���� �ŷ��ݾ׺��� �۾ƾ��մϴ�.";
				}
				if(commonVO.getValAddTax() > commonVO.getPayValAddTax()) {
					code = "0008";
					msg = "�Է��� �ΰ���ġ���� ���������� �ΰ���ġ������ Ů�ϴ�.";
				}
			}
		}
		commonVO.setResponseCode(code);
		commonVO.setResponseMsg(msg);
	}

}
