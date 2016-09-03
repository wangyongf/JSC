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

        CodeReader reader = new CodeReader(CODE);
        String code = reader.read();
        Lex lex = new Lex(code, FILE_NAME);
        lex.scan();
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
