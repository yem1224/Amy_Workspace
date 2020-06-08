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
 * 결제처리 Service
 * @author 은미
 *
 */
@Service
public class PaymentServiceImpl implements PaymentService{
	
	@Autowired
	private PaymentDAO paymentDAO;
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

	/**
	 * 결제 요청
	 */
	@Override
	public HashMap<String, Object> doPaymentRequest(HashMap<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub
		logger.info("****** PaymentServiceImpl.doPaymentRequest ******");
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		CommonVO commonVO = new CommonVO(); 
		
		/*******REQUEST VALUES *******/
		String uniqueId = ""; //관리번호 
		String transDv = "PAYMENT"; //거래구분 
		String cardNo = requestMap.get("card_no").toString(); //카드번호 
		String expDt = requestMap.get("exp_dt").toString(); //유효기간 
		String cvc = requestMap.get("cvc").toString(); //cvc 
		String instmMonth = requestMap.get("instm_month").toString(); //할부개월수
		String transAmt = requestMap.get("trans_amt").toString(); //거래금액
		String valAddTax = requestMap.get("val_add_tax").toString(); //부가가치세 
		String stringData = ""; //string데이터
		String regUsr = "YEOM"; //등록자
		
		/******* RESPONSE VALUES *******/
		String response_code = ""; //응답코드 
		String response_msg = ""; //응답메세지 
		
		/******* 관리번호 채번 *******/
		String maxUniqueId ="000000000000000000200";
		//= paymentDAO.selectMaxUniqueId();
		if(maxUniqueId.equals("")) {
			uniqueId = "000000000000000000001"; //초기값 세팅 
		}else {
			uniqueId = String.format("%020d",Integer.parseInt(maxUniqueId) + 1);
		} 
		logger.info("관리번호 채번  ::::: "+uniqueId);
		
		/******* 카드정보 암호화 *******/ // 
		AES256Util aes256Util = new AES256Util();
		String encryptCardInfo = aes256Util.encrypt(String.format("%-20s", cardNo)+"|"+String.format("%-4s", expDt)+"|"+String.format("%-3s", cvc));
		logger.info("카드정보 암호화 데이터 ::::: "+encryptCardInfo);
		
		
		/******* 부가가치세 계산 *******/
		if(valAddTax.equals("")) {  //값을 받지 않은 경우, 자동계산 
			int iValAddTax = (int) Math.round(Double.parseDouble(transAmt)/11); //소수점이하 반올림 
			valAddTax = Integer.toString(iValAddTax); 
			logger.info("부가가치세 계산 ::::: "+valAddTax);
		}

		/******* string데이터 생성 *******/ 
		stringData 
		= String.format("%-10s", transDv) //데이터구분 
		+ String.format("%20s", uniqueId) //관리번호
		+ String.format("%-20s", cardNo) //카드번호 
		+ String.format("%02d", Integer.parseInt(instmMonth)) //할부개월수
		+ String.format("%-4s", expDt) //카드유효기간
		+ String.format("%-3s", cvc) //CVC
		+ String.format("%10s", transAmt) //거래금액
		+ String.format("%010d", Integer.parseInt(valAddTax)) //부가가치세
		+ String.format("%20s", "") //원거래관리번호 (결제시에는 공백)
		+ String.format("%-300s", encryptCardInfo) //암호화된카드정보
		+ String.format("%-47s", "") //예비필드 
	    ;	
		//데이터 길이 추가 
		stringData = String.format("%4s", stringData.length()) + stringData;
		logger.info("카드사에 전송될  stringData ::::: " + stringData);
		
		/******* 공통 VO에 세팅  *******/
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
		
		/******* 데이터 제약 체크 *******/
		chkData(commonVO);
		response_code = commonVO.getResponseCode();
		response_msg = commonVO.getResponseMsg();
		
		if(response_code.equals("0000")) { //성공시 
			/******* 거래기본 테이블 insert *******/
			paymentDAO.insertPayTransBase(commonVO);
			
			/******* 거래내역 테이블 insert *******/
			paymentDAO.insertPayTransDtls(commonVO);
			
			/******* json format으로 출력  *******/
			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();
			JSONObject uniqueIdObj = new JSONObject();
			
			uniqueIdObj.put("관리번호", uniqueId);
			obj.put("required",uniqueIdObj);
			obj.put("optional",uniqueIdObj);
			
			arr.add(obj);
			
			//결과값 세팅 
			resultMap.put("result",arr.toString());
		}
		
		/******* 응답결과 세팅 *******/
		resultMap.put("response_code",response_code);
		resultMap.put("response_msg",response_msg);
		return resultMap;
	}

