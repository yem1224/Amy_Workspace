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
	var uniqueId = $('#unique_id').val();
	var cancelAmt  = $('#cancel_amt').val();
	var valAddTax  = $('#val_add_tax').val();
	
	if(uniqueId==''){
		alert('관리번호를 입력해주세요.');
		return false;
	}
	if(cancelAmt==''){
		alert('취소금액을 입력해주세요.');
		return false;
	}
	if(isNaN(cancelAmt) || isNaN(valAddTax)){
		alert('[취소금액/부가가치세] 정보는 숫자로 입력해주세요.');
		return false;
	}
	return true;
}
 
/**
 * 결제취소 요청 
 */
  function fn_request() {
	//입력 데이터 체크  
	if(fn_chkInput()){
		$.ajax({
	    	url: "../payment/doPaymentCancel.do",
	    	type : "POST",
	    	dataType : "json",
	    	data : JSON.stringify({ unique_id : $('#unique_id').val() //관리번호 
	    		                  , cancel_amt : $('#cancel_amt').val() //취소금액
	    		                  , val_add_tax : $('#val_add_tax').val() //부가가치세 
	    		                 }),
	    	contentType : "application/json; charset=UTF-8",
	    	
	    	//호출 성공 시 
	    	success:function(resData) {
	    		if(resData.response_code == "0000"){
	    			$('#response').val(resData.result);
	    		}else{
	    			alert(resData.response_msg);
	    			return;
	    		}
	    	}
	    });
	}
  }
  
</script>
<title>결제취소 요청 화면</title>
</head>
<body>
	<table border=1>
        <tr>
            <td>관리번호</td>
            <td><input type="text" value="" name="unique_id" id="unique_id" /></td>
        </tr>
        <tr>
        	<td>취소금액</td>
        	<td><input type="number" value="" name="cancel_amt" id="cancel_amt" /></td>
        </tr>
        <tr>
        	<td>부가가치세</td>
        	<td><input type="number" value="" name="val_add_tax" id="val_add_tax" /></td>
        </tr>
	</table>
	<br/><br/>
	<input type="button" value="취소요청" onclick="fn_request();">
	<input type="button" value="메인화면가기" onclick="fn_main();">
	<br/><br/>
	<textarea id="response" rows="10" cols="40" readonly>결과가 표시됩니다.</textarea>
</body>
</html>