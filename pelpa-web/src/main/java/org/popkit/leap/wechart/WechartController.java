package org.popkit.leap.wechart;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.popkit.core.config.LeapConfigLoader;
import org.popkit.core.utils.ResponseUtils;
import org.popkit.leap.wechart.entity.Records;
import org.popkit.leap.wechart.mapper.RecordsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Author: aborn.jiang
 * Email : aborn.jiang@foxmail.com.com
 * Date  : 02-25-2018
 * Time  : 1:00 PM
 */
@RequestMapping(value = "api/wechart")
@Controller
public class WechartController {

    @Autowired
    RecordsMapper recordsMapper;

    @RequestMapping(value = "test.json")
    public void test(HttpServletResponse response) {
        Records records = recordsMapper.selectByPrimaryKey(1);
        ResponseUtils.renderJson(response, JSONObject.toJSONString(records));
    }

    @RequestMapping(value = "jscode2session")
    public void jscode2session(HttpServletResponse response, String code) {
        String appid= LeapConfigLoader.get("wechart_appid");
        String secret = LeapConfigLoader.get("wechart_secret");
        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?" +
                        "appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code);
        JSONObject result = null;
        try {
            result = getWechartInfo(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        if (result == null) {
            jsonObject.put("code", 500);
        } else {
            jsonObject = result;
            jsonObject.put("code", 200);
        }
        ResponseUtils.renderJson(response, jsonObject.toJSONString());
    }

    private JSONObject getWechartInfo(String url) throws Exception {
        // 默认链接超时设置为500ms,请求超时(SocketTimeout)设置为300ms
        int socketTimeout = 300;  // 请求超时
        int connectTimeout = 500; // 链接超时

        // 设置请求参数
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).
                setConnectTimeout(connectTimeout).build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        JSONObject obj = new JSONObject();
        obj.put("data", "data string");

        // httclient及数据设置
        CloseableHttpClient httpclient = HttpClients.createDefault();
        StringEntity stringEntity = new StringEntity(obj.toJSONString());
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);

        try {
            CloseableHttpResponse response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {

                HttpEntity responseEntity = response.getEntity();
                BufferedReader br = new BufferedReader(new InputStreamReader((responseEntity.getContent())));
                String output;
                // 读取输入流
                StringBuilder stringBuilder = new StringBuilder();
                while ((output = br.readLine()) != null) {
                    stringBuilder.append(output);
                }
                JSONObject jsonObject = JSONObject.parseObject(stringBuilder.toString());
                String status = jsonObject.getString("status");
                EntityUtils.consume(responseEntity);
                return jsonObject;
            } else {
            }
        } finally {
            httpclient.close();
        }
        return new JSONObject();
    }

}