	/**
	 * 결제취소 요청 (부분취소 포함) 
	 */
	@Override
	public HashMap<String, Object> doPaymentCancel(HashMap<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub
		logger.info("****** PaymentServiceImpl.doPaymentCancel ******");
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		CommonVO commonVO = new CommonVO(); 
		
		/******* REQUEST VALUES *******/
		String uniqueId = ""; //관리번호
		String transDv = "CANCEL"; //거래구분 
		String orgUniqueId = requestMap.get("unique_id").toString(); //원거래관리번호  
		String transAmt = requestMap.get("cancal_amt").toString(); //거래금액(취소금액)
		String valAddTax = requestMap.get("val_add_tax").toString(); //부가가치세 
		String stringData = ""; //string데이터
		String regUsr = "YEOM"; //등록자
		
		/******* RESPONSE VALUES *******/
		String response_code = ""; //응답코드 
		String response_msg = ""; //응답메세지 
		
		/******* 원거래정보 조회 *******/
		commonVO.setUniqueId(orgUniqueId);
		paymentDAO.selectPayTransDtls(commonVO);

		/*******관리번호 채번 *******/
		String maxUniqueId ="000000000000000000200";
		//= paymentDAO.selectMaxUniqueId();
		if(maxUniqueId.equals("")) {
			uniqueId = "000000000000000000001"; //초기값 세팅 
		}else {
			uniqueId = String.format("%020d",Integer.parseInt(maxUniqueId) + 1);
		} 
		logger.info("관리번호 채번  ::::: "+uniqueId);
		
		/******* 카드정보 암호화 *******/
		String cardNo = Integer.toString(commonVO.getCardNo());
		String expDt = Integer.toString(commonVO.getExpDt());
		String cvc = Integer.toString(commonVO.getCvc());
		AES256Util aes256Util = new AES256Util();
		String encryptCardInfo = aes256Util.encrypt(String.format("%-20s", cardNo)+"|"+String.format("%-4s", expDt)+"|"+String.format("%-3s", cvc));
		logger.info("카드정보 암호화 데이터 ::::: "+encryptCardInfo);
		
		
		/******* 부가가치세 계산 *******/
		if(valAddTax.equals("")) {  //값을 받지 않은 경우, 결제데이터의 부가가치세로 세팅  
			int iValAddTax = (int) Math.round(Double.parseDouble(transAmt)/11); //소수점이하 반올림 
			valAddTax = Integer.toString(iValAddTax); 
			logger.info("부가가치세 계산 ::::: "+valAddTax);
		}
		
		/******* string데이터 생성 *******/ 
		stringData 
		= String.format("%-10s", transDv) //데이터구분 
		+ uniqueId //관리번호
		+ String.format("%-20s", cardNo) //카드번호 (원데이터와 동일)
		+ "00" //할부개월수 (취소시에는 일시불"00"으로 저장)
		+ String.format("%-4s", expDt) //카드유효기간 (원데이터와 동일)
		+ String.format("%-3s", cvc) //CVC (원데이터와 동일)
		+ String.format("%10s", transAmt) //거래금액
		+ String.format("%010d", Integer.parseInt(valAddTax)) //부가가치세
		+ String.format("%-20s",orgUniqueId) //원거래관리번호 (결제시에는 공백)
		+ String.format("%-300s", encryptCardInfo) //암호화된카드정보
		+ String.format("%-47s", "") //예비필드 
	    ;	
		//데이터 길이 추가 
		stringData = String.format("%4s", stringData.length()) + stringData;
		logger.info("카드사에 전송될 stringData :::::" + stringData);
		
		/******* 공통 VO에 세팅 *******/ 
		commonVO.setUniqueId(uniqueId);
		commonVO.setTransDv(transDv); 
		commonVO.setInstmMonth("00");
		commonVO.setTransAmt(Integer.parseInt(transAmt));
		commonVO.setValAddTax(Integer.parseInt(valAddTax));
		commonVO.setOrgUniqueId(orgUniqueId);
		commonVO.setStringData(stringData);
		commonVO.setRegUsr(regUsr);
		
		
		/******* 데이터 제약 체크 *******/
		chkData(commonVO);
		response_code = commonVO.getResponseCode();
		response_msg = commonVO.getResponseMsg();
		
		
		if(response_code.equals("0000")) {
			
			//금액 계산 
			commonVO.setPayAmt(commonVO.getPayAmt()-Integer.parseInt(transAmt));
			//commonVO.setPayValAddTax(pay_val_add_tax);
			
			/******* 거래기본 테이블 insert *******/
			paymentDAO.insertPayTransBase(commonVO);
			/******* 거래내역 테이블 insert *******/
			paymentDAO.insertPayTransDtls(commonVO);
			
			/******* json format으로 출력  *******/
			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();
			JSONObject uniqueIdObj = new JSONObject();
			
			uniqueIdObj.put("관리번호", uniqueId);
			obj.put("required",uniqueIdObj);
			obj.put("optional",uniqueIdObj);
			
			arr.add(obj);
			
			//결과값 세팅 
			resultMap.put("result",arr.toString());
		}
		
		/******* 응답결과 세팅 *******/ 
		resultMap.put("response_code",response_code);
		resultMap.put("response_msg",response_msg);
		return resultMap;
	}
	
