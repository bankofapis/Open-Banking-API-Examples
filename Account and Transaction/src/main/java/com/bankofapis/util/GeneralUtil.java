package com.bankofapis.util;

import org.apache.commons.cli.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class GeneralUtil {
    public static NameValuePair getNameValuePair(String name, String value) {
        return new BasicNameValuePair(name, value);
    }

    public static String GetAccountIdFromFlags(String[] args) throws ParseException {
        Options options=new Options();
        Option accountIdFlag= new Option("accountId",true, "account ID of the account being queried. GetAccounts endpoint can be queried to get a valid Account ID.");
        accountIdFlag.setRequired(true);
        options.addOption(accountIdFlag);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine cmd;

        try{
            cmd = commandLineParser.parse(options,args);
        } catch(ParseException e){
            System.out.println(e.getMessage());
            helpFormatter.printHelp("Help",options);
            throw e;
        }
        String accountId = cmd.getOptionValue("accountId");
        System.out.println(accountId);
        return accountId;
    }

    public static boolean isNullOrEmpty(String val) {
        return val == null || val.equals("");
    }
}
