package com.stackfortech.urlShorteningService.util;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class QueryUtil {

    public static List<NameValuePair> getQueryMap(String query)
    {
        List<NameValuePair> queryMap = new ArrayList<NameValuePair>();
        String[] params = query.split(Pattern.quote("&"));
        for (String param : params)
        {
            String[] chunks = param.split(Pattern.quote("="));
            String name = chunks[0], value = null;
            if(chunks.length > 1) {
                value = chunks[1];
            }
            queryMap.add(new BasicNameValuePair(name, value));
        }
        return queryMap;
    }


}
