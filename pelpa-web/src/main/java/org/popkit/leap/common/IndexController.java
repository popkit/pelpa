package org.popkit.leap.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
