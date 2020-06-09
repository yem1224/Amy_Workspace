package com.yem.payapi.payment.service;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yem.payapi.common.AES256Util;
import com.yem.payapi.common.vo.CommonVO;
import com.yem.payapi.payment.db.PaymentDAO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
 
/**
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
	@Transactional(rollbackFor=Exception.class)
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
		
		/******* REQUEST VALUES�� ����  *******/
		commonVO.setTransDv(transDv); 
		commonVO.setCardNo(cardNo);
		commonVO.setExpDt(expDt);
		commonVO.setCvc(cvc);
		commonVO.setInstmMonth(String.format("%02d", Long.parseLong(instmMonth)));
		commonVO.setTransAmt(transAmt);
		commonVO.setValAddTax(valAddTax);
		
		/******* ������ ���� üũ *******/
		chkData(commonVO);
		response_code = commonVO.getResponseCode();
		response_msg = commonVO.getResponseMsg();
		
		if(response_code.equals("0000")) { //������ 
			
			/******* ������ȣ ä�� *******/
			uniqueId = makeUniqueId();
			
			/******* ī������ ��ȣȭ *******/ // 
			AES256Util aes256Util = new AES256Util();
			String encryptCardInfo = aes256Util.encrypt(cardNo+"|"+expDt+"|"+cvc);
			logger.info("ī������ ��ȣȭ ������ ::::: "+encryptCardInfo);
			
			/******* �ΰ���ġ�� ��� *******/
			valAddTax = calValAddTax(transAmt, valAddTax,"0","0");
			
			/******* string������ ���� *******/ 
			stringData 
			= String.format("%-10s", transDv) //�����ͱ��� 
			+ String.format("%20s", uniqueId) //������ȣ
			+ String.format("%-20s", cardNo) //ī���ȣ 
			+ String.format("%02d", Long.parseLong(instmMonth)) //�Һΰ�����
			+ String.format("%-4s", expDt) //ī����ȿ�Ⱓ
			+ String.format("%-3s", cvc) //CVC
			+ String.format("%10s", transAmt) //�ŷ��ݾ�
			+ String.format("%010d", Long.parseLong(valAddTax)) //�ΰ���ġ��
			+ String.format("%20s", "") //���ŷ�������ȣ (�����ÿ��� ����)
			+ String.format("%-300s", encryptCardInfo) //��ȣȭ��ī������
			+ String.format("%-47s", "") //�����ʵ� 
		    ;	
			//������ ���� �߰� 
			stringData = String.format("%4s", stringData.length()) + stringData;
			logger.info("ī��翡 ���۵�  stringData ::::: " + stringData);
			
			/******* ���� VO ����  *******/
			commonVO.setUniqueId(uniqueId);
			commonVO.setValAddTax(valAddTax);
			commonVO.setPayAmt(transAmt);
			commonVO.setPayValAddTax(valAddTax);
			commonVO.setOrgUniqueId("");
			commonVO.setStringData(stringData);
			commonVO.setRegUsr(regUsr);
			commonVO.setStatus("0"); // ��������( 0: ���� ) 
			commonVO.setSuccessYn("Y");
			
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
			//obj.put("optional",uniqueIdObj);
			
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
	 * @throws Exception 
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public HashMap<String, Object> doPaymentCancel(HashMap<String, Object> requestMap) throws Exception{
		// TODO Auto-generated method stub
		logger.info("****** PaymentServiceImpl.doPaymentCancel ******");
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		CommonVO commonVO = new CommonVO(); 
		
		/******* REQUEST VALUES *******/
		String orgUniqueId = requestMap.get("unique_id").toString(); //���ŷ�������ȣ  
		
		/******* RESPONSE VALUES *******/
		String response_code = ""; //�����ڵ� 
		String response_msg = ""; //����޼��� 
		
		/******* ���ŷ����� ��ȸ *******/
		try {
			commonVO.setUniqueId(orgUniqueId);
			commonVO = paymentDAO.selectPayTransDtls(commonVO); 
			logger.info("���ŷ����� ��ȸ [�ŷ��Ϸù�ȣ  ::::: "+commonVO.getTransSeq()+" ]");
			logger.info("���ŷ����� ��ȸ [������ȣ  ::::: "+orgUniqueId+" ]");
			logger.info("���ŷ����� ��ȸ [ī���ȣ  ::::: "+commonVO.getCardNo()+" ]");
			logger.info("���ŷ����� ��ȸ [��ȿ�Ⱓ  ::::: "+commonVO.getExpDt()+" ]");
			logger.info("���ŷ����� ��ȸ [cvc  ::::: "+commonVO.getCvc()+" ]");
			logger.info("���ŷ����� ��ȸ [���� ����  ::::: "+commonVO.getStatus()+" ]");
			logger.info("���ŷ����� ��ȸ [�ΰ���ġ�� ::::: "+commonVO.getValAddTax()+" ]");
			logger.info("���ŷ����� ��ȸ [���������� �ݾ� ::::: "+commonVO.getPayAmt()+" ]");
			logger.info("���ŷ����� ��ȸ [���������� �ΰ���ġ��::: "+commonVO.getPayValAddTax()+" ]");
			commonVO.setOrgValAddTax(commonVO.getValAddTax()); //��� �� �ΰ���ġ�� üũ�� ���� 
		}
		catch (Exception e) {
			// TODO: handle exception
			resultMap.put("response_code","9999");
			resultMap.put("response_msg","������ȣ�� �ش��ϴ� ���������� �����ϴ�.");
			return  resultMap;
		}
		
		/******* REQUEST VALUES�� ����  *******/ 
		String transDv = "CANCEL";
		String transAmt = requestMap.get("cancel_amt").toString(); //�ŷ��ݾ�(��ұݾ�)
		String valAddTax = requestMap.get("val_add_tax").toString(); //�ΰ���ġ�� 
		String ctt = requestMap.get("ctt").toString(); //��һ��� 
		String regUsr = "YEOM"; //�����

		commonVO.setTransDv(transDv); 
		commonVO.setTransAmt(transAmt);
		commonVO.setCtt(ctt);
		commonVO.setOrgUniqueId(orgUniqueId);
		commonVO.setRegUsr(regUsr);
		
		/******* �ΰ���ġ�� ��� *******/
		valAddTax = calValAddTax(transAmt, valAddTax,commonVO.getPayAmt(),commonVO.getPayValAddTax());
		commonVO.setValAddTax(valAddTax);

		/******* ������ ���� üũ *******/
		chkData(commonVO);
		response_code = commonVO.getResponseCode();
		response_msg = commonVO.getResponseMsg();
		
		if(response_code.equals("0000")) {
			/*******������ȣ ä�� *******/
			String uniqueId = makeUniqueId();
			
			/******* ī������ ��ȣȭ *******/
			String cardNo = commonVO.getCardNo();
			String expDt = commonVO.getExpDt();
			String cvc = commonVO.getCvc();
			AES256Util aes256Util = new AES256Util();
			String encryptCardInfo = aes256Util.encrypt(cardNo+"|"+expDt+"|"+cvc);
			logger.info("ī������ ��ȣȭ ������ ::::: "+encryptCardInfo);
			
			
			/******* string������ ���� *******/ 
			String stringData
			= String.format("%-10s", transDv) //�����ͱ��� 
			+ uniqueId //������ȣ
			+ String.format("%-20s", cardNo) //ī���ȣ (�������Ϳ� ����)
			+ "00" //�Һΰ����� (��ҽÿ��� �Ͻú�"00"���� ����)
			+ String.format("%-4s", expDt) //ī����ȿ�Ⱓ (�������Ϳ� ����)
			+ String.format("%-3s", cvc) //CVC (�������Ϳ� ����)
			+ String.format("%10s", transAmt) //�ŷ��ݾ�
			+ String.format("%010d", Long.parseLong(valAddTax)) //�ΰ���ġ��
			+ String.format("%-20s",orgUniqueId) //���ŷ�������ȣ (�����ÿ��� ����)
			+ String.format("%-300s", encryptCardInfo) //��ȣȭ��ī������
			+ String.format("%-47s", "") //�����ʵ� 
		    ;	
			//������ ���� �߰� 
			stringData = String.format("%4s", stringData.length()) + stringData;
			logger.info("ī��翡 ���۵� stringData :::::" + stringData);
			
			/******* �κ���Ҹ� ���� �ݾ� ��� *******/
			long payAmt = Long.parseLong(commonVO.getPayAmt())-Long.parseLong(transAmt);
			long payValAddTax = Long.parseLong(commonVO.getPayValAddTax())-Long.parseLong(valAddTax);
			
			/******* ���� VO�� ���� *******/ 
			commonVO.setUniqueId(uniqueId);
			commonVO.setTransDv(transDv); 
			commonVO.setInstmMonth("00");
			commonVO.setTransAmt(transAmt);
			commonVO.setValAddTax(valAddTax);
			commonVO.setStringData(stringData);
			commonVO.setSuccessYn("Y");
			commonVO.setPayAmt(Long.toString(payAmt));
			commonVO.setPayValAddTax(Long.toString(payValAddTax));
			if(payAmt == 0) { //��� ������� ��� 
				commonVO.setStatus("2");
			}else { //�κ� ����� ��� 
				commonVO.setStatus("1");
			}
			
			/******* �ŷ��⺻ ���̺� insert *******/
			paymentDAO.insertPayTransBase(commonVO);
			/******* �ŷ����� ���̺� insert *******/
			paymentDAO.insertPayTransDtls(commonVO);
			/******* ���ŷ����� ���� update *******/
			paymentDAO.updatePayTransDtls(commonVO);
			
			/******* json format���� ���  *******/
			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();
			JSONObject uniqueIdObj = new JSONObject();
			
			uniqueIdObj.put("������ȣ", uniqueId);
			obj.put("required",uniqueIdObj);
			//obj.put("optional",uniqueIdObj);
			
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
	@Transactional(rollbackFor=Exception.class)
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
		
		/******* REQUEST VALUES ����  *******/
		commonVO.setUniqueId(uniqueId);

		/******* ������ ���� üũ *******/
		chkData(commonVO);
		response_code = commonVO.getResponseCode();
		response_msg = commonVO.getResponseMsg();
		
		if(response_code.equals("0000")) {
			
			/******* string������ ��ȸ  *******/
			try {
				commonVO = paymentDAO.selectPayTransBase(commonVO);
				stringData = commonVO.getStringData();
				logger.info("ī��翡 ���۵� stringData :::::" + stringData);
			}
			catch (NullPointerException e) {
				// TODO: handle exception
				resultMap.put("response_code","9999");
				resultMap.put("response_msg","������ȣ�� �ش��ϴ� ���������� �����ϴ�.");
				return  resultMap;
			}
			
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
			amtInfoObj.put("�ΰ���ġ��", Long.parseLong(valAddTax));
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
	@Transactional(rollbackFor=Exception.class)
	public void chkData(CommonVO commonVO) throws Exception {
		logger.info("****** PaymentServiceImpl.chkData ******");
		String code = "0000";
		String msg = "";
		
		/******* üũ�� ����  *******/
		String chkTransDv= commonVO.getTransDv(); //�ŷ����� 
		String chkUniqueId= commonVO.getUniqueId(); //������ȣ
		String chkCardNo=  commonVO.getCardNo(); //ī���ȣ 
		String chkExpDt=  commonVO.getExpDt(); //��ȿ�Ⱓ 
		String chkCvc=  commonVO.getCvc(); //cvc 
		String chkOrgUniqueId= commonVO.getOrgUniqueId(); //���ŷ� ������ȣ 
		String chkInstmMonth=  commonVO.getInstmMonth(); //�Һΰ����� 
		String chkTransAmt= commonVO.getTransAmt(); //�ŷ��ݾ�
		String chkValAddTax= commonVO.getValAddTax(); //�ŷ��ΰ���ġ��
		String chkPayAmt= commonVO.getPayAmt(); //���������� �ŷ��ݾ�
		String chkPayValAddTax= commonVO.getPayValAddTax(); //���������� �ΰ���ġ��
		String chkOrgValAddTax= commonVO.getOrgValAddTax(); //���ŷ� �ΰ���ġ��
		String chkCtt= commonVO.getCtt(); //����

		//������ ��ȸ  �� ������ȣ üũ
		if(chkTransDv.equals("")) {
			if(chkUniqueId.equals("")) {
				code = "0009";
				msg = "������ȣ�� �Է����ּ���.";
			}
			else if(chkUniqueId.length() != 20) {
				code = "0010";
				msg = "��ȿ�������� ������ȣ �Դϴ�.";
			}
		}else {
			//���� �ŷ��� ��� 
			if(chkTransDv.equals("PAYMENT")){
				//ī���ȣ �ڸ��� üũ 
				if(10 >chkCardNo.length() || 20 < chkCardNo.length()) {
					code = "0001";
					msg = "ī���ȣ�� �ּ�10�ڸ�, �ִ�20�ڸ����� �մϴ�.";
				}
				//��ȿ�Ⱓ üũ
				else if(chkExpDt.length() != 4) {
					code = "0006";
					msg = "��ȿ�Ⱓ�� ��(2�ڸ�),�⵵(2�ڸ�)�� �Է����ּ���.";
				}
				else if(Integer.parseInt(chkExpDt.substring(0, 2)) > 12 || Integer.parseInt(chkExpDt.substring(0, 2)) < 1 ) {
					code = "0016";
					msg = "��ȿ�Ⱓ ���� 01~12�� �Է����ּ���.";
				}
				//cvcüũ
				else if(chkCvc.length() > 3) {
					code = "0013";
					msg = "CVC�� 3�ڸ��� �Է����ּ���.";
				}
				//�Ҽ������� üũ 
				else if(0 > Integer.parseInt(chkInstmMonth)|| 12 < Integer.parseInt(chkInstmMonth) || 1==Integer.parseInt(chkInstmMonth)) {
					code = "0012";
					msg = "�Һΰ������� 2~12 �� �Է����ּ���.";
				}
				//�ŷ��ݾ� üũ  
				else if(100 > Long.parseLong(chkTransAmt) ) {
					code = "0002";
					msg = "�����ݾ��� 100�� �̻��̿��� �մϴ�.";
				}
				else if(1000000000 < Long.parseLong(chkTransAmt) ) {
					code = "0007";
					msg = "�����ݾ��� 10��� ���Ͽ��� �մϴ�.";
				}
				//�ΰ���ġ�� üũ  
				else if( !chkValAddTax.equals("") && Long.parseLong(chkValAddTax) > Long.parseLong(chkTransAmt) ) {
					code = "0003";
					msg = "�ΰ���ġ���� �ŷ��ݾ׺��� �۾ƾ��մϴ�.";
				}
			}
			//��� �ŷ��� ��� 
			else if(chkTransDv.equals("CANCEL")){
				if(chkUniqueId.equals("")) {
					code = "0009";
					msg = "������ȣ�� �Է����ּ���.";
				}
				else if(chkUniqueId.length() != 20) {
					code = "0010";
					msg = "��ȿ�������� ������ȣ �Դϴ�.";
				}
				//���������� �ݾ��ִ��� üũ 
				else if(Long.parseLong(chkPayAmt) == 0) {
					code = "0011";
					msg = "���� �ݾ��� �����ϴ�.";
				}
				//�ŷ��ݾ� üũ  
				else if( Long.parseLong(chkTransAmt) > Long.parseLong(chkPayAmt) ) {
					code = "0004";
					msg = "���� �ݾ׺��� �۾ƾ��մϴ�.\n[���������� �ݾ�: "+chkPayAmt+"]";
				}
				//�ΰ���ġ�� üũ
				else if( !chkValAddTax.equals("") && Long.parseLong(chkValAddTax) > Long.parseLong(chkTransAmt) ) {
					code = "0005";
					msg = "�ΰ���ġ���� �ŷ��ݾ׺��� �۾ƾ��մϴ�.";
				}
				else if( !chkValAddTax.equals("") && Long.parseLong(chkValAddTax) > Long.parseLong(chkPayValAddTax) ) {
					code = "0008";
					msg = "�Է��� �ΰ���ġ���� ���������� �ΰ���ġ������ Ů�ϴ�.\n[���������� �ΰ���ġ�� : "+chkPayValAddTax+"]";
				}
				//��ü ����� ���, ���ŷ� �ݾ��� �ΰ���ġ���� �� ��ұݾ��� �ΰ���ġ���� �հ� ���ƾ���.
				else if(Long.parseLong(chkPayAmt)- Long.parseLong(chkTransAmt) == 0) { 
					String valAddTaxSum = paymentDAO.selectOrgValAddTax(chkOrgUniqueId);
					if((Double.parseDouble(valAddTaxSum)+Double.parseDouble(chkValAddTax)) != Double.parseDouble(chkOrgValAddTax)) {
						code = "0014";
						msg = "����� ���, �� �ŷ� �ݾ��� �ΰ���ġ���� �� ��ұݾ��� �ΰ���ġ���� �հ� ���ƾ� �մϴ�.";
					}
				}
				else if(!chkCtt.equals("") && chkCtt.length() > 50) {
					code = "0015";
					msg = "������ 50�� ���� �Է����ּ���.";
				}
			}
			
		}
		commonVO.setResponseCode(code);
		commonVO.setResponseMsg(msg);
	}
	
	
	/**
	 * ������ȣ ä�� 
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public String makeUniqueId() throws Exception {
		String id = "";
		String maxUniqueId = paymentDAO.selectMaxUniqueId();
		if(maxUniqueId.equals("00000000000000000000")) {
			id  = "00000000000000000001"; //�ʱⰪ ���� 
		}else {
			id  = String.format("%020d",Long.parseLong(maxUniqueId) + 1);
		} 
		logger.info("������ȣ ä��  ::::: "+id );
		return id;
	}
	
	/**
	 * �ΰ���ġ�� ��� �� ���� 
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public String calValAddTax(String amt, String valAddTax,String payAmt, String payValAddTax) throws Exception {
		String tax = "";
		//���� ���� ���� ���, �ڵ���� 
		if(valAddTax.equals("")) {  
			if(Long.parseLong(payAmt) == Long.parseLong(amt)) { //��ü����� ��� 
				tax = payValAddTax; //�ΰ���ġ���� �����ִ� �ݾ����� ���� 
			}else {
				int iValAddTax = (int) Math.round(Double.parseDouble(amt)/11); //�Ҽ������� �ݿø� 
				tax = Integer.toString(iValAddTax);
			}
		}else {
			tax = valAddTax; 
		}
		logger.info("�ΰ���ġ�� ��� ::::: "+tax);
		return tax;
	}
	
}
