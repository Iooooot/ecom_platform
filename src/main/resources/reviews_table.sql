-- 创建商品评价表
CREATE TABLE `reviews` (
  `review_id` varchar(36) NOT NULL COMMENT '评价ID',
  `product_id` varchar(36) NOT NULL COMMENT '商品ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `rating` int(11) NOT NULL COMMENT '评分(1-5)',
  `content` text COMMENT '评价内容',
  `images` json DEFAULT NULL COMMENT '评价图片URL列表',
  `additional_review` text DEFAULT NULL COMMENT '追评内容',
  `additional_review_time` datetime DEFAULT NULL COMMENT '追评时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`review_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- 测试数据
INSERT INTO `reviews` (`review_id`, `product_id`, `user_id`, `rating`, `content`, `images`, `additional_review`, `additional_review_time`, `create_time`, `update_time`)
VALUES
-- 手机类产品评价
('r-001', 'p-001', '1', 5, '这款手机太棒了！屏幕清晰，系统流畅，拍照效果也很好。', JSON_ARRAY('/images/reviews/r001-1.jpg', '/images/reviews/r001-2.jpg'), '使用一周后依然很满意，电池续航特别给力！', '2023-11-10 15:30:00', '2023-11-01 14:20:00', '2023-11-10 15:30:00'),
('r-002', 'p-001', '2', 4, '整体感觉不错，就是价格有点贵。相机功能很强大。', NULL, NULL, NULL, '2023-11-02 09:15:00', '2023-11-02 09:15:00'),
('r-003', 'p-001', '3', 2, '收到的手机有划痕，客服态度也不好。性能倒是可以。', JSON_ARRAY('/images/reviews/r003-1.jpg'), NULL, NULL, '2023-11-03 16:45:00', '2023-11-03 16:45:00'),
('r-004', 'p-002', '1', 5, '这款平板电脑性价比非常高，画面细腻，运行速度快。', JSON_ARRAY('/images/reviews/r004-1.jpg', '/images/reviews/r004-2.jpg'), NULL, NULL, '2023-11-04 11:30:00', '2023-11-04 11:30:00'),

-- 服装类产品评价
('r-005', 'p-101', '4', 5, '衣服质量很好，款式也很时尚，穿起来很舒适。', JSON_ARRAY('/images/reviews/r005-1.jpg', '/images/reviews/r005-2.jpg'), '洗过几次衣服还是很挺括，没有缩水变形，真的很满意！', '2023-11-20 14:22:00', '2023-11-05 10:20:00', '2023-11-20 14:22:00'),
('r-006', 'p-101', '5', 3, '尺寸偏小，建议购买大一码的。材质还可以。', NULL, NULL, NULL, '2023-11-06 13:40:00', '2023-11-06 13:40:00'),
('r-007', 'p-102', '6', 4, '这条牛仔裤很合身，做工精细，就是有点偏硬。', JSON_ARRAY('/images/reviews/r007-1.jpg'), NULL, NULL, '2023-11-07 17:25:00', '2023-11-07 17:25:00'),

-- 食品类产品评价
('r-008', 'p-201', '7', 5, '这个零食太好吃了，包装也很精美，很适合送礼。', JSON_ARRAY('/images/reviews/r008-1.jpg'), '朋友收到非常喜欢，问我在哪买的，我又给他推荐了一份。', '2023-11-25 09:15:00', '2023-11-08 20:10:00', '2023-11-25 09:15:00'),
('r-009', 'p-201', '8', 2, '口味一般，而且保质期很短，收到时只有一个月了。', NULL, NULL, NULL, '2023-11-09 08:50:00', '2023-11-09 08:50:00'),
('r-010', 'p-202', '9', 4, '这款茶叶香气浓郁，泡出来的颜色很好看，就是价格有点贵。', JSON_ARRAY('/images/reviews/r010-1.jpg', '/images/reviews/r010-2.jpg'), NULL, NULL, '2023-11-10 15:35:00', '2023-11-10 15:35:00'),

-- 家电类产品评价
('r-011', 'p-301', '10', 5, '这款冰箱制冷效果好，噪音小，空间也很大，很满意。', JSON_ARRAY('/images/reviews/r011-1.jpg'), '使用一个月后发现省电效果也很好，电费比之前少了不少。', '2023-12-01 16:40:00', '2023-11-11 14:30:00', '2023-12-01 16:40:00'),
('r-012', 'p-301', '1', 3, '冰箱不错，但送货安装人员态度很差，还要额外收费。', NULL, NULL, NULL, '2023-11-12 09:20:00', '2023-11-12 09:20:00'),
('r-013', 'p-302', '2', 4, '这款洗衣机洗得很干净，甩干效果也好，就是有点小。', JSON_ARRAY('/images/reviews/r013-1.jpg'), NULL, NULL, '2023-11-13 16:15:00', '2023-11-13 16:15:00'),

-- 图书类产品评价
('r-014', 'p-401', '3', 5, '内容非常精彩，印刷质量也很好，值得一读。', JSON_ARRAY('/images/reviews/r014-1.jpg'), '二刷更有感触，发现了很多第一次没注意到的细节，推荐！', '2023-12-05 20:30:00', '2023-11-14 21:50:00', '2023-12-05 20:30:00'),
('r-015', 'p-401', '4', 4, '故事情节紧凑，人物塑造生动，就是结局有点仓促。', NULL, NULL, NULL, '2023-11-15 18:45:00', '2023-11-15 18:45:00');

CREATE TABLE `review_likes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `review_id` varchar(36) NOT NULL COMMENT '评价ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_review_user` (`review_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价点赞表';

-- 插入一些测试数据
INSERT INTO `review_likes` (`review_id`, `user_id`, `create_time`) VALUES
('r001', '2', DATE_SUB(NOW(), INTERVAL 29 DAY)),
('r001', '3', DATE_SUB(NOW(), INTERVAL 28 DAY)),
('r001', '4', DATE_SUB(NOW(), INTERVAL 27 DAY)),
('r002', '1', DATE_SUB(NOW(), INTERVAL 24 DAY)),
('r002', '3', DATE_SUB(NOW(), INTERVAL 23 DAY)),
('r003', '1', DATE_SUB(NOW(), INTERVAL 19 DAY)),
('r003', '2', DATE_SUB(NOW(), INTERVAL 18 DAY)),
('r004', '1', DATE_SUB(NOW(), INTERVAL 14 DAY)),
('r004', '2', DATE_SUB(NOW(), INTERVAL 13 DAY)),
('r004', '3', DATE_SUB(NOW(), INTERVAL 12 DAY)); 