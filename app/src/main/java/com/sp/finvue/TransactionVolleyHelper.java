package com.sp.finvue;

import java.util.HashMap;

public class TransactionVolleyHelper {
    static String region =  "https://753a59ec-6802-408c-8cb6-a2927c108807-us-east1.apps.astra.datastax.com/api/rest";
    static String user_url = region + "/v2/keyspaces/transactions/user_table/";
    static String transaction_url = region + "/v2/keyspaces/transactions/transaction_table/";
    static String user_transaction_url = region + "/v2/keyspaces/transactions/user_transaction/";
    static String Cassandra_Token = "AstraCS:swcBoNpSAlKFIRFdvBHkSdmM:8be36ea7fb138db56402e1f047c67b58a755d8e99349822f90d3e185666aa423";
    static int lastID = 0;
    static HashMap getHeader() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Cassandra-Token", Cassandra_Token);
        headers.put("Accept", "application/json");
        return headers;
    }
}
