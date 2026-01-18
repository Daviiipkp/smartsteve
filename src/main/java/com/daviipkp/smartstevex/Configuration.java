package com.daviipkp.smartstevex;

import java.io.File;

public class Configuration {

    //General
    public static String LLM_PROVIDER;
    public static String SEARCH_PROVIDER;
    public static String LLM_MODEL_NAME;
    public static String LLM_API_KEY;
    public static String SEARCH_API_KEY;
    public static String USER_COMMAND_PACKAGE;


    //Numbers
    public static int PROTOCOL_SEARCH_NUMBER = 5;


    //Booleans
    public static boolean DO_WARM_UP = true;
    public static boolean USE_VOICE_START_WORD = true;
    public static boolean USE_VOICE_END_WORD = true;
    public static boolean VOICE_TYPING_FEATURE = false;
    public static boolean CLEAR_MEMO_ON_STARTUP = false;
    public static boolean USE_DEFAULT_COMMANDS = true;


    //Debug
    public static boolean SHOW_VOICE_TEXT_DEBUG = false;
    public static boolean MEMORY_DEBUG = false;
    public static boolean USER_PROMPT_DEBUG = false;
    public static boolean STEVE_RESPONSE_DEBUG = false;
    public static boolean DATABASE_SAVING_DEBUG = false;
    public static boolean FINAL_PROMPT_DEBUG = false;
    public static boolean PROMPT_LATENCY_DEBUG = false;
    public static boolean PROMPT_COMPONENTS_CONTENT_EMPTY_DEBUG = false;



    //Strings
    public static String VOICE_START_WORD = "steve";
    public static String VOICE_END_WORD = "over";
    public static String VOICE_TYPING_STOP_STRING;
    public static String FIRST_BOOT_INSTRUCTIONS;
    public static String ALARM_PATH = System.getProperty("user.dir") + File.separator + "alarm.mp3";
}
