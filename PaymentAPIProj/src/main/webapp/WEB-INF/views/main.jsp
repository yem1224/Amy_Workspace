<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
</head>
<body>
<div>
    <form id="fm" name="fm" method="get">
        <div>
        	<input type="button" value="결제 요청" onclick="location.href='/payapi/payment/paymentRequest.do'">
        	<input type="button" value="결제취소 요청" onclick="location.href='/payapi/payment/paymentCancel.do'">
        	<input type="button" value="데이터 조회" onclick="location.href='/payapi/search/dataSearch.do'">            
        </div>
    </form>
<script>
</script>
</div>
</body>
</html>