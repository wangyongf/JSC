/*
 * Copyright (C) 1996-2016 YONGF Inc.All Rights Reserved.
 * Scott Wang blog.54yongf.com | blog.csdn.net/yongf2014	
 * 文件名：Symbol						
 * 描述：
 * 
 * 修改历史
 * 版本号    作者                     日期                    简要描述
 *  1.0         Scott Wang         16-9-3             新增：Create	
 */

package com.yongf.compiler.bean;

/**
 * 符号存储结构定义
 *
 * @author Scott Wang
 * @version 1.0, 16-9-3
 * @see
 * @since JSC 1.0
 */
public class Symbol {

    int v;                  //符号的单词编码
    int r;                     //符号存储类型
    int c;                      //符号关联值
    Type type;             //符号的数据类型
    Symbol next;        //关联的其他符号
    Symbol prev_tok;                //指向前一定义的同名符号
}
