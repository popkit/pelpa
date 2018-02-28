package org.popkit.leap.geekpen.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.entity.SimpleResult;
import org.popkit.core.utils.ResponseUtils;
import org.popkit.leap.geekpen.entity.ReadRecords;
import org.popkit.leap.geekpen.entity.RecordVo;
import org.popkit.leap.geekpen.entity.Records;
import org.popkit.leap.geekpen.mapper.RecordsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @RequestMapping(value = "querylatest")
    public void querylatest(HttpServletResponse response) {
        List<Records> recordsList = recordsMapper.queryLatest(5);
        List<RecordVo> recordVos = new ArrayList<RecordVo>();
        if (CollectionUtils.isNotEmpty(recordsList)) {
            for (Records records : recordsList) {
                recordVos.add(new RecordVo(records));
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", recordVos);
        ResponseUtils.renderJson(response, jsonObject.toJSONString());
    }

    @RequestMapping(value = "record.json")
    public void record(@RequestBody ReadRecords records, HttpServletResponse response, HttpServletRequest request) {
        SimpleResult simpleResult = new SimpleResult();
        String postData = null;
        try {
            postData = IOUtils.toString(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (records == null || StringUtils.isBlank(records.getOpenid()) || CollectionUtils.isEmpty(records.getRecords())) {
            simpleResult.update(false, "参数错误!");
        }

        try {
            for (RecordVo vo : records.getRecords()) {
                Records recordsDB = new Records();
                recordsDB.setOpenid(records.getOpenid());
                recordsDB.setBookName(vo.getBookName());
                recordsDB.setType(vo.getType());
                recordsDB.setCreateTime(new Date());
                recordsDB.setProgress(vo.getProgress());
                //recordsMapper.insert(recordsDB);
            }
            simpleResult.update(true, "操作成功!");
        } catch (Exception e) {
            simpleResult.update(false, "操作失败!");
        }

        ResponseUtils.renderJson(response, JSONObject.toJSONString(simpleResult));
    }


    @RequestMapping(value = "mock")
    public String mock(HttpServletRequest request) {
        return "geekpen/mock";
    }
}
