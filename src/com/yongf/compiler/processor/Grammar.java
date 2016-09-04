/*
 * Copyright (C) 1996-2016 YONGF Inc.All Rights Reserved.
 * Scott Wang blog.54yongf.com | blog.csdn.net/yongf2014	
 * 文件名：Grammar						
 * 描述：
 * 
 * 修改历史
 * 版本号    作者                     日期                    简要描述
 *  1.0         Scott Wang         16-9-3             新增：Create	
 */

package com.yongf.compiler.processor;

import static com.yongf.compiler.bean.ITokenCode.KW_CHAR;
import static com.yongf.compiler.bean.ITokenCode.KW_SHORT;
import static com.yongf.compiler.bean.ITokenCode.TK_EOF;

/**
 * Simple-C语言的语法分析器
 *
 * @author Scott Wang
 * @version 1.0, 16-9-3
 * @see
 * @since JSC 1.0
 */
public class Grammar {

    /**
     * 单词编码
     */
    private int syn;

    /**
     * <translation_unit::={<external_declaration>}<TK_EOF></TK_EOF>
     */
    public void translationUnit() {
        while (syn != TK_EOF) {

        }
    }

    /**
     * 解析外部声明
     *
     * @param l 存储类型，局部or全局
     */
    public void externalDeclaration(int l) {

    }

    /**
     * 解析类型区分符
     *
     * <type_specifier>::=<KW_INT>
     *     |<KW_CHAR>
     *     |<KW_SHORT>
     *     |<KW_VOID>
     *     |<struct_specifier>
     *
     * @return true-发现合法的类型区分符
     */
    public boolean typeSpecifier() {
        boolean result = false;
        switch (syn) {
            case KW_CHAR:
                result = true;

                break;
            case KW_SHORT:
//                result = true;

                break;
        }

        return result;
    }
}
