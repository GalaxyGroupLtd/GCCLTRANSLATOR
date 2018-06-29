/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carpa.library.config;

/**
 * @author ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr /
 * aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662
 */
public class ErrorCodeConfig {
    public static final String[] PARSING_ERROR = {"10", "Object parse error"};
    public static final String[] VALIDATION_ERROR = {"11", "Object's field validation error"};
    public static final String[] GENERAL_PROCESSING_ERROR = {"12", "General processing error"};
    public static final String[] CONSTARINT_ERROR = {"13", "Database constraint violation"};
    public static final String[] GENERAL_DATABASE_ERROR = {"14", "General database error"};
    public static final String[] GENERAL_LOGIC_ERROR = {"15", "General logic error"};
    public static final String[] NORMAL_ERROR = {"16", "General faillure"};
    public static final String[] INTERNAL_ERROR = {"17", "Internal application faillure"};
    public static final String[] EXTERNAL_API_ERROR = {"18", "External API error"};
}
