package com.nowcoder.community.controller;

import com.nowcoder.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * ClassName: DataController
 * Package: com.nowcoder.community.controller
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    /**
     * Gets data page.
     * 打开统计页面的方法
     *
     * @return the data page
     */
    @RequestMapping(path = "/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage() {
        return "/site/admin/data";
    }

    /**
     * Gets uv.
     * 处理统计网站UV的请求
     *
     * @param start the start
     * @param end   the end
     * @return the uv
     */
    @RequestMapping(path = "/data/uv", method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult", uv);
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate", end);
        return "forward:/date";
    }

    /**
     * Gets dau.
     * 处理统计网站DAU的请求
     *
     * @param start the start
     * @param end   the end
     * @param model the model
     * @return the dau
     */
    @RequestMapping(path = "/data/dau", method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long dau = dataService.calculateUV(start, end);
        model.addAttribute("dauResult", dau);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);
        return "forward:/date";
    }

}
