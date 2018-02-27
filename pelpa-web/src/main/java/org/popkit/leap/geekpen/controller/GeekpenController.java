package org.popkit.leap.geekpen.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.entity.SimpleResult;
import org.popkit.core.utils.ResponseUtils;
import org.popkit.leap.geekpen.entity.ReadRecords;
import org.popkit.leap.geekpen.mapper.RecordsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        ResponseUtils.renderJson(response, JSONObject.toJSONString(simpleResult));
    }


    @RequestMapping(value = "mock")
    public String mock(HttpServletRequest request) {
        return "geekpen/mock";
    }
}
