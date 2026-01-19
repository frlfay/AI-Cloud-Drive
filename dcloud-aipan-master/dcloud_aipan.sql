/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80404 (8.4.4)
 Source Host           : localhost:3306
 Source Schema         : dcloud_aipan

 Target Server Type    : MySQL
 Target Server Version : 80404 (8.4.4)
 File Encoding         : 65001

 Date: 25/05/2025 12:33:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户头像',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'COMMON' COMMENT '用户角色 COMMON, ADMIN',
  `del` tinyint DEFAULT '0' COMMENT '逻辑删除（1删除 0未删除）',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_phone_uni` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户信息表';

-- ----------------------------
-- Records of account
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for account_file
-- ----------------------------
DROP TABLE IF EXISTS `account_file`;
CREATE TABLE `account_file` (
  `id` bigint NOT NULL COMMENT 'id',
  `account_id` bigint DEFAULT NULL COMMENT '用户ID',
  `is_dir` int NOT NULL COMMENT '状态 0不是文件夹，1是文件夹',
  `parent_id` bigint DEFAULT NULL COMMENT '上层文件夹ID,顶层文件夹为0',
  `file_id` bigint DEFAULT NULL COMMENT '文件ID，真正存储的文件',
  `file_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件名称',
  `file_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件类型：普通文件common 、压缩文件compress 、  excel  、 word  、 pdf  、 txt  、 图片img  、音频audio  、视频video 、ppt 、源码文件code  、 csv',
  `file_suffix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件的后缀拓展名',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小，字节',
  `del` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除（0未删除，1已删除）',
  `del_time` datetime DEFAULT NULL COMMENT '删除日期',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户文件关联表';

-- ----------------------------
-- Records of account_file
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `id` bigint NOT NULL COMMENT '文件id',
  `account_id` bigint DEFAULT NULL COMMENT '用户id，是哪个用户初次上传的',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件名称，秒传需要用到，冗余存储',
  `file_suffix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件的后缀拓展名，冗余存储',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小，字节，冗余存储',
  `object_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件的key, 格式 日期/md5.拓展名，比如 2024-11-13/921674fd-cdaf-459a-be7b-109469e7050d.png',
  `identifier` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '唯一标识，文件MD5',
  `del` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除（0未删除，1已删除）',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户文件表';

