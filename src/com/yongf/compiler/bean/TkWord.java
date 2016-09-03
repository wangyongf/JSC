/*
 * Copyright (C) 1996-2016 YONGF Inc.All Rights Reserved.
 * Scott Wang blog.54yongf.com | blog.csdn.net/yongf2014	
 * 文件名：TkWord						
 * 描述：
 * 
 * 修改历史
 * 版本号    作者                     日期                    简要描述
 *  1.0         Scott Wang         16-9-3             新增：Create	
 */

package com.yongf.compiler.bean;

/**
 * 单词存储结构定义
 *
 * @author Scott Wang
 * @version 1.0, 16-9-3
 * @see
 * @since JSC 1.0
 */
public class TkWord {

    public int tkCode;                 //单词编码
    public String spelling;             //单词字符串
    public Symbol struct;              //单词所表示的结构定义
    public Symbol identifier;          //单词所表示的标识符

    public TkWord(int tkCode, String spelling, Symbol struct, Symbol identifier) {
        this.tkCode = tkCode;
        this.spelling = spelling;
        this.struct = struct;
        this.identifier = identifier;
    }
}
