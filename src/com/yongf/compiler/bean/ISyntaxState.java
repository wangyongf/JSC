/*
 * Copyright (C) 1996-2016 YONGF Inc.All Rights Reserved.
 * Scott Wang blog.54yongf.com | blog.csdn.net/yongf2014	
 * 文件名：ISyntaxState						
 * 描述：
 * 
 * 修改历史
 * 版本号    作者                     日期                    简要描述
 *  1.0         Scott Wang         16-9-4             新增：Create	
 */

package com.yongf.compiler.bean;

/**
 * 语法状态枚举值
 *
 * @author Scott Wang
 * @version 1.0, 16-9-4
 * @see
 * @since JSC 1.0
 */
public interface ISyntaxState {

    int SNTX_NUL = 0;           //空状态，没有语法缩进动作
    int SNTX_SP = 1;                //空格
    int SNTX_LF_HT = 2;         //换行并缩进，每一个声明，函数定义，语句结束都要置为此状态
    int SNTX_DELAY = 3;         //延迟到取出下一个单词之后确定输出格式
}
