package com.yem.payapi.common.vo;
 
public class CommonVO {

    private String unique_id; //������ȣ
    
    private String trans_dv = ""; //�ŷ� ����(PAYMENT:����, CANCEL:���)
    private int card_no; //ī���ȣ 
    private int exp_dt; //��ȿ�Ⱓ
    private int cvc; //cvc
    private String instm_month; //�Һΰ�����
    private int trans_amt; //�ŷ��ݾ�
    private int val_add_tax; //�ΰ���ġ�� 
    private String org_unique_id; //���ŷ�������ȣ 
    
    private String string_data = ""; //string������
    private String ctt; //���
    private String reg_dtm; //����Ͻ�
    private String reg_usr; //�����
    
    private String success_yn; //�ŷ���������  (Y:����,N:����)
    private String status; // ������� (����(0),�κ����(1),��ü���(2))
    private int pay_amt; //��������� �ݾ� 
    private int pay_val_add_tax; //���������� �ΰ���ġ��
    
    private String response_code; //�����ڵ� 
    private String response_msg; //����޼��� 
    
    /**
     * ������ȣ 
     * @return unique_id
     */
    public String getUniqueId() {
        return unique_id;
    }
    public void setUniqueId(String unique_id) {
        this.unique_id = unique_id;
    }
    
    /**
     * �ŷ� ����(PAYMENT:����, CANCEL:���) 
     * @return trans_dv
     */
    public String getTransDv() {
        return trans_dv;
    }
    public void setTransDv(String trans_dv) {
        this.trans_dv = trans_dv;
    }
    /**
     * ī���ȣ 
     * @return card_no
     */
    public int getCardNo() {
        return card_no;
    }
    public void setCardNo(int card_no) {
        this.card_no = card_no;
    }
    
    /**
     * ��ȿ�Ⱓ
     * @return exp_dt
     */
    public int getExpDt() {
        return exp_dt;
    }
    public void setExpDt(int exp_dt) {
        this.exp_dt = exp_dt;
    }
    
    /**
     * cvc
     * @return cvc
     */
    public int getCvc() {
        return cvc;
    }
    public void setCvc(int cvc) {
        this.cvc = cvc;
    }
    
    /***
     * �Һΰ�����
     * @return instm_month
     */
    public String getInstmMonth() {
        return instm_month;
    }
    public void setInstmMonth(String instm_month) {
        this.instm_month = instm_month;
    }
    
    /**
     * �ŷ��ݾ�
     * @return trans_amt
     */
    public int getTransAmt() {
        return trans_amt;
    }
    public void setTransAmt(int trans_amt) {
        this.trans_amt = trans_amt;
    }
    
    /**
     * �ΰ���ġ��
     * @return val_add_tax
     */
    public int getValAddTax() {
        return val_add_tax;
    }
    public void setValAddTax(int val_add_tax) {
        this.val_add_tax = val_add_tax;
    }
    
    /**
     * ���ŷ�������ȣ 
     * @return unique_id
     */
    public String getOrgUniqueId() {
        return org_unique_id;
    }
    public void setOrgUniqueId(String org_unique_id) {
        this.org_unique_id = org_unique_id;
    }
    
    /**
     * string ������ 
     * @return string_data
     */
    public String getStringData() {
        return string_data;
    }
    public void setStringData(String string_data) {
        this.string_data = string_data;
    }
    
    /**
     * ���
     * @return ctt
     */
    public String getCtt() {
        return ctt;
    }
    public void setCtt(String ctt) {
        this.ctt = ctt;
    }
    
    /**
     * ����Ͻ�
     * @return reg_dtm
     */
    public String getRegDtm() {
        return reg_dtm;
    }
    public void setRegDtm(String reg_dtm) {
        this.reg_dtm = reg_dtm;
    }
    
    /**
     * �����
     * @return reg_usr
     */
    public String getRegUsr() {
        return reg_usr;
    }
    public void setRegUsr(String reg_usr) {
        this.reg_usr = reg_usr;
    }
    
    
    /**
     * �ŷ��������� 
     * @return success_yn
     */
    public String getSuccessYn() {
        return success_yn;
    }
    public void setSuccessYn(String success_yn) {
        this.success_yn = success_yn;
    }
    
    
    /**
     * ��������
     * @return status
     */
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * ��������� �ݾ� 
     * @return pay_amt
     */
    public int getPayAmt() {
        return pay_amt;
    }
    public void setPayAmt(int pay_amt) {
        this.pay_amt = pay_amt;
    }
    
    /**
     * ���������� �ΰ���ġ��
     * @return pay_val_add_tax
     */
    public int getPayValAddTax() {
        return pay_val_add_tax;
    }
    public void setPayValAddTax(int pay_val_add_tax) {
        this.pay_val_add_tax = pay_val_add_tax;
    }
    
    /**
     * �����ڵ� 
     * @return response_code
     */
    public String getResponseCode() {
        return response_code;
    }
    public void setResponseCode(String response_code) {
        this.response_code = response_code;
    }
    
    /**
     * ����޼���
     * @return response_msg
     */
    public String getResponseMsg() {
        return response_msg;
    }
    public void setResponseMsg(String response_msg) {
        this.response_msg = response_msg;
    }
    
}