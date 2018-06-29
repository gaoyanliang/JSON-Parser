package com.json.demo;

import com.json.demo.jsonstyle.JsonArray;
import com.json.demo.jsonstyle.JsonObject;
import com.json.demo.util.HttpUtil;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JSONParserTest {

    @Test
    public void fromJSON1() throws Exception {
        String json = "{\"a\": 1, \"b\": \"b\", \"c\": {\"a\": 1, \"b\": null, \"d\": [0.1, \"a\", 1,2, 123, 1.23e+10, true, false, null]}}";
        JSONParser jsonParser = new JSONParser();
        JsonObject jsonObject = (JsonObject) jsonParser.fromJSON(json);
        System.out.println(jsonObject);

        assertEquals(1, jsonObject.get("a"));
        assertEquals("b", jsonObject.get("b"));

        JsonObject c = jsonObject.getJsonObject("c");
        assertEquals(null, c.get("b"));

        JsonArray d = c.getJsonArray("d");
        assertEquals(0.1, d.get(0));
        assertEquals("a", d.get(1));
        assertEquals(123, d.get(4));
        assertEquals(1.23e+10, d.get(5));
        assertTrue((Boolean) d.get(6));
        assertFalse((Boolean) d.get(7));
        assertEquals(null, d.get(8));
    }

    @Test
    public void fromJSON2() throws Exception {
        String json = "[[1,2,3,\"\u4e2d\"]]";
        JSONParser jsonParser = new JSONParser();
        JsonArray jsonArray = (JsonArray) jsonParser.fromJSON(json);
        System.out.println(jsonArray);
    }

    @Test
    public void fromJSON3() throws Exception{
        String json = getJSON();
        JSONParser jsonParser = new JSONParser();
        JsonObject jsonObject = (JsonObject)jsonParser.fromJSON(json);
        System.out.println(jsonObject);
    }

    @Test
    public void fromJSON4() throws Exception{
        /**
         * 测试数据
         * http://news-at.zhihu.com/api/2/news/latest
         * http://news-at.zhihu.com/api/3/news/latest
         */

        String url = "http://news-at.zhihu.com/api/4/news/latest";
        String json = new String(HttpUtil.get(url));

        JSONParser jsonParser = new JSONParser();
        JsonObject jsonObject = (JsonObject)jsonParser.fromJSON(json);
        System.out.println(jsonObject);
    }

    private String getJSON() throws IOException {
        String url = "http://music.163.com/weapi/v3/playlist/detail";
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("params", "kJMudgZJvK8p8STuuxRpkUvO71Enw4C9y91PBkVTv2SMVnWG30eDKK1iAPcXnEah"));
        params.add(new BasicNameValuePair("encSecKey", "d09b0b95b7d5b4e68aa7a16d6177d3f00a78bfa013ba59f309d41f18a2b4ea066cdea7863866b6283f403ddcd3bfb51f73f8ad3c6818269ceabff934a645196faf7a9aae0edde6e232b279fd495140e6252503291cf819eabbd9f3373648775201a70f179b7981d627257d3bba5a5e1b99d0732ce3e898db3614d82bcbe1a6a8"));
        Response response = Request.Post(url)
                .bodyForm(params)
                .execute();

        return response.returnContent().asString(Charset.forName("utf-8"));
    }
}
