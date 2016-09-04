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

    public int v;                  //符号的单词编码
    public int r;                     //符号存储类型(StorageClass)
    public int c;                      //符号关联值
    public Type type;             //符号的数据类型
    public Symbol next;        //关联的其他符号
    public Symbol prev_tok;                //指向前一定义的同名符号

    public Symbol(int v, int r, int c, Type type, Symbol next, Symbol prev_tok) {
        this.v = v;
        this.r = r;
        this.c = c;
        this.type = type;
        this.next = next;
        this.prev_tok = prev_tok;
    }
}
