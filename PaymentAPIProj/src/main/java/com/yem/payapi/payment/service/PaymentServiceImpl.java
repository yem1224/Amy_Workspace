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
	@Transactional(rollbackFor=Exception.class)
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
		
		/******* REQUEST VALUES값 세팅  *******/
		commonVO.setTransDv(transDv); 
		commonVO.setCardNo(cardNo);
		commonVO.setExpDt(expDt);
		commonVO.setCvc(cvc);
		commonVO.setInstmMonth(String.format("%02d", Long.parseLong(instmMonth)));
		commonVO.setTransAmt(transAmt);
		commonVO.setValAddTax(valAddTax);
		
		/******* 데이터 제약 체크 *******/
		chkData(commonVO);
		response_code = commonVO.getResponseCode();
		response_msg = commonVO.getResponseMsg();
		
		if(response_code.equals("0000")) { //성공시 
			
			/******* 관리번호 채번 *******/
			uniqueId = makeUniqueId();
			
			/******* 카드정보 암호화 *******/ // 
			AES256Util aes256Util = new AES256Util();
			String encryptCardInfo = aes256Util.encrypt(cardNo+"|"+expDt+"|"+cvc);
			logger.info("카드정보 암호화 데이터 ::::: "+encryptCardInfo);
			
			/******* 부가가치세 계산 *******/
			valAddTax = calValAddTax(transAmt, valAddTax,"0","0");
			
			/******* string데이터 생성 *******/ 
			stringData 
			= String.format("%-10s", transDv) //데이터구분 
			+ String.format("%20s", uniqueId) //관리번호
			+ String.format("%-20s", cardNo) //카드번호 
			+ String.format("%02d", Long.parseLong(instmMonth)) //할부개월수
			+ String.format("%-4s", expDt) //카드유효기간
			+ String.format("%-3s", cvc) //CVC
			+ String.format("%10s", transAmt) //거래금액
			+ String.format("%010d", Long.parseLong(valAddTax)) //부가가치세
			+ String.format("%20s", "") //원거래관리번호 (결제시에는 공백)
			+ String.format("%-300s", encryptCardInfo) //암호화된카드정보
			+ String.format("%-47s", "") //예비필드 
		    ;	
			//데이터 길이 추가 
			stringData = String.format("%4s", stringData.length()) + stringData;
			logger.info("카드사에 전송될  stringData ::::: " + stringData);
			
			/******* 공통 VO 세팅  *******/
			commonVO.setUniqueId(uniqueId);
			commonVO.setValAddTax(valAddTax);
			commonVO.setPayAmt(transAmt);
			commonVO.setPayValAddTax(valAddTax);
			commonVO.setOrgUniqueId("");
			commonVO.setStringData(stringData);
			commonVO.setRegUsr(regUsr);
			commonVO.setStatus("0"); // 결제상태( 0: 결제 ) 
			commonVO.setSuccessYn("Y");
			
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
			//obj.put("optional",uniqueIdObj);
			
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
		String orgUniqueId = requestMap.get("unique_id").toString(); //원거래관리번호  
		
		/******* RESPONSE VALUES *******/
		String response_code = ""; //응답코드 
		String response_msg = ""; //응답메세지 
		
		/******* 원거래정보 조회 *******/
		try {
			commonVO.setUniqueId(orgUniqueId);
			commonVO = paymentDAO.selectPayTransDtls(commonVO); 
			logger.info("원거래정보 조회 [거래일련번호  ::::: "+commonVO.getTransSeq()+" ]");
			logger.info("원거래정보 조회 [관리번호  ::::: "+orgUniqueId+" ]");
			logger.info("원거래정보 조회 [카드번호  ::::: "+commonVO.getCardNo()+" ]");
			logger.info("원거래정보 조회 [유효기간  ::::: "+commonVO.getExpDt()+" ]");
			logger.info("원거래정보 조회 [cvc  ::::: "+commonVO.getCvc()+" ]");
			logger.info("원거래정보 조회 [결제 상태  ::::: "+commonVO.getStatus()+" ]");
			logger.info("원거래정보 조회 [부가가치세 ::::: "+commonVO.getValAddTax()+" ]");
			logger.info("원거래정보 조회 [결제상태의 금액 ::::: "+commonVO.getPayAmt()+" ]");
			logger.info("원거래정보 조회 [결제상태의 부가가치세::: "+commonVO.getPayValAddTax()+" ]");
			commonVO.setOrgValAddTax(commonVO.getValAddTax()); //취소 시 부가가치세 체크를 위함 
		}
		catch (Exception e) {
			// TODO: handle exception
			resultMap.put("response_code","9999");
			resultMap.put("response_msg","관리번호에 해당하는 결제내역이 없습니다.");
			return  resultMap;
		}
		
		/******* REQUEST VALUES값 세팅  *******/ 
		String transDv = "CANCEL";
		String transAmt = requestMap.get("cancel_amt").toString(); //거래금액(취소금액)
		String valAddTax = requestMap.get("val_add_tax").toString(); //부가가치세 
		String ctt = requestMap.get("ctt").toString(); //취소사유 
		String regUsr = "YEOM"; //등록자

		commonVO.setTransDv(transDv); 
		commonVO.setTransAmt(transAmt);
		commonVO.setCtt(ctt);
		commonVO.setOrgUniqueId(orgUniqueId);
		commonVO.setRegUsr(regUsr);
		
		/******* 부가가치세 계산 *******/
		valAddTax = calValAddTax(transAmt, valAddTax,commonVO.getPayAmt(),commonVO.getPayValAddTax());
		commonVO.setValAddTax(valAddTax);

		/******* 데이터 제약 체크 *******/
		chkData(commonVO);
		response_code = commonVO.getResponseCode();
		response_msg = commonVO.getResponseMsg();
		
		if(response_code.equals("0000")) {
			/*******관리번호 채번 *******/
			String uniqueId = makeUniqueId();
			
			/******* 카드정보 암호화 *******/
			String cardNo = commonVO.getCardNo();
			String expDt = commonVO.getExpDt();
			String cvc = commonVO.getCvc();
			AES256Util aes256Util = new AES256Util();
			String encryptCardInfo = aes256Util.encrypt(cardNo+"|"+expDt+"|"+cvc);
			logger.info("카드정보 암호화 데이터 ::::: "+encryptCardInfo);
			
			
			/******* string데이터 생성 *******/ 
			String stringData
			= String.format("%-10s", transDv) //데이터구분 
			+ uniqueId //관리번호
			+ String.format("%-20s", cardNo) //카드번호 (원데이터와 동일)
			+ "00" //할부개월수 (취소시에는 일시불"00"으로 저장)
			+ String.format("%-4s", expDt) //카드유효기간 (원데이터와 동일)
			+ String.format("%-3s", cvc) //CVC (원데이터와 동일)
			+ String.format("%10s", transAmt) //거래금액
			+ String.format("%010d", Long.parseLong(valAddTax)) //부가가치세
			+ String.format("%-20s",orgUniqueId) //원거래관리번호 (결제시에는 공백)
			+ String.format("%-300s", encryptCardInfo) //암호화된카드정보
			+ String.format("%-47s", "") //예비필드 
		    ;	
			//데이터 길이 추가 
			stringData = String.format("%4s", stringData.length()) + stringData;
			logger.info("카드사에 전송될 stringData :::::" + stringData);
			
			/******* 부분취소를 위한 금액 계산 *******/
			long payAmt = Long.parseLong(commonVO.getPayAmt())-Long.parseLong(transAmt);
			long payValAddTax = Long.parseLong(commonVO.getPayValAddTax())-Long.parseLong(valAddTax);
			
			/******* 공통 VO에 세팅 *******/ 
			commonVO.setUniqueId(uniqueId);
			commonVO.setTransDv(transDv); 
			commonVO.setInstmMonth("00");
			commonVO.setTransAmt(transAmt);
			commonVO.setValAddTax(valAddTax);
			commonVO.setStringData(stringData);
			commonVO.setSuccessYn("Y");
			commonVO.setPayAmt(Long.toString(payAmt));
			commonVO.setPayValAddTax(Long.toString(payValAddTax));
			if(payAmt == 0) { //모두 취소했을 경우 
				commonVO.setStatus("2");
			}else { //부분 취소의 경우 
				commonVO.setStatus("1");
			}
			
			/******* 거래기본 테이블 insert *******/
			paymentDAO.insertPayTransBase(commonVO);
			/******* 거래내역 테이블 insert *******/
			paymentDAO.insertPayTransDtls(commonVO);
			/******* 원거래내역 상태 update *******/
			paymentDAO.updatePayTransDtls(commonVO);
			
			/******* json format으로 출력  *******/
			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();
			JSONObject uniqueIdObj = new JSONObject();
			
			uniqueIdObj.put("관리번호", uniqueId);
			obj.put("required",uniqueIdObj);
			//obj.put("optional",uniqueIdObj);
			
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
	@Transactional(rollbackFor=Exception.class)
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
		
		/******* REQUEST VALUES 세팅  *******/
		commonVO.setUniqueId(uniqueId);

		/******* 데이터 제약 체크 *******/
		chkData(commonVO);
		response_code = commonVO.getResponseCode();
		response_msg = commonVO.getResponseMsg();
		
		if(response_code.equals("0000")) {
			
			/******* string데이터 조회  *******/
			try {
				commonVO = paymentDAO.selectPayTransBase(commonVO);
				stringData = commonVO.getStringData();
				logger.info("카드사에 전송될 stringData :::::" + stringData);
			}
			catch (NullPointerException e) {
				// TODO: handle exception
				resultMap.put("response_code","9999");
				resultMap.put("response_msg","관리번호에 해당하는 결제내역이 없습니다.");
				return  resultMap;
			}
			
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
			amtInfoObj.put("부가가치세", Long.parseLong(valAddTax));
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
	@Transactional(rollbackFor=Exception.class)
	public void chkData(CommonVO commonVO) throws Exception {
		logger.info("****** PaymentServiceImpl.chkData ******");
		String code = "0000";
		String msg = "";
		
		/******* 체크할 값들  *******/
		String chkTransDv= commonVO.getTransDv(); //거래구분 
		String chkUniqueId= commonVO.getUniqueId(); //관리번호
		String chkCardNo=  commonVO.getCardNo(); //카드번호 
		String chkExpDt=  commonVO.getExpDt(); //유효기간 
		String chkCvc=  commonVO.getCvc(); //cvc 
		String chkOrgUniqueId= commonVO.getOrgUniqueId(); //원거래 관리번호 
		String chkInstmMonth=  commonVO.getInstmMonth(); //할부개월수 
		String chkTransAmt= commonVO.getTransAmt(); //거래금액
		String chkValAddTax= commonVO.getValAddTax(); //거래부가가치세
		String chkPayAmt= commonVO.getPayAmt(); //결제상태인 거래금액
		String chkPayValAddTax= commonVO.getPayValAddTax(); //결제상태인 부가가치세
		String chkOrgValAddTax= commonVO.getOrgValAddTax(); //원거래 부가가치세
		String chkCtt= commonVO.getCtt(); //내용

		//데이터 조회  시 관리번호 체크
		if(chkTransDv.equals("")) {
			if(chkUniqueId.equals("")) {
				code = "0009";
				msg = "관리번호를 입력해주세요.";
			}
			else if(chkUniqueId.length() != 20) {
				code = "0010";
				msg = "유효하지않은 관리번호 입니다.";
			}
		}else {
			//결제 거래의 경우 
			if(chkTransDv.equals("PAYMENT")){
				//카드번호 자리수 체크 
				if(10 >chkCardNo.length() || 20 < chkCardNo.length()) {
					code = "0001";
					msg = "카드번호는 최소10자리, 최대20자리여야 합니다.";
				}
				//유효기간 체크
				else if(chkExpDt.length() != 4) {
					code = "0006";
					msg = "유효기간은 월(2자리),년도(2자리)로 입력해주세요.";
				}
				else if(Integer.parseInt(chkExpDt.substring(0, 2)) > 12 || Integer.parseInt(chkExpDt.substring(0, 2)) < 1 ) {
					code = "0016";
					msg = "유효기간 월은 01~12로 입력해주세요.";
				}
				//cvc체크
				else if(chkCvc.length() > 3) {
					code = "0013";
					msg = "CVC는 3자리로 입력해주세요.";
				}
				//할수개월수 체크 
				else if(0 > Integer.parseInt(chkInstmMonth)|| 12 < Integer.parseInt(chkInstmMonth) || 1==Integer.parseInt(chkInstmMonth)) {
					code = "0012";
					msg = "할부개월수는 2~12 로 입력해주세요.";
				}
				//거래금액 체크  
				else if(100 > Long.parseLong(chkTransAmt) ) {
					code = "0002";
					msg = "결제금액은 100원 이상이여야 합니다.";
				}
				else if(1000000000 < Long.parseLong(chkTransAmt) ) {
					code = "0007";
					msg = "결제금액은 10억원 이하여야 합니다.";
				}
				//부가가치세 체크  
				else if( !chkValAddTax.equals("") && Long.parseLong(chkValAddTax) > Long.parseLong(chkTransAmt) ) {
					code = "0003";
					msg = "부가가치세는 거래금액보다 작아야합니다.";
				}
			}
			//취소 거래의 경우 
			else if(chkTransDv.equals("CANCEL")){
				if(chkUniqueId.equals("")) {
					code = "0009";
					msg = "관리번호를 입력해주세요.";
				}
				else if(chkUniqueId.length() != 20) {
					code = "0010";
					msg = "유효하지않은 관리번호 입니다.";
				}
				//결제상태인 금액있는지 체크 
				else if(Long.parseLong(chkPayAmt) == 0) {
					code = "0011";
					msg = "결제 금액이 없습니다.";
				}
				//거래금액 체크  
				else if( Long.parseLong(chkTransAmt) > Long.parseLong(chkPayAmt) ) {
					code = "0004";
					msg = "결제 금액보다 작아야합니다.\n[결제상태인 금액: "+chkPayAmt+"]";
				}
				//부가가치세 체크
				else if( !chkValAddTax.equals("") && Long.parseLong(chkValAddTax) > Long.parseLong(chkTransAmt) ) {
					code = "0005";
					msg = "부가가치세는 거래금액보다 작아야합니다.";
				}
				else if( !chkValAddTax.equals("") && Long.parseLong(chkValAddTax) > Long.parseLong(chkPayValAddTax) ) {
					code = "0008";
					msg = "입력한 부가가치세가 결제상태인 부가가치세보다 큽니다.\n[결제상태인 부가가치세 : "+chkPayValAddTax+"]";
				}
				//전체 취소의 경우, 원거래 금액의 부가가치세와 총 취소금액의 부가가치세의 합과 같아야함.
				else if(Long.parseLong(chkPayAmt)- Long.parseLong(chkTransAmt) == 0) { 
					String valAddTaxSum = paymentDAO.selectOrgValAddTax(chkOrgUniqueId);
					if((Double.parseDouble(valAddTaxSum)+Double.parseDouble(chkValAddTax)) != Double.parseDouble(chkOrgValAddTax)) {
						code = "0014";
						msg = "취소의 경우, 원 거래 금액의 부가가치세와 총 취소금액의 부가가치세의 합과 같아야 합니다.";
					}
				}
				else if(!chkCtt.equals("") && chkCtt.length() > 50) {
					code = "0015";
					msg = "사유는 50자 내로 입력해주세요.";
				}
			}
			
		}
		commonVO.setResponseCode(code);
		commonVO.setResponseMsg(msg);
	}
	
	
	/**
	 * 관리번호 채번 
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public String makeUniqueId() throws Exception {
		String id = "";
		String maxUniqueId = paymentDAO.selectMaxUniqueId();
		if(maxUniqueId.equals("00000000000000000000")) {
			id  = "00000000000000000001"; //초기값 세팅 
		}else {
			id  = String.format("%020d",Long.parseLong(maxUniqueId) + 1);
		} 
		logger.info("관리번호 채번  ::::: "+id );
		return id;
	}
	
	/**
	 * 부가가치세 계산 및 검증 
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public String calValAddTax(String amt, String valAddTax,String payAmt, String payValAddTax) throws Exception {
		String tax = "";
		//값을 받지 않은 경우, 자동계산 
		if(valAddTax.equals("")) {  
			if(Long.parseLong(payAmt) == Long.parseLong(amt)) { //전체취소의 경우 
				tax = payValAddTax; //부가가치세를 남아있는 금액으로 세팅 
			}else {
				int iValAddTax = (int) Math.round(Double.parseDouble(amt)/11); //소수점이하 반올림 
				tax = Integer.toString(iValAddTax);
			}
		}else {
			tax = valAddTax; 
		}
		logger.info("부가가치세 계산 ::::: "+tax);
		return tax;
	}
	
}
