-- 验证码表建表语句
CREATE TABLE `verification_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `target` varchar(50) NOT NULL COMMENT '目标（手机号或邮箱）',
  `code` varchar(10) NOT NULL COMMENT '验证码',
  `type` tinyint(4) NOT NULL COMMENT '类型（0:手机，1:邮箱）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `expiry_time` datetime NOT NULL COMMENT '过期时间',
  `used` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已使用（0:未使用，1:已使用）',
  PRIMARY KEY (`id`),
  KEY `idx_target` (`target`),
  KEY `idx_expiry_time` (`expiry_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

-- 添加清理过期验证码的存储过程
DELIMITER //
CREATE PROCEDURE `clean_expired_verification_codes`()
BEGIN
    DELETE FROM `verification_code` WHERE `expiry_time` < NOW();
END //
DELIMITER ;

-- 添加定时任务清理过期验证码（每天凌晨3点执行）
-- 注意：需要有EVENT权限才能创建事件调度器
SET GLOBAL event_scheduler = ON;
DELIMITER //
CREATE EVENT IF NOT EXISTS `clean_expired_codes_event`
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_DATE + INTERVAL 3 HOUR
DO
BEGIN
    CALL clean_expired_verification_codes();
END //
DELIMITER ; 