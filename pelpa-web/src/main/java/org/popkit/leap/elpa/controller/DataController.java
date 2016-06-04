package org.popkit.leap.elpa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-06-04:18:44
 */
@Controller
@RequestMapping("elpa/data")
public class DataController {

    @RequestMapping(value = "index.html")
    public String index(HttpServletRequest request) {
        return "elpa/data";
    }
}
