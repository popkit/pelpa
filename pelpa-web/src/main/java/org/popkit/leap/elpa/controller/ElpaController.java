package org.popkit.leap.elpa.controller;

import org.popkit.leap.common.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-06-04:18:38
 */
@Controller
@RequestMapping(value = "elpa")
public class ElpaController extends BaseController {

    @RequestMapping(value = "index.html")
    public String index() {
        return "elpa/index";
    }
}
