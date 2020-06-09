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
	if(uniqueId==''){
		alert('관리번호를 입력해주세요.');
		return false;
	}
	return true;
}
 
/**
 * 조회
 */
  function fn_search() {
	//입력 데이터 체크  
	if(fn_chkInput()){
		$.ajax({
	    	url: "../payment/doDataSearch.do",
	    	type : "POST",
	    	dataType : "json",
	    	data : JSON.stringify({ unique_id : $('#unique_id').val() //관리번호 
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
<title>데이터조회 화면</title>
</head>
<body>
	<table border=1>
        <tr>
            <td>관리번호</td>
            <td><input type="text" value="" name="unique_id" id="unique_id" /></td>
        </tr>
	</table>
	<br/><br/>
	<input type="button" value="조회" onclick="fn_search();">
	<input type="button" value="메인화면가기" onclick="location.href='../main.do'">
	<br/><br/>
	<textarea id="response" rows="10" cols="40" readonly>결과가 표시됩니다.</textarea>
</body>
</html>