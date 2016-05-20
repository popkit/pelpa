package org.popkit.leap.elpa.services;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.popkit.leap.elpa.utils.FetchRemoteFileUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-20:21:58
 */
@Service
public class HttpProxyService {

    public boolean downloadGistFile(String url, String workingPath) {
        return FetchRemoteFileUtils.downloadFile(url, workingPath);
    }

    public String getJSON(String url) throws IOException {
        //String url = "http://example.com";
        // 默认链接超时设置为500ms,请求超时(SocketTimeout)设置为300ms
        int socketTimeout = 300;  // 请求超时
        int connectTimeout = 500; // 链接超时

        // 设置请求参数
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).
                setConnectTimeout(connectTimeout).build();
        HttpGet httpPost = new HttpGet(url);
        //
        httpPost.setConfig(requestConfig);
        JSONObject obj = new JSONObject();
        obj.put("data", "data string");

        // httclient及数据设置
        CloseableHttpClient httpclient = HttpClients.createDefault();
        StringEntity stringEntity = new StringEntity(obj.toJSONString());
        stringEntity.setContentType("application/json");
        //httpPost.setEntity(stringEntity);
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
                return stringBuilder.toString();
            } else {
                //
            }
        } finally {
            httpclient.close();
        }

        return null;
    }
}
