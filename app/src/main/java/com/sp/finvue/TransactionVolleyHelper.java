package com.sp.finvue;

import org.json.JSONObject;

import java.util.HashMap;

public class TransactionVolleyHelper {
    static String region =  "https://753a59ec-6802-408c-8cb6-a2927c108807-us-east1.apps.astra.datastax.com/api/rest";
    static String user_url = region + "/v2/keyspaces/transactions/user_table/";
    static String transaction_url = region + "/v2/keyspaces/transactions/user_trans/"; // action/
    static String Cassandra_Token = "AstraCS:bXNQLQFRWjfhbahiYsHprRCj:27eebe7b7c9bd75e5a91ccae3c9b1d9386870c063f5d57b19b78938c983290d6";
    static int lastID = 0;
    static HashMap getHeader() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Cassandra-Token", Cassandra_Token);
        headers.put("Accept", "application/json");
        return headers;
    }

}
