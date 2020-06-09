package com.yem.payapi.payment.db;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.yem.payapi.common.vo.CommonVO;
import org.apache.ibatis.session.SqlSession;

import java.sql.*;

/**
 * 결제처리 DAO
 * @author 은미
 *
 */
@Repository
public class PaymentDAO {
	private final String driver ="com.mysql.jdbc.Driver";
	private final String url ="jdbc:mysql://localhost:3306/mysql?serverTimezone=UTC";
	private final String username ="root";
	private final String password="dmsal.1224";
	
	@Autowired
	private SqlSession sqlSession;
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentDAO.class);
	
	/**
	 * 결제거래기본 테이블 select
	 * @param commonVO
	 * @return
	 * @throws Exception
	 */
	public CommonVO selectPayTransBase(CommonVO commonVO) throws Exception {
		// TODO Auto-generated method stub
		logger.info("****** PaymentDAO.selectPayTransBase ******");
    	PaymentMapper mapper = sqlSession.getMapper(PaymentMapper.class);
		return mapper.selectPayTransBase(commonVO);
	}
	
	/**
	 * 결제거래기본 테이블 insert
	 * @param commonVO
	 * @return
	 * @throws Exception
	 */
    public void insertPayTransBase(CommonVO commonVO) throws Exception {
    	// TODO Auto-generated method stub
    	logger.info("****** PaymentDAO.insertPayTransBase ******");
    	PaymentMapper mapper = sqlSession.getMapper(PaymentMapper.class);
    	mapper.insertPayTransBase(commonVO);
    }
    
    /**
     * 관리번호 최대값 조회
     * @return
     * @throws Exception 
     */
	public String selectMaxUniqueId() throws Exception {
		// TODO Auto-generated method stub
		logger.info("****** PaymentDAO.selectMaxUniqueId ******");
    	PaymentMapper mapper = sqlSession.getMapper(PaymentMapper.class);
		return mapper.selectMaxUniqueId();
	}
	
	/**
	 * 결제거래상세 테이블 select
	 * @param commonVO
	 * @return
	 * @throws Exception
	 */
	public CommonVO selectPayTransDtls(CommonVO commonVO) throws Exception {
		// TODO Auto-generated method stub
		logger.info("****** PaymentDAO.selectPayTransDtls ******");
    	PaymentMapper mapper = sqlSession.getMapper(PaymentMapper.class);
		return mapper.selectPayTransDtls(commonVO);
	}

    /**
     * 결제거래상세 테이블 insert 
     * @param commonVO
     * @throws Exception
     */
	public void insertPayTransDtls(CommonVO commonVO) throws Exception {
		// TODO Auto-generated method stub
		logger.info("****** PaymentDAO.insertPayTransDtls ******");
    	PaymentMapper mapper = sqlSession.getMapper(PaymentMapper.class);
        mapper.insertPayTransDtls(commonVO);
	}
	
	/**
	 * 원거래내역 상태 update
	 * @param commonVO
	 * @throws Exception
	 */
	public void updatePayTransDtls(CommonVO commonVO) throws Exception {
		// TODO Auto-generated method stub
		logger.info("****** PaymentDAO.updatePayTransDtls ******");
		PaymentMapper mapper = sqlSession.getMapper(PaymentMapper.class);
		mapper.updatePayTransDtls(commonVO);
	}

	/**
	 * 원거래의 취소거래 부가가치세합 
	 * @param orgUniqueId
	 * @return
	 */
	public String selectOrgValAddTax(String orgUniqueId) throws Exception {
		// TODO Auto-generated method stub
		logger.info("****** PaymentDAO.selectOrgValAddTax ******");
    	PaymentMapper mapper = sqlSession.getMapper(PaymentMapper.class);
		return mapper.selectOrgValAddTax(orgUniqueId);
	}
}
