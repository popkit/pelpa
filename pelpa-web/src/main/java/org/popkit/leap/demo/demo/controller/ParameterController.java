package org.popkit.leap.demo.demo.controller;

import org.popkit.core.annotation.LeapSupport;
import org.popkit.core.entity.CommonResponse;
import org.popkit.leap.demo.common.BaseController;
import org.popkit.leap.demo.demo.entity.ParamReq;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-12:21:59
 */
@Controller
@RequestMapping(value = "demo")
public class ParameterController extends BaseController {

    @RequestMapping(value = "parameter.html")
    @LeapSupport
    public CommonResponse<ParamReq> parameter(ParamReq paramReq) {
        CommonResponse<ParamReq> commonResponse = new CommonResponse<ParamReq>();
        commonResponse.setData(paramReq);
        commonResponse.update(true, "get success");
        return commonResponse;
    }
}