-- ----------------------------
-- Records of file
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for file_chunk
-- ----------------------------
DROP TABLE IF EXISTS `file_chunk`;
CREATE TABLE `file_chunk` (
  `id` bigint NOT NULL,
  `identifier` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件唯一标识（md5）',
  `upload_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分片上传ID',
  `file_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
  `bucket_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属桶名',
  `object_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件的key',
  `total_size` bigint NOT NULL COMMENT '总文件大小（byte）',
  `chunk_size` bigint NOT NULL COMMENT '每个分片大小（byte）',
  `chunk_num` int NOT NULL COMMENT '分片数量',
  `account_id` bigint NOT NULL COMMENT '用户ID',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uq_file_identifier` (`identifier`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='文件分片信息表';

-- ----------------------------
-- Records of file_chunk
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for file_suffix
-- ----------------------------
DROP TABLE IF EXISTS `file_suffix`;
CREATE TABLE `file_suffix` (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_suffix` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件扩展名',
  `file_type_id` int NOT NULL COMMENT '文件类型ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `fk_file_type_id` (`file_type_id`) USING BTREE,
  CONSTRAINT `fk_file_type_id` FOREIGN KEY (`file_type_id`) REFERENCES `file_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='文件分类表';

-- ----------------------------
-- Records of file_suffix
-- ----------------------------
BEGIN;
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (1, 'jpg', 1);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (2, 'jpeg', 1);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (3, 'png', 1);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (4, 'gif', 1);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (5, 'bmp', 1);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (6, 'tiff', 1);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (7, 'svg', 1);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (8, 'ico', 1);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (9, 'webp', 1);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (10, 'heic', 1);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (11, 'psd', 1);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (12, 'ai', 1);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (13, 'mp4', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (14, 'avi', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (15, 'mkv', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (16, 'flv', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (17, 'mov', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (18, 'wmv', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (19, 'mpeg', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (20, 'rmvb', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (21, '3gp', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (22, 'webm', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (23, 'm4v', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (24, 'ts', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (25, 'vob', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (26, 'm2ts', 2);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (27, 'mp3', 3);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (28, 'wav', 3);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (29, 'flac', 3);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (30, 'aac', 3);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (31, 'ogg', 3);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (32, 'wma', 3);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (33, 'm4a', 3);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (34, 'mid', 3);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (35, 'aiff', 3);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (36, 'alac', 3);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (37, 'pcm', 3);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (38, 'doc', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (39, 'docx', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (40, 'pdf', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (41, 'txt', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (42, 'ppt', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (43, 'pptx', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (44, 'xls', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (45, 'xlsx', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (46, 'odt', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (47, 'rtf', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (48, 'csv', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (49, 'md', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (50, 'epub', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (51, 'mobi', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (52, 'tex', 4);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (53, 'zip', 5);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (54, 'rar', 5);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (55, '7z', 5);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (56, 'tar', 5);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (57, 'gz', 5);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (58, 'bz2', 5);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (59, 'xz', 5);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (60, 'iso', 5);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (61, 'z', 5);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (62, 'tgz', 5);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (63, 'dmg', 5);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (64, 'cbr', 5);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (65, 'exe', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (66, 'bat', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (67, 'sh', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (68, 'apk', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (69, 'iso', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (70, 'bin', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (71, 'torrent', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (72, 'bak', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (73, 'dll', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (74, 'deb', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (75, 'rpm', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (76, 'msi', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (77, 'vmdk', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (78, 'vdi', 6);
INSERT INTO `file_suffix` (`id`, `file_suffix`, `file_type_id`) VALUES (79, 'qcow2', 6);
COMMIT;

-- ----------------------------
-- Table structure for file_type
-- ----------------------------
DROP TABLE IF EXISTS `file_type`;
CREATE TABLE `file_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件类型名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='文件类型表';

-- ----------------------------
-- Records of file_type
-- ----------------------------
BEGIN;
INSERT INTO `file_type` (`id`, `file_type_name`) VALUES (1, '图片');
INSERT INTO `file_type` (`id`, `file_type_name`) VALUES (2, '视频');
INSERT INTO `file_type` (`id`, `file_type_name`) VALUES (3, '音频');
INSERT INTO `file_type` (`id`, `file_type_name`) VALUES (4, '文档');
INSERT INTO `file_type` (`id`, `file_type_name`) VALUES (5, '压缩');
INSERT INTO `file_type` (`id`, `file_type_name`) VALUES (6, '其他');
INSERT INTO `file_type` (`id`, `file_type_name`) VALUES (7, '全部');
COMMIT;

-- ----------------------------
-- Table structure for share
-- ----------------------------
DROP TABLE IF EXISTS `share`;
CREATE TABLE `share` (
  `id` bigint NOT NULL COMMENT '分享id',
  `share_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '分享名称',
  `share_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '0' COMMENT '分享类型（no_code没有提取码 ,need_code有提取码）',
  `share_day_type` int NOT NULL DEFAULT '0' COMMENT '分享类型（0 永久有效；1: 7天有效；2: 30天有效）',
  `share_day` int NOT NULL DEFAULT '0' COMMENT '分享有效天数（永久有效为0）',
  `share_end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分享结束时间',
  `share_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '分享链接地址',
  `share_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '分享提取码',
  `share_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '0' COMMENT '分享状态  used正常, expired已失效,  cancled取消',
  `account_id` bigint NOT NULL COMMENT '分享创建人',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_create_user_time` (`account_id`,`gmt_create`) USING BTREE COMMENT '创建人、创建时间唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='用户分享表';

-- ----------------------------
-- Records of share
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for share_file
-- ----------------------------
DROP TABLE IF EXISTS `share_file`;
CREATE TABLE `share_file` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `share_id` bigint NOT NULL COMMENT '分享id',
  `account_file_id` bigint NOT NULL COMMENT '用户文件的ID',
  `account_id` bigint NOT NULL COMMENT '创建者id',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '分享时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='文件分享表';

-- ----------------------------
-- Records of share_file
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for storage
-- ----------------------------
DROP TABLE IF EXISTS `storage`;
CREATE TABLE `storage` (
  `id` bigint NOT NULL,
  `account_id` bigint DEFAULT NULL COMMENT '所属用户',
  `used_size` bigint DEFAULT NULL COMMENT '占用存储大小',
  `total_size` bigint DEFAULT NULL COMMENT '总容量大小，字节存储',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `userid_index` (`account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='存储信息表';

-- ----------------------------
-- Records of storage
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
