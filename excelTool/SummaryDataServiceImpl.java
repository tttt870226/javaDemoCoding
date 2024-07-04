package com.skytech.cppcc.mt.manage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skytech.cppcc.mt.manage.mapper.SummaryDataMapper;
import com.skytech.cppcc.mt.manage.model.dto.*;
import com.skytech.cppcc.mt.manage.model.entity.User;
import com.skytech.cppcc.mt.manage.service.IAttendService;
import com.skytech.cppcc.mt.manage.service.ILeaveService;
import com.skytech.cppcc.mt.manage.service.ISummaryDataService;
import com.skytech.cppcc.mt.manage.service.IUserService;
import com.skytech.cppcc.mt.system.service.FileService;
import com.skytech.cppcc.mt.util.Constant;
import com.skytech.cppcc.mt.util.EasyPoiUtil;
import com.skytech.cppcc.mt.util.ExcelUtil;
import com.skytech.seed.core.security.authenticate.AuthenticationTools;
import com.skytech.seed.core.security.model.Account;
import com.skytech.seed.core.web.wrapper.ResponseModel;
import com.skytech.seed.file.client.model.vo.BusinessVO;
import com.skytech.seed.system.category.model.dto.CategoryItemDTO;
import com.skytech.seed.system.category.service.ICategoryItemService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 汇总服务
 * 顾勇
 */

@Slf4j
@Service
public class SummaryDataServiceImpl implements ISummaryDataService {
    @Resource
    private SummaryDataMapper summaryDataMapper;

    @Resource
    private IUserService userService;

    @Resource
    private IAttendService attendService;

    @Resource
    private ILeaveService leaveService;

    @Resource
    private FileService fileService;

    @Resource
    AuthenticationTools authenticationTools;

    @Resource
    ICategoryItemService categoryItemService;

    /**
     * 分页查询汇总人员表
     *
     * @param page      页
     * @param searchDTO 查询对象
     * @return 结果集
     */
    @Override
    public IPage<SummaryDataDTO> getSummaryUserList(IPage<SummaryDataDTO> page, SummarySearchDTO searchDTO) {
        return summaryDataMapper.getSummaryUserList(page, searchDTO);
    }

    /**
     * 此会议是否配置了汇总 ，默认为false
     *
     * @param meetingId 会议ID
     * @return 结果
     */
    @Override
    public boolean isAnyProcess(String meetingId) {
        return false;
    }

    /**
     * 列表查询汇总人员表
     *
     * @param searchDTO 查询对象
     * @return 结果集
     */
    @Override
    public List<SummaryDataDTO> getStatisticsSummaryUserList(SummarySearchDTO searchDTO) {
        return summaryDataMapper.getStatisticsSummaryUserList(searchDTO);
    }

