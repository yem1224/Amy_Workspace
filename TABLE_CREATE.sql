-- pay_trans_base 생성 
CREATE TABLE `pay_trans_base` (
  `unique_id` VARCHAR(20) NOT NULL,
  `trans_dv` VARCHAR(10) DEFAULT NULL,
  `card_no` VARCHAR(20) DEFAULT NULL,
  `instm_month` VARCHAR(2) DEFAULT NULL,
  `exp_dt` VARCHAR(4) DEFAULT NULL,
  `cvc` VARCHAR(3) DEFAULT NULL,
  `trans_amt` VARCHAR(10) DEFAULT NULL,
  `val_add_tax` VARCHAR(10) DEFAULT NULL,
  `org_unique_id` VARCHAR(20) DEFAULT NULL,
  `string_data` VARCHAR(500) DEFAULT NULL,
  `ctt` VARCHAR(50) DEFAULT NULL,
  `reg_dtm` DATETIME DEFAULT NULL,
  `reg_usr` VARCHAR(10) DEFAULT NULL,
  PRIMARY KEY (`unique_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4
 ;


-- pay_trans_dtls 생성 
CREATE TABLE `pay_trans_dtls` (
  `trans_seq` INT(20) NOT NULL  AUTO_INCREMENT,
  `unique_id` VARCHAR(20) DEFAULT NULL,
     `success_yn` VARCHAR(1) DEFAULT NULL,
  `trans_dv` VARCHAR(10) DEFAULT NULL,
  `org_unique_id` VARCHAR(20) DEFAULT NULL,
   `status` VARCHAR(1) DEFAULT NULL,
      `trans_amt` VARCHAR(10) DEFAULT NULL,
  `val_add_tax` VARCHAR(10) DEFAULT NULL,
      `pay_amt` VARCHAR(10) DEFAULT NULL,
  `pay_val_add_tax` VARCHAR(10) DEFAULT NULL,
  `ctt` VARCHAR(50) DEFAULT NULL,
  `reg_dtm` DATETIME DEFAULT NULL,
  `reg_usr` VARCHAR(10) DEFAULT NULL,
  primary key (`trans_seq`),
  constraint fk_unique_id foreign key (unique_id) references pay_trans_base (unique_id) on delete cascade
    ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4
 ;