/*
 * Copyright (C) 1996-2016 YONGF Inc.All Rights Reserved.
 * Scott Wang blog.54yongf.com | blog.csdn.net/yongf2014	
 * 文件名：ETypeCode						
 * 描述：
 * 
 * 修改历史
 * 版本号    作者                     日期                    简要描述
 *  1.0         Scott Wang         16-9-3             新增：Create	
 */

package com.yongf.compiler.bean;

/**
 * 数据类型编码
 *
 * @author Scott Wang
 * @version 1.0, 16-9-3
 * @see
 * @since JSC 1.0
 */
public interface ITypeCode {

    int T_INT = 0;              //整型
    int T_CHAR = 1;             //字符型
    int T_SHORT = 2;            //短整型
    int T_VOID = 3;                 //空类型
    int T_PTR = 4;                  //指针
    int T_FUNC = 5;                 //函数
    int T_STRUCT = 6;               //结构体

    int T_BTYPE = 0x000f;           //基本类型掩码
    int T_ARRAY = 0x0010;           //数组
}