    /**
     * 新增人员
     *
     * @param summaryHandAddDTO 手工录入的人员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseModel<String> addPerson(SummaryHandAddDTO summaryHandAddDTO) {
        if (StringUtils.isEmpty(summaryHandAddDTO.getMeetingId())) {
            return (new ResponseModel.DefaultBuilder(false)).message("meetingId没有传！").code("99").body("meetingId没有传！");
        }
        if (summaryHandAddDTO.getHandleType() == 1) { //参会
            List<UserDTO> userDTOS = userService.getAndReplaceList(summaryHandAddDTO.getUserIds(), summaryHandAddDTO.getMeetingId(), 12, "1");
            log.info("====>向参会表里添加参会人数据");
            List<String> meetingIds = new ArrayList<>();
            for (UserDTO userDTO : userDTOS) {
                meetingIds.add(userDTO.getId());
            }
            attendService.attendMeetingFromNotice(summaryHandAddDTO.getMeetingId(), meetingIds, "2", "", Constant.INPUT_BY_HAND);
        } else if (summaryHandAddDTO.getHandleType() == 2) {  //请假
        } else if (summaryHandAddDTO.getHandleType() == 3) {  //列席人员
            List<UserDTO> userDTOS = userService.getAndReplaceList(summaryHandAddDTO.getUserIds(), summaryHandAddDTO.getMeetingId(), 12, "2");
            //组装meetingUserIds -- 从参会表里移除
            List<String> meetingUserIds = new ArrayList<>();
            for (UserDTO userDTO : userDTOS) {
                meetingUserIds.add(userDTO.getId());
            }
            attendService.deleteAttendInfo(summaryHandAddDTO.getMeetingId(), meetingUserIds);
        } else if (summaryHandAddDTO.getHandleType() == 4) { //小组秘书
            List<UserDTO> userDTOS = userService.getAndReplaceList(summaryHandAddDTO.getUserIds(), summaryHandAddDTO.getMeetingId(), 12, "3");
            //组装meetingUserIds -- 从参会表里移除
            List<String> meetingUserIds = new ArrayList<>();
            for (UserDTO userDTO : userDTOS) {
                meetingUserIds.add(userDTO.getId());
            }
            attendService.deleteAttendInfo(summaryHandAddDTO.getMeetingId(), meetingUserIds);
        } else if (summaryHandAddDTO.getHandleType() == 5) {  //工作人员
            List<UserDTO> userDTOS = userService.getAndReplaceList(summaryHandAddDTO.getUserIds(), summaryHandAddDTO.getMeetingId(), 12, "4");
            //组装meetingUserIds -- 从参会表里移除
            List<String> meetingUserIds = new ArrayList<>();
            for (UserDTO userDTO : userDTOS) {
                meetingUserIds.add(userDTO.getId());
            }
            attendService.deleteAttendInfo(summaryHandAddDTO.getMeetingId(), meetingUserIds);
        }
        return ResponseModel.success("成功");
    }

    /**
     * 系统外新增人员
     *
     * @param summaryHandAddDTO 系统外新增人员
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseModel<String> addOutPerson(SummaryHandAddDTO summaryHandAddDTO) {
        if (StringUtils.isEmpty(summaryHandAddDTO.getMeetingId())) {
            return (new ResponseModel.DefaultBuilder(false)).message("meetingId没有传！").code("99").body("meetingId没有传！");
        }
        if (summaryHandAddDTO.getHandleType() == 1) { //参会
            List<String> meetingIds = new ArrayList<>();
            for (UserDTO userDTO : summaryHandAddDTO.getUserDTOS()) {
                userDTO.setJoinType(13);
                userDTO.setMeetingRole("1");
                String id = userService.add(userDTO);
                meetingIds.add(id);
            }
            attendService.attendMeetingFromNotice(summaryHandAddDTO.getMeetingId(), meetingIds, "3", "", Constant.INPUT_BY_HAND);
        } else if (summaryHandAddDTO.getHandleType() == 2) {  //请假

        } else if (summaryHandAddDTO.getHandleType() == 3) {  //列席人员
            for (UserDTO userDTO : summaryHandAddDTO.getUserDTOS()) {
                userDTO.setJoinType(13);
                userDTO.setMeetingRole("2");
                userService.add(userDTO);
            }
        } else if (summaryHandAddDTO.getHandleType() == 4) { //小组秘书
            for (UserDTO userDTO : summaryHandAddDTO.getUserDTOS()) {
                userDTO.setJoinType(13);
                userDTO.setMeetingRole("3");
                userService.add(userDTO);
            }
        } else if (summaryHandAddDTO.getHandleType() == 5) {  //工作人员
            for (UserDTO userDTO : summaryHandAddDTO.getUserDTOS()) {
                userDTO.setJoinType(13);
                userDTO.setMeetingRole("4");
                userService.add(userDTO);
            }
        }
        return ResponseModel.success("成功");
    }

    /**
     * 获取名单审核名单
     *
     * @param page      页面
     * @param searchDTO 查询
     * @return 结果集
     */
    @Override
    public IPage<SummaryNoticeUserResponseDTO> getSummaryNoticeUserList(IPage<SummaryNoticeUserResponseDTO> page, SummarySearchDTO searchDTO) {
        return summaryDataMapper.getSummaryNoticeUserList(page, searchDTO);
    }


    /**
     * 手动删除人员
     *
     * @param usersDeleteDTO 删除的人员
     * @return 结果
     */
    public ResponseModel<String> deletePerson(UsersDeleteDTO usersDeleteDTO) {
        if (usersDeleteDTO.getHandleType() == 1) {  //参会人员删除
            attendService.deleteAttendInfo(usersDeleteDTO.getMeetingId(), usersDeleteDTO.getMeetingUserIds());
        } else if (usersDeleteDTO.getHandleType() == 2) { //请假人员没有删除

        } else {  //列席人员 小组秘书 工作人员
            for (String id : usersDeleteDTO.getMeetingUserIds()) {
                userService.deleteLogic(id);
            }
        }
        return ResponseModel.success("成功");
    }

    @Override
    public ResponseModel<DataNumDTO> getDataNum(String meetingId) {
        if (StringUtils.isEmpty(meetingId)) {
            return ResponseModel.error(null);
        }
        DataNumDTO dataNumDTO = userService.getDataNum(meetingId);
        return ResponseModel.success(dataNumDTO);
    }

    /**
     * 获取请假信息
     *
     * @param leaveId 会议主键
     * @return 结果
     */
    @Override
    public ResponseModel<LeaveDetailDTO> getLeaveInfo(String leaveId) {
        List<LeaveDetailDTO> leaveDetailDTOS = summaryDataMapper.getLeaveInfo(leaveId);
        if (leaveDetailDTOS.size() <= 0) {
            log.error("没有找到这个请假信息！");
            return (new ResponseModel.DefaultBuilder(false)).message("没有找到这个请假信息！").code("99").body(null);
        }

        LeaveDetailDTO leaveDetailDTO = leaveDetailDTOS.get(0);
        //获取日程信息
        List<LeaveScheduleResponseDTO> scheduleResponseDTOS = summaryDataMapper.getLeaveSchedule(leaveId);
        leaveDetailDTO.setScheduleDetailDTOS(scheduleResponseDTOS);
        //获取附件信息
        BusinessVO businessVO = new BusinessVO();
        businessVO.setBusinessApp("cppcc_meeting_backend");
        businessVO.setBusinessTab("t_meeting_leave");
        businessVO.setBusinessTabCol("attachments");
        businessVO.setBusinessId(leaveId);
        leaveDetailDTO.setAttachmentDTOS(fileService.getAttachments(businessVO));
        leaveDetailDTO.setShowAction(0); //默认不显示
        if ("0".equals(leaveDetailDTO.getStatus())) {  //待审核状态 需要确定是否显示同意，拒绝操作
            Account user = authenticationTools.getCurrentOperator();
            List<String> roles = user.getRoles();
            if (roles != null && roles.size() > 0) {
                List<LeaveAuditDTO> auditDTOS = getLeaveAuditList(leaveId);
                List<CategoryItemDTO> categoryItemDTOS = categoryItemService.loadCategoryItemListByConstName("leave_audit_role");
                if (auditDTOS == null || auditDTOS.size() <= 0) { //第一级审批
                    if (categoryItemDTOS.size() > 0 && roles.contains(categoryItemDTOS.get(0).getItemCode())) {
                        leaveDetailDTO.setShowAction(1); //默认不显示
                    }
                } else { //二级审批
                    if (categoryItemDTOS.size() > 1 && roles.contains(categoryItemDTOS.get(1).getItemCode())) {
                        leaveDetailDTO.setShowAction(1); //默认不显示
                    }
                }
            }

        }
        return ResponseModel.success(leaveDetailDTO);
    }

    /**
     * 请假记录
     *
     * @param leaveId 会议主键
     * @return 审核历史
     */
    @Override
    public List<LeaveAuditDTO> getLeaveAuditList(String leaveId) {
        return summaryDataMapper.getLeaveAuditList(leaveId);
    }

    /**
     * 导出汇总数据
     *
     * @param searchDTO 查询的数据
     * @param request   请求
     * @param response  返回
     * @return 结果
     */
    @Override
    public void export(SummarySearchDTO searchDTO, HttpServletRequest request, HttpServletResponse response) {
        if (searchDTO.getUserName() == null) {
            searchDTO.setUserName("");
        }
        if (searchDTO.getExportType().equals("1") || searchDTO.getExportType().equals("2")) {  //参会/请假
            IPage<SummaryDataDTO> page = new Page<>(1, 99999);
            searchDTO.setSummaryType(Integer.parseInt(searchDTO.getExportType()));
            IPage<SummaryDataDTO> pages = getSummaryUserList(page, searchDTO);
            if (pages.getTotal() == 0) {
                log.warn("====>没有查询到数据！");
            }
            String name = searchDTO.getExportType().equals("1") ? "参会人员表" : "请假人员表";
            List<SummaryDataDTO> list = pages.getRecords();
            ExcelUtil.exportToExcel(list, SummaryDataDTO.class, name + ".xlsx", response);
        } else {  // 3 4 5
            UserSearchDTO userSearchDTO = new UserSearchDTO();
            userSearchDTO.setMeetingId(searchDTO.getMeetingId());
            userSearchDTO.setMeetingRole((Integer.parseInt(searchDTO.getExportType()) - 1) + "");
            userSearchDTO.setUserName(searchDTO.getUserName());
            IPage<User> page = new Page<>(1, 99999);
            IPage<UserDTO> pages = userService.page(userSearchDTO, page);
            if (pages.getTotal() == 0) {
                log.warn("====>没有查询到数据！");
            }
            String name = "列席人员";
            if (searchDTO.getExportType().equals("3")) {
                name = "列席人员";
            } else if (searchDTO.getExportType().equals("4")) {
                name = "小组秘书";
            } else {
                name = "工作人员";
            }
            List<UserDTO> list = pages.getRecords();
            EasyPoiUtil.exportExcel(list, UserDTO.class, name + ".xlsx", response);
        }


    }


}
