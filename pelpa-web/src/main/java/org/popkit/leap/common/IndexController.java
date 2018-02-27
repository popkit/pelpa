package org.popkit.leap.common;

import com.alibaba.fastjson.JSONObject;
import org.popkit.core.utils.ResponseUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-16:09:10
 */
@Controller
public class IndexController extends BaseController {

    @RequestMapping(value = "index.html")
    public String index() {
        return "redirect:/elpa/build/index.html";
    }

    @RequestMapping(value = "stat.json")
    public void live(HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "running");
        jsonObject.put("code", 200);
        ResponseUtils.renderJson(response, jsonObject.toJSONString());
    }

}
