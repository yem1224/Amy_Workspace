<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="//code.jquery.com/jquery-3.3.1.min.js"></script>
<script>
/**
 * 입력 데이터 체크  
 */
 function fn_chkInput(){
	var cardNo = $('#card_no').val();
	var expDt  = $('#exp_dt').val();
	var cvc  = $('#cvc').val();
	var instmMonth  = $('#instm_month').val();
	var transAmt  = $('#trans_amt').val();
	
	if(cardNo=='' || expDt=='' || cvc=='' || instmMonth=='' || transAmt==''){
		alert('결제 정보를 모두 입력하여 주세요.');
		return false;
	}
	if(isNaN(cardNo) || isNaN(expDt) || isNaN(cvc) || isNaN(transAmt)){
		alert('[카드번호/유효기간/cvc/결제금액] 정보는 숫자로 입력해주세요.');
		return false;
	}
	return true;
}
 
/**
 * 결제 요청 
 */
  function fn_request() {
	//입력 데이터 체크  
	if(fn_chkInput()){
		$.ajax({
	    	url: "../payment/doPaymentRequest.do",
	    	type : "POST",
	    	dataType : "json",
	    	data : JSON.stringify({ card_no : $('#card_no').val() //카드번호 
	    		                  , exp_dt : $('#exp_dt').val() //유효기간
	    		                  , cvc : $('#cvc').val()  //cvc
	    		                  , instm_month : $('#instm_month').val() //할부개월수
	    		                  , trans_amt : $('#trans_amt').val() //결제금액
	    		                  , val_add_tax : $('#val_add_tax').val() //부가가치세 
	    		                 }),
	    	contentType : "application/json; charset=UTF-8",
	    	
	    	//호출 성공 시 
	    	success:function(resData) {
	    		if(resData.response_code == "0000"){
	    			$('#response').val(resData.result);
	    		}else{
	    			alert("[에러코드: "+resData.response_code+"]"+resData.response_msg);
	    			return;
	    		}
	    	}
	    });
	}
  }

</script>
<title>결제 요청 화면</title>
</head>
<body>
	<table border=1>
        <tr>
            <td>카드번호</td>
            <td><input type="text" value="" name="card_no" id="card_no" /></td>
        </tr>
        <tr>
            <td>유효기간</td>
            <td><input type="text" value="" name="exp_dt" id="exp_dt" /></td>
        </tr>
        <tr>
        	<td>cvc</td>
        	<td><input type="text" value="" name="cvc" id="cvc" /></td>
        </tr>
        <tr>
        	<td>할부개월수</td>
        	<td><input type="text" value="" name="instm_month" id="instm_month" /></td>
        </tr>
        <tr>
        	<td>결제금액</td>
        	<td><input type="text" value="" name="trans_amt" id="trans_amt" /></td>
        </tr>
        <tr>
        	<td>부가가치세</td>
        	<td><input type="text" value="" name="val_add_tax" id="val_add_tax" /></td>
        </tr>
	</table>
	<br/><br/>
	<input type="button" value="요청" onclick="fn_request();">
	<input type="button" value="메인화면가기" onclick="location.href='../main.do'">
	<br/><br/>
	<textarea id="response" rows="10" cols="40" readonly>결과가 표시됩니다.</textarea>
</body>
</html>