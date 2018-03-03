package org.popkit.leap.geekpen.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.popkit.core.entity.SimpleResult;
import org.popkit.core.logger.LeapLogger;
import org.popkit.core.utils.ResponseUtils;
import org.popkit.leap.geekpen.entity.ReadRecords;
import org.popkit.leap.geekpen.entity.RecordVo;
import org.popkit.leap.geekpen.entity.Records;
import org.popkit.leap.geekpen.entity.Users;
import org.popkit.leap.geekpen.mapper.RecordsMapper;
import org.popkit.leap.geekpen.mapper.UsersMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * Date  : 02-27-2018
 * Time  : 7:52 PM
 */
@RequestMapping(value = "geekpen/api")
@Controller
public class GeekpenController {

    @Autowired
    RecordsMapper recordsMapper;

    @Autowired
    UsersMapper usersMapper;

    @RequestMapping(value = "querylatest")
    public void querylatest(HttpServletResponse response) {
        List<Records> recordsList = recordsMapper.queryLatest(5);
        Map<String, Users> usersMap = new HashMap<String, Users>();

        List<RecordVo> recordVos = new ArrayList<RecordVo>();
        if (CollectionUtils.isNotEmpty(recordsList)) {
            for (Records records : recordsList) {
                RecordVo vo = new RecordVo(records);

                if (usersMap.containsKey(records.getOpenid())) {
                    Users users = new Users();
                    BeanUtils.copyProperties(usersMap.get(records.getOpenid()), users);
                    vo.setUser(users);
                } else {
                    Users user = usersMapper.selectByOpenid(records.getOpenid());
                    if (user != null) {
                        vo.setUser(user);
                        usersMap.put(records.getOpenid(), user);
                    }
                }
                recordVos.add(vo);
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", recordVos);
        ResponseUtils.renderJson(response, jsonObject.toJSONString());
    }

    @RequestMapping(value = "queryrecords")
    public void queryrecords(String openid, HttpServletResponse response) {
        List<Records> result = new ArrayList<Records>();
        SimpleResult<List<Records>> simpleResult = new SimpleResult<List<Records>>();
        simpleResult.update(true, "");

        if (StringUtils.isBlank(openid)) {
            simpleResult.update(false, "参数错误!");
            ResponseUtils.renderJson(response, JSONObject.toJSONString(simpleResult));
        }

        try {
            List<Records> recordsList = recordsMapper.queryRecords(openid);
            if (CollectionUtils.isNotEmpty(recordsList)) {
                result = recordsList;
            }
        } catch (Exception e){
            simpleResult.update(false, "服务器异常!");
        }
        simpleResult.setData(result);
        ResponseUtils.renderJson(response, JSONObject.toJSONString(simpleResult));
    }

    @RequestMapping(value = "record.json")
    public void record(@RequestBody ReadRecords records, HttpServletResponse response, HttpServletRequest request) {
        SimpleResult simpleResult = new SimpleResult();
        if (records == null || StringUtils.isBlank(records.getOpenid()) || CollectionUtils.isEmpty(records.getRecords())) {
            simpleResult.update(false, "参数错误!");
        }

        try {
            updateUserInfo(records.getUser());
        } catch (Exception e) {
            LeapLogger.warn("update info exception!", e);
        }

        try {
            // 同一本书，同一天只能上报一次, 只处理今天的
            String day = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateBeginToday = simpleDateFormat.parse(day + " 00:00:00");

            List<Records> recordsList = recordsMapper.queryDatasFromTime(records.getOpenid(), dateBeginToday);
            Map<String, Records> recordDBMap = new HashMap<String, Records>();
            if (CollectionUtils.isNotEmpty(recordsList)) {
                for (Records records1 : recordsList) {
                    recordDBMap.put(buildKey(records1.getBookName(), records1.getRecordTime()), records1);
                }
            }

            DateTime dateTime = new DateTime();
            int  dayOfYear = dateTime.dayOfYear().get();

            for (RecordVo vo : records.getRecords()) {
                Records recordsDB = new Records();
                recordsDB.setOpenid(records.getOpenid());
                recordsDB.setBookName(vo.getBookName());
                recordsDB.setType(vo.getType());
                recordsDB.setRecordTime(vo.getTime());
                int recordDay = new DateTime(vo.getTime()).dayOfYear().get();
                if (recordDay != dayOfYear) { continue; } // 不是同一天直接跳过

                recordsDB.setProgress(vo.getProgress());
                String key = buildKey(recordsDB.getBookName(), recordsDB.getRecordTime());
                if (recordDBMap.containsKey(key)) {
                    recordsDB.setId(recordDBMap.get(key).getId());
                    //recordsMapper.updateByPrimaryKey(recordsDB);
                } else {
                    //recordsMapper.insert(recordsDB);
                }
            }
            simpleResult.update(true, "操作成功!");
        } catch (Exception e) {
            simpleResult.update(false, "操作失败!");
        }

        ResponseUtils.renderJson(response, JSONObject.toJSONString(simpleResult));
    }

    private boolean updateUserInfo(Users user) {
        if (user == null || StringUtils.isBlank(user.getOpenid())) { return false;}
        Users users = usersMapper.selectByOpenid(user.getOpenid());
        if (users != null) {
            //usersMapper.updateByPrimaryKey(user);
        } else {
            usersMapper.insert(user);
        }
        return true;
    }

    private String buildKey(String bookName, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date) + bookName;
    }

    @RequestMapping(value = "mock")
    public String mock(HttpServletRequest request) {
        return "geekpen/mock";
    }

    @RequestMapping(value = "test.json")
    public void test(HttpServletResponse response) {
        Users users = usersMapper.selectByOpenid("o6Jzu0OvdlwmcmQ2N1FtFpIfslx4");
        if (users != null) {
            ResponseUtils.renderJson(response, JSONObject.toJSONString(users));
        }
    }
}
