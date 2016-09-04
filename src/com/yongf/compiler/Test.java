/*
 * Copyright (C) 1996-2016 YONGF Inc.All Rights Reserved.
 * Scott Wang blog.54yongf.com | blog.csdn.net/yongf2014	
 * 文件名：Test						
 * 描述：
 * 
 * 修改历史
 * 版本号    作者                     日期                    简要描述
 *  1.0         Scott Wang         16-9-1             新增：Create	
 */

package com.yongf.compiler;

import com.yongf.compiler.processor.CodeReader;
import com.yongf.compiler.processor.Lex;
import com.yongf.compiler.processor.LexicalAnalyzer;

/**
 * description
 *
 * @author Scott Wang
 * @version 1.0, 16-9-1
 * @see
 * @since JSC 1.0
 */
public class Test {

    private static String FILE_NAME = "scc.txt";
    private static String CODE = "/home/scottwang/桌面/" + FILE_NAME;

    public static void main(String[] args) {
//        oldLex();

        newLex();
    }

    private static void other() {
        String s = "2";
        changeString(s);
        System.out.println("s = " + s);
    }

    /**
     * 通过赋值的方式是无法直接改变String类型的值的！
     * @param str
     */
    private static void changeString(String str) {
        str = str + "1";
    }

    /**
     * 曲线救国的方式修改基本类型变量的值
     *
     * @param i 存放待修改变量的数组容器
     */
    private static void change(int[] i) {
        i[0] = 3;
    }

    private static void newLex() {
        CodeReader reader = new CodeReader(CODE);
        String code = reader.read();
        Lex lex = new Lex(code, FILE_NAME);
        lex.scan0();
    }

    private static void oldLex() {
        CodeReader reader = new CodeReader(CODE);
        String sourcecode = reader.read();
        LexicalAnalyzer analyzer = new LexicalAnalyzer(sourcecode);
        analyzer.scan();
        analyzer.storeResult();
    }

    private static void getLineBreak() {
        String property = System.getProperty("line.separator");
        if (property.equals("\r\n")) {
            property = "\\r\\n";
        } else if (property.equals("\n")) {
            property = "\\n";
        }
        System.out.println("property = " + property);
    }
}
