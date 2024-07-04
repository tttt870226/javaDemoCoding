package com.skytech.cppcc.mt.manage.model.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.skytech.seed.core.web.serialization.dict.Dict;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 会议人员表
 *
 * @author gy
 * @since 2024-05-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "会议人员表")
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;
    /**
     * 租户号
     */
    @Schema(description = "租户号")
    private String tenantId;
    /**
     * 删除标记（0正常 1删除）
     */
    @Schema(description = "删除标记（0正常 1删除）")
    private Integer delStatus;
    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUid;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateUid;
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    /**
     * 会议ID
     */
    @Schema(description = "会议ID")
    private String meetingId;
    /**
     * 单位ID
     */
    @Schema(description = "单位ID")
    private String deptId;
    /**
     * 单位名
     */
    @Excel(name = "单位", width = 100)
    @Schema(description = "单位名")
    private String deptName;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String userId;
    /**
     * 人员姓名
     */
    @Excel(name = "姓名", width = 50)
    @Schema(description = "人员姓名")
    private String userName;
    /**
     * 性别（字典项）
     */
    @Excel(name = "性别", width = 20, replace = {"女_0", "男_1", "未知性别_2", "未知性别_3"})
    @Dict(codes = "SEX")
    @Schema(description = "性别（字典项）")
    private Integer sex;
    /**
     * 联系方式
     */
    @Schema(description = "联系方式")
    private String phone;
    /**
     * 会议角色
     */
    @Dict(codes = "t_meeting_user:meeting_role")
    @Schema(description = "会议角色")
    private String meetingRole;
    /**
     * 人员类型0：非委员，1：委员
     */
    @Schema(description = "人员类型 0：非委员，1：委员")
    private String userType;
    /**
     * 照片文件
     */
    @Schema(description = "照片文件")
    private String photo;
    /**
     * 职务
     */
    @Excel(name = "职务", width = 100)
    @Schema(description = "职务")
    private String post;
    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    private String idCard;
    /**
     * 住址
     */
    @Schema(description = "住址")
    private String nativePlace;
    /**
     * 备注记录
     */
    @Schema(description = "备注记录")
    private String remark;
    /**
     * 加入的渠道类型 11 参会通知添加 12手工录入 13系统外录入
     */
    @Schema(description = "加入的渠道类型")
    private Integer joinType;
    /**
     * 类型
     */
    @Schema(description = "类型")
    private Integer type;

    /**
     * 0委员，1常委，2主席会议组成人员
     */
    @Schema(description = "委员类型")
    private String standingCommitteeStatus;

    /**
     * 界别
     */
    @Schema(description = "界别")
    private String subsector;

}
