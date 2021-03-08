package com.yem.payapi.common.vo;
 
public class CommonVO {

    private String unique_id; //관리번호
    
    private String trans_dv = ""; //거래 구분(PAYMENT:결제, CANCEL:취소)
    private String card_no; //카드번호 
    private String exp_dt; //유효기간
    private String cvc; //cvc
    private String instm_month; //할부개월수
    private String trans_amt; //거래금액
    private String val_add_tax; //부가가치세 
    private String org_unique_id; //원거래관리번호 
    private String org_val_add_tax; //원거래부가가치세
    
    private String string_data = ""; //string데이터
    private String ctt; //비고
    private String reg_dtm; //등록일시
    private String reg_usr; //등록자
    
    private int trans_seq; //거래일련번호
    private String success_yn; //거래성공여부  (Y:성공,N:실패)
    private String status; // 결재상태 (결제(0),부분취소(1),전체취소(2))
    private String pay_amt; //결재상태인 금액 
    private String pay_val_add_tax; //결제상태인 부가가치세
    
    private String response_code; //응답코드 
    private String response_msg; //응답메세지 
    
    /**
     * 관리번호 
     * @return unique_id
     */
    public String getUniqueId() {
        return unique_id;
    }
    public void setUniqueId(String unique_id) {
        this.unique_id = unique_id;
    }
    
    /**
     * 거래 구분(PAYMENT:결제, CANCEL:취소) 
     * @return trans_dv
     */
    public String getTransDv() {
        return trans_dv;
    }
    public void setTransDv(String trans_dv) {
        this.trans_dv = trans_dv;
    }
    /**
     * 카드번호 
     * @return card_no
     */
    public String getCardNo() {
        return card_no;
    }
    public void setCardNo(String card_no) {
        this.card_no = card_no;
    }
    
    /**
     * 유효기간
     * @return exp_dt
     */
    public String getExpDt() {
        return exp_dt;
    }
    public void setExpDt(String exp_dt) {
        this.exp_dt = exp_dt;
    }
    
    /**
     * cvc
     * @return cvc
     */
    public String getCvc() {
        return cvc;
    }
    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
    
    /***
     * 할부개월수
     * @return instm_month
     */
    public String getInstmMonth() {
        return instm_month;
    }
    public void setInstmMonth(String instm_month) {
        this.instm_month = instm_month;
    }
    
    /**
     * 거래금액
     * @return trans_amt
     */
    public String getTransAmt() {
        return trans_amt;
    }
    public void setTransAmt(String trans_amt) {
        this.trans_amt = trans_amt;
    }
    
    /**
     * 부가가치세
     * @return val_add_tax
     */
    public String getValAddTax() {
        return val_add_tax;
    }
    public void setValAddTax(String val_add_tax) {
        this.val_add_tax = val_add_tax;
    }
    
    /**
     * 원거래관리번호 
     * @return unique_id
     */
    public String getOrgUniqueId() {
        return org_unique_id;
    }
    public void setOrgUniqueId(String org_unique_id) {
        this.org_unique_id = org_unique_id;
    }
    
    /**
     * string 데이터 
     * @return string_data
     */
    public String getStringData() {
        return string_data;
    }
    public void setStringData(String string_data) {
        this.string_data = string_data;
    }
    
    /**
     * 비고
     * @return ctt
     */
    public String getCtt() {
        return ctt;
    }
    public void setCtt(String ctt) {
        this.ctt = ctt;
    }
    
    /**
     * 등록일시
     * @return reg_dtm
     */
    public String getRegDtm() {
        return reg_dtm;
    }
    public void setRegDtm(String reg_dtm) {
        this.reg_dtm = reg_dtm;
    }
    
    /**
     * 등록자
     * @return reg_usr
     */
    public String getRegUsr() {
        return reg_usr;
    }
    public void setRegUsr(String reg_usr) {
        this.reg_usr = reg_usr;
    }
    
    /**
     * 거래일련번호
     * @return  tran_seq
     */
    public int getTransSeq() {
		return trans_seq;
	}
	public void setTransSeq(int trans_seq) {
		this.trans_seq = trans_seq;
	}
	
    /**
     * 거래성공여부 
     * @return success_yn
     */
    public String getSuccessYn() {
        return success_yn;
    }
    public void setSuccessYn(String success_yn) {
        this.success_yn = success_yn;
    }
    
    
    /**
     * 결제상태
     * @return status
     */
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * 결재상태인 금액 
     * @return pay_amt
     */
    public String getPayAmt() {
        return pay_amt;
    }
    public void setPayAmt(String pay_amt) {
        this.pay_amt = pay_amt;
    }
    
    /**
     * 결제상태인 부가가치세
     * @return pay_val_add_tax
     */
    public String getPayValAddTax() {
        return pay_val_add_tax;
    }
    public void setPayValAddTax(String pay_val_add_tax) {
        this.pay_val_add_tax = pay_val_add_tax;
    }
    
    /**
     * 응답코드 
     * @return response_code
     */
    public String getResponseCode() {
        return response_code;
    }
    public void setResponseCode(String response_code) {
        this.response_code = response_code;
    }
    
    /**
     * 응답메세지
     * @return response_msg
     */
    public String getResponseMsg() {
        return response_msg;
    }
    public void setResponseMsg(String response_msg) {
        this.response_msg = response_msg;
    }
    
    /**
     * 원거래부가가치세 
     * @return org_val_add_tax
     */
	public String getOrgValAddTax() {
		return org_val_add_tax;
	}
	public void setOrgValAddTax(String org_val_add_tax) {
		this.org_val_add_tax = org_val_add_tax;
	}
    
}
