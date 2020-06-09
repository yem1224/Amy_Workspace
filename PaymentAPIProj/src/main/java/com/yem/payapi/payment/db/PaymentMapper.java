package com.yem.payapi.payment.db;

import com.yem.payapi.common.vo.CommonVO;

/**
 * ���� ó�� Mapper interface
 * @author ����
 *
 */
public interface PaymentMapper {

	/**
	 * �����ŷ��⺻ ���̺� select
	 * @param commonVO
	 * @return
	 * @throws Exception
	 */
	public CommonVO selectPayTransBase(CommonVO commonVO) throws Exception;
	
	/**
	 * �����ŷ��⺻ ���̺� insert 
	 * @param commonVO
	 * @return
	 * @throws Exception
	 */
	public void insertPayTransBase(CommonVO commonVO) throws Exception;

	/**
	 * ������ȣ �ִ밪 ��ȸ
	 * @return
	 * @throws Exception
	 */
	public String selectMaxUniqueId() throws Exception;
	
	/**
	 * �����ŷ��� ���̺� select
	 * @param commonVO
	 * @return
	 * @throws Exception
	 */
	public CommonVO selectPayTransDtls(CommonVO commonVO) throws Exception;
	
	/**
	 * �����ŷ��� ���̺� insert
	 * @param commonVO
	 * @throws Exception
	 */
	public void insertPayTransDtls(CommonVO commonVO) throws Exception;

	/**
	 * ���ŷ����� ���� update
	 * @param commonVO
	 * @throws Exception
	 */
	public void updatePayTransDtls(CommonVO commonVO) throws Exception;

	/**
	 * ���ŷ��� ��Ұŷ� �ΰ���ġ���� 
	 * @param orgUniqueId
	 * @throws Exception
	 */
	public String selectOrgValAddTax(String orgUniqueId) throws Exception;


}
 
  