	/**
	 * StringData 조회 
	 */
	@Override
	public HashMap<String, Object> doDataSearch(HashMap<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub
		logger.info("****** PaymentServiceImpl.doDataSearch ******");
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		CommonVO commonVO = new CommonVO(); 
		
		/******* REQUEST VALUES  *******/
		String uniqueId = requestMap.get("unique_id").toString(); //관리번호  
		
		/******* RESPONSE VALUES  *******/
		String response_code = ""; //응답코드 
		String response_msg = ""; //응답메세지 
		String stringData = ""; //string 데이터 
		String cardNo = ""; //카드번호 
		String expDt = ""; //유효기간 
		String cvc = ""; //cvc 
		String transDv = ""; //거래구분 
		String transAmt = ""; //거래금액
		String valAddTax = ""; //부가가치세 
		
		/******* 공통 VO에 세팅  *******/
		commonVO.setUniqueId(uniqueId);

		/******* 데이터 제약 체크 *******/
		chkData(commonVO);
		response_code = commonVO.getResponseCode();
		response_msg = commonVO.getResponseMsg();
		
		if(response_code.equals("0000")) {
			
			/******* string데이터 조회  *******/
			//commonVO = paymentDAO.selectPayTransBase(commonVO);
			stringData = " 446PAYMENT   000000000000000002015555555555          0555  5        55550000000505                    mC0tRrujsgxZ5fKMAs39G450mxaUk1b32R6nHbSWvYM=                                                                                                                                                                                                                                                                                                               \r\n" + 
					""; 
					//commonVO.getStringData();
			logger.info("카드사에 전송될 stringData :::::" + stringData);
			
			/******* String 데이터  응답값으로 조립  *******/
			uniqueId = stringData.substring(14, 34); //관리번호
			transDv = stringData.substring(4, 14); //거래구분 
			transAmt = stringData.substring(63, 73); //거래금액
			valAddTax = stringData.substring(73, 83); //부가가치세 
			
			/******* 암호화된 카드정보 복호화  *******/
			String encryptCardInfo = stringData.substring(103, 403);
			logger.info("암호화된 카드정보 :::::" + encryptCardInfo);
			AES256Util aes256Util = new AES256Util();
			String decryptCardInfo = aes256Util.decrypt(encryptCardInfo);
			logger.info("카드정보  복호화  데이터 ::::: "+decryptCardInfo);
			
			String[] CardInfo= decryptCardInfo.split("\\|");
			cardNo = CardInfo[0]; //카드번호 
			expDt = CardInfo[1]; //유효기간 
			cvc = CardInfo[2]; //cvc 

			/******* json format으로 출력  *******/
			JSONArray arr = new JSONArray();
			JSONObject uniqueIdObj = new JSONObject();
			uniqueIdObj.put("관리번호", uniqueId);
			
			JSONObject cardObj = new JSONObject();
			JSONObject cardInfoObj = new JSONObject();
			cardInfoObj.put("카드번호", cardNo.trim());
			cardInfoObj.put("유효기간", expDt.trim());
			cardInfoObj.put("cvc", cvc.trim());
			cardObj.put("카드정보", cardInfoObj);
			
			JSONObject transDvObj = new JSONObject();
			transDvObj.put("결제/취소 구분", transDv.trim());
			
			JSONObject amtObj = new JSONObject();
			JSONObject amtInfoObj = new JSONObject();
			amtInfoObj.put("결제/취소금액", transAmt.trim());
			amtInfoObj.put("부가가치세", Integer.parseInt(valAddTax));
			amtObj.put("금액정보", amtInfoObj);
			
			arr.add(uniqueIdObj);
			arr.add(cardObj);
			arr.add(transDvObj);
			arr.add(amtObj);
			
			/******* 결과값 세팅  *******/
			resultMap.put("result",arr.toString());
		}
		
		/******* 응답결과 세팅  *******/
		resultMap.put("response_code",response_code);
		resultMap.put("response_msg",response_msg);
		
		
		return  resultMap;
	}
	
