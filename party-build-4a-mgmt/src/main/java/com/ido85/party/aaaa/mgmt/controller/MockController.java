package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.security.utils.DateUtils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 前端 mock 数据 Controller
 */
@Controller
public class MockController {

    @RequestMapping(value = "/manage/assist/query", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> query(@RequestBody Map<String, Object> params) {
        Map<String, Object> resp = new HashMap<>();

        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("relName", "张辅" + i);
            item.put("telephone", "1863494054" + i);
            item.put("idCard", "37010119831223001" + i);
            item.put("fzuserId", StringUtils.toString(i + 1));
            item.put("isActive", StringUtils.toString(i % 2));
            item.put("state", i > 3 ? "1" : "0");
            item.put("remark", (i + 1) + " 级干部");
            item.put("createDate", DateUtils.formatISO8601Date(new Date()));
            item.put("authorizationCode", "38290" + i);
            List<Map<String, String>> clients = new ArrayList<>();
            // if (i < 3) {
                Map<String, String> client = new HashMap<>();
                client.put("clientName", "党组织和单位信息管理系统");
                client.put("manageName", "中国共产党宁阳县伏山镇委员会");
                clients.add(client);

                client = new HashMap<>();
                client.put("clientName", "党员信息管理系统");
                client.put("manageName", "中国共产党宁阳县伏山镇委员会");
                clients.add(client);

                client = new HashMap<>();
                client.put("clientName", "山东e支部管理系统");
                client.put("manageName", "中国共产党宁阳县磁窑镇委员会");
                clients.add(client);
            // 8}
            item.put("clients", clients);
            data.add(item);
        }

        resp.put("count", 19);
        resp.put("data", data);

        return resp;
    }
}
