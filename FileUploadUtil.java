package com.chris.test.httptest.utils;


import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FileUploadUtil {

	public static void main(String[] args) throws Exception {
        fileUpload(file2byte(new File("F:\\test_directory\\abcd.xlsx")), "", "123.xlsx");
    }

    /**
     * 以流的形式上传文件至文件服务器
     * @param data 文件字节数组
     * @param folderName 文件夹名
     * @param fileName 文件名
     * @return
     * @throws Exception
     */
    public static String fileUpload(byte[] data, String folderName, String fileName) throws Exception {
        String result = null;
        InputStream inputStream = null;
        CloseableHttpResponse response = null;

        CloseableHttpClient httpClient = HttpClients.createDefault();

        //连接超时相关设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000)              //设置连接超时时间
                .setConnectionRequestTimeout(3000)    // 设置请求超时时间
                .setSocketTimeout(3000)
                .setRedirectsEnabled(true)            //默认允许自动重定向
                .build();

        //把一个普通参数和文件上传给下面这个地址    是一个servlet
        HttpPost httpPost = new HttpPost("http://127.0.0.1:9090/upload-service/upload");

        httpPost.setConfig(requestConfig);

        //设置传输参数
        MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();

        //添加文件以外的参数 post模拟表单请求
//        Map<String,String> postParam = new HashMap<>();
//        postParam.put("mode", "upload");
//        postParam.put("path", "/" + folderName + "/");
//        Set<String> keySet = postParam.keySet();
//
//        //添加参数
//        for (String key : keySet) {
//            multipartEntity.addPart(key, new StringBody(postParam.get(key), ContentType.create("text/plain", Consts.UTF_8)));
//        }

        try {
            inputStream = new ByteArrayInputStream(data);

            //这里是使用流的形式上传
            InputStreamBody inputStreamBody = new InputStreamBody(inputStream, fileName);
            multipartEntity.addPart("file", inputStreamBody);

            //在http报文中获取的实体
            HttpEntity reqEntity = multipartEntity.build();
            httpPost.setEntity(reqEntity);

            //发起请求   并返回请求的响应
            response = httpClient.execute(httpPost);
            //获取响应对象
            HttpEntity resEntity = response.getEntity();
            //响应结果
            result = EntityUtils.toString(resEntity, Charset.forName("UTF-8"));

            //销毁响对象
            EntityUtils.consume(resEntity);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            if (inputStream != null) {
                inputStream.close();
            }

            if(response != null){
                response.close();
            }
        }

        return result ;

    }

    public static byte[] file2byte(File file){

        byte[] buffer = null;

        try{

            FileInputStream fis = new FileInputStream(file);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            int n;

            while ((n = fis.read(b)) != -1)

            {

                bos.write(b, 0, n);

            }

            fis.close();

            bos.close();

            buffer = bos.toByteArray();

        }catch (FileNotFoundException e){

            e.printStackTrace();

        }

        catch (IOException e){

            e.printStackTrace();

        }

        return buffer;

    }
}
