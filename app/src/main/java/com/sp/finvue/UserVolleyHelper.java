package com.sp.finvue;

import java.util.HashMap;

public class UserVolleyHelper {
    static String region =  "https://e19129fa-47f2-40f7-ab7c-46539ea483d0-us-east-2.apps.astra.datastax.com/api/rest";
    static String url = region + "/v2/namespaces/finvue/collections";
    static String Cassandra_Token = "AstraCS:GdolkTDLTtGsvNRNuFnzzQuC";
    static int lastID = 0;
    static HashMap getHeader() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Cassandra-Token", Cassandra_Token);
        headers.put("Accept", "application/json");
        return headers;
    }
}