	/**
	 * 데이터 제약 체크 
	 * @param commonVO
	 * @return
	 * @throws Exception
	 */
	public void chkData(CommonVO commonVO) throws Exception {
		logger.info("****** PaymentServiceImpl.chkData ******");
		String code = "0000";
		String msg = "";
		
		//데이터 조회 시 관리번호 체크
		if(commonVO.getTransDv().equals("")) {
			if(commonVO.getUniqueId().equals("")) {
				code = "0009";
				msg = "데이터조회 시 관리번호는 필수입니다.";
			}
			if(commonVO.getUniqueId().length() != 20) {
				code = "0010";
				msg = "유효하지않은 관리번호 입니다.";
			}
		}else {
			//카드번호 자리수 체크 
			if(10 > (int)(Math.log10(commonVO.getCardNo())+1) || 20 < (int)(Math.log10(commonVO.getCardNo())+1)) {
				code = "0001";
				msg = "카드번호는 최소10자리, 최대20자리여야 합니다.";
			}
			
			//결제 거래의 경우 
			if(commonVO.getTransDv().equals("PAYMENT")){
				//거래금액 체크  
				if(100 > commonVO.getTransAmt()) {
					code = "0002";
					msg = "결제금액은 100원 이상이여야 합니다.";
				}
				if(1000000000 < commonVO.getTransAmt()) {
					code = "0007";
					msg = "결제금액은 10억원 이하여야 합니다.";
				}
				//부가가치세 체크  
				if(commonVO.getValAddTax() > commonVO.getTransAmt()) {
					code = "0003";
					msg = "부가가치세는 거래금액보다 작아야합니다.";
				}
				//원거래관리번호 
				if(!commonVO.getOrgUniqueId().equals("")) {
					code = "0006";
					msg = "결제 시에는 원거래관리번호가 공백이여야 합니다.";
				}
			}
			//취소 거래의 경우 
			else if(commonVO.getTransDv().equals("CANCEL")){
				//거래금액 체크  
				if(commonVO.getTransAmt() > commonVO.getPayAmt()) {
					code = "0004";
					msg = "결제 금액보다 작아야합니다.";
				}
				//부가가치세 체크   
				if(commonVO.getValAddTax() > commonVO.getTransAmt()) {
					code = "0005";
					msg = "부가가치세는 거래금액보다 작아야합니다.";
				}
				if(commonVO.getValAddTax() > commonVO.getPayValAddTax()) {
					code = "0008";
					msg = "입력한 부가가치세가 결제상태인 부가가치세보다 큽니다.";
				}
			}
		}
		commonVO.setResponseCode(code);
		commonVO.setResponseMsg(msg);
	}

}
