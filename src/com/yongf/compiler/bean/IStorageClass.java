/*
 * Copyright (C) 1996-2016 YONGF Inc.All Rights Reserved.
 * Scott Wang blog.54yongf.com | blog.csdn.net/yongf2014	
 * 文件名：IStorageClass						
 * 描述：
 * 
 * 修改历史
 * 版本号    作者                     日期                    简要描述
 *  1.0         Scott Wang         16-9-3             新增：Create	
 */

package com.yongf.compiler.bean;

/**
 * 存储类型
 *
 * @author Scott Wang
 * @version 1.0, 16-9-3
 * @see
 * @since JSC 1.0
 */
public interface IStorageClass {

    int SC_GLOBAL = 0x00f0;             //包括整型常量，字符常量，字符串常量，全局变量，函数定义
    int SC_LOCAL = 0x00f1;                  //栈中变量（局部变量）
    int SC_LLOCAL = 0x00f2;                 //寄存器溢出存放栈中
    int SC_CMP = 0x00f3;                    //使用标志寄存器
    int SC_VALMASK = 0x00ff;            //存储类型掩码
    int SC_LVAL = 0x0100;                   //左值
    int SC_SYM = 0x0200;                    //符号

    int SC_ANOM = 0x10000000;               //匿名符号
    int SC_STRUCT = 0x20000000;             //结构体符号
    int SC_MEMBER = 0x40000000;             //结构体成员变量
    int SC_PARAMS = 0x80000000;             //函数参数
}
