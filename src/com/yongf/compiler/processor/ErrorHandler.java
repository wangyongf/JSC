/*
 * Copyright (C) 1996-2016 YONGF Inc.All Rights Reserved.
 * Scott Wang blog.54yongf.com | blog.csdn.net/yongf2014	
 * 文件名：Error						
 * 描述：
 * 
 * 修改历史
 * 版本号    作者                     日期                    简要描述
 *  1.0         Scott Wang         16-9-3             新增：Create	
 */

package com.yongf.compiler.processor;

import com.yongf.compiler.bean.IErrorLevel;
import com.yongf.compiler.bean.IWorkStage;
import com.yongf.compiler.bean.TkWord;

import java.util.List;

/**
 * 编译器错误处理程序
 *
 * @author Scott Wang
 * @version 1.0, 16-9-3
 * @see
 * @since JSC 1.0
 */
public class ErrorHandler {

    /**
     * 异常处理
     *
     * @param stage      编译阶段还是链接阶段
     * @param level      错误级别
     * @param fileName   文件名
     * @param lineNumber 错误所在行号
     * @param msg        错误信息
     * @param tkTable    单词表(用于输出错误信息)
     */
    public static void exception(int stage, int level, String fileName, int lineNumber,
                                 String msg, List<TkWord> tkTable) {
        if (stage == IWorkStage.STAGE_COMPILE) {
            if (level == IErrorLevel.LEVEL_WARNING) {
                System.out.println(fileName + "(第" + lineNumber + "行): 编译警告: " + msg + "!");
            } else {
                System.out.println(fileName + "(第" + lineNumber + "行): 编译错误: " + msg + "!");
                System.exit(120);
            }
        } else {
            System.out.println("链接错误: " + msg + "!");
            System.exit(110);
        }
    }

    /**
     * 编译警告处理
     *
     * @param fileName   文件名
     * @param lineNumber 警告所在行号
     * @param msg        错误信息
     * @param tkTable    单词表
     */
    public static void warning(String fileName, int lineNumber, String msg,
                               List<TkWord> tkTable) {
        exception(IWorkStage.STAGE_COMPILE, IErrorLevel.LEVEL_WARNING,
                fileName, lineNumber, msg, tkTable);
    }

    /**
     * 编译致命错误处理
     *
     * @param fileName   文件名
     * @param lineNumber 错误所在行号
     * @param msg        错误信息
     * @param tkTable    单词表
     */
    public static void error(String fileName, int lineNumber, String msg,
                             List<TkWord> tkTable) {
        exception(IWorkStage.STAGE_COMPILE, IErrorLevel.LEVEL_ERROR,
                fileName, lineNumber, msg, tkTable);
    }

    /**
     * 提示错误，此处缺少某个语法成分
     *
     * @param fileName   文件名
     * @param lineNumber 错误所在行号
     * @param msg        错误信息
     * @param tkTable    单词表
     */
    public static void expect(String fileName, int lineNumber, String msg,
                              List<TkWord> tkTable) {
        error(fileName, lineNumber, "缺少" + msg, tkTable);
    }
}
