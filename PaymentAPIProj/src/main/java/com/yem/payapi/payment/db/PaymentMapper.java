package com.yem.payapi.payment.db;

import com.yem.payapi.common.vo.CommonVO;

/**
 * 결제 처리 Mapper interface
 * @author 은미
 *
 */
public interface PaymentMapper {

	/**
	 * 결제거래기본 테이블 select
	 * @param commonVO
	 * @return
	 * @throws Exception
	 */
	public CommonVO selectPayTransBase(CommonVO commonVO) throws Exception;
	
	/**
	 * 결제거래기본 테이블 insert 
	 * @param commonVO
	 * @return
	 * @throws Exception
	 */
	public void insertPayTransBase(CommonVO commonVO) throws Exception;

	/**
	 * 관리번호 최대값 조회
	 * @return
	 * @throws Exception
	 */
	public String selectMaxUniqueId() throws Exception;
	
	/**
	 * 결제거래상세 테이블 select
	 * @param commonVO
	 * @return
	 * @throws Exception
	 */
	public CommonVO selectPayTransDtls(CommonVO commonVO) throws Exception;
	
	/**
	 * 결제거래상세 테이블 insert
	 * @param commonVO
	 * @throws Exception
	 */
	public void insertPayTransDtls(CommonVO commonVO) throws Exception;

	/**
	 * 원거래내역 상태 update
	 * @param commonVO
	 * @throws Exception
	 */
	public void updatePayTransDtls(CommonVO commonVO) throws Exception;

	/**
	 * 원거래의 취소거래 부가가치세합 
	 * @param orgUniqueId
	 * @throws Exception
	 */
	public String selectOrgValAddTax(String orgUniqueId) throws Exception;


}
 
  
