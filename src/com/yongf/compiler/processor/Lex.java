/*
 * Copyright (C) 1996-2016 YONGF Inc.All Rights Reserved.
 * Scott Wang blog.54yongf.com | blog.csdn.net/yongf2014	
 * 文件名：Lex						
 * 描述：
 * 
 * 修改历史
 * 版本号    作者                     日期                    简要描述
 *  1.0         Scott Wang         16-9-3             新增：Create	
 */

package com.yongf.compiler.processor;

import com.yongf.compiler.bean.ILexState;
import com.yongf.compiler.bean.TkWord;

import java.util.ArrayList;
import java.util.List;

import static com.yongf.compiler.bean.ITokenCode.*;

/**
 * Simple-C语言词法分析器
 *
 * @author Scott Wang
 * @version 1.0, 16-9-3
 * @see
 * @since JSC 1.0
 */
public class Lex {

    /**
     * 单词表
     */
    private List<TkWord> tkTable = new ArrayList<>();

    /**
     * 单词编码
     */
    private int syn;

    /**
     * 单词字符串
     * eg:
     * abc
     */
    private StringBuilder tokenStr = new StringBuilder();

    /**
     * 单词源码字符串
     * eg:
     * "abc"
     */
    private StringBuilder sourceStr = new StringBuilder();

    /**
     * 源代码字符串(所有)
     */
    private String sourceCode;

    /**
     * 记录当前扫描到源代码字符串中的位置
     */
    private int curIndex;

    /**
     * 整型常量的值
     */
    private int intVal;

    /**
     * 当前取到的源码字符
     */
    private char ch;

    /**
     * 当前所在行号
     */
    private int lineNumber;

    /**
     * 文件名
     */
    private String fileName = "";

    public Lex(String sourceCode, String fileName) {
        this.sourceCode = sourceCode;
        this.fileName = fileName;
    }

    /**
     * 词法分析初始化
     * 初始化单词表
     */
    public void init() {
        lineNumber = 1;
        init0();
    }

    private void init0() {
        TkWord[] keywords = {
                new TkWord(TK_PLUS, "+", null, null),
                new TkWord(TK_MINUS, "-", null, null),
                new TkWord(TK_STAR, "*", null, null),
                new TkWord(TK_DIVIDE, "/", null, null),
                new TkWord(TK_MOD, "%", null, null),
                new TkWord(TK_EQ, "==", null, null),
                new TkWord(TK_NEQ, "!=", null, null),
                new TkWord(TK_LT, "<", null, null),
                new TkWord(TK_LEQ, "<=", null, null),
                new TkWord(TK_GT, ">", null, null),
                new TkWord(TK_GEQ, ">=", null, null),
                new TkWord(TK_ASSIGN, "=", null, null),
                new TkWord(TK_POINTSTO, "->", null, null),
                new TkWord(TK_DOT, ".", null, null),
                new TkWord(TK_AND, "&", null, null),
                new TkWord(TK_OPENPA, "(", null, null),
                new TkWord(TK_CLOSEPA, ")", null, null),
                new TkWord(TK_OPENBR, "[", null, null),
                new TkWord(TK_CLOSEBR, "]", null, null),
                new TkWord(TK_BEGIN, "{", null, null),
                new TkWord(TK_END, "}", null, null),
                new TkWord(TK_SEMICOLON, ";", null, null),
                new TkWord(TK_COMMA, ",", null, null),
                new TkWord(TK_ELLIPSIS, "...", null, null),
                new TkWord(TK_EOF, "End_of_File", null, null),

                new TkWord(TK_CINT, "整型常量", null, null),
                new TkWord(TK_CCHAR, "字符常量", null, null),
                new TkWord(TK_CSTR, "字符串常量", null, null),

                new TkWord(KW_CHAR, "char", null, null),
                new TkWord(KW_SHORT, "short", null, null),
                new TkWord(KW_INT, "int", null, null),
                new TkWord(KW_VOID, "void", null, null),
                new TkWord(KW_STRUCT, "struct", null, null),

                new TkWord(KW_IF, "if", null, null),
                new TkWord(KW_ELSE, "else", null, null),
                new TkWord(KW_FOR, "for", null, null),
                new TkWord(KW_CONTINUE, "continue", null, null),
                new TkWord(KW_BREAK, "break", null, null),
                new TkWord(KW_RETURN, "return", null, null),
                new TkWord(KW_SIZEOF, "sizeof", null, null),
                new TkWord(KW_ALIGN, "__align", null, null),
                new TkWord(KW_CDECL, "__cdecl", null, null),
                new TkWord(KW_STDCALL, "__stdcall", null, null),
                new TkWord(0, "", null, null)
        };
        for (int i = 0; i < keywords.length; i++) {
            tkTable.add(keywords[i]);
        }
    }

    /**
     * 扫描整个源码字符串
     */
    public void scan() {
        if (sourceCode == null || sourceCode.length() == 0) {
            return;
        }

        init();
        getChar();
        while (curIndex < sourceCode.length()) {
            scanUnit();
            colorful(ILexState.LEX_NORMAL);
        }
        System.out.println("\n代码行数: " + lineNumber + "行");
        System.out.println("单词表大小: " + tkTable.size());
        System.out.println(fileName + "词法分析成功!");
    }

    /**
     * 扫描一个完整的字符单元
     */
    public void scanUnit() {
        preprocess();
        if (isLetterOrUnderscore(ch)) {
            parseIdentifier();
            TkWord tw = addIdentifier();
            syn = tw.tkCode;
        } else if (isDigit(ch)) {
            parseNumber();
            syn = TK_CINT;
        } else switch (ch) {
            case '+':
                getChar();
                syn = TK_PLUS;
                break;
            case '-':
                getChar();
                if (ch == '>') {        //指针
                    syn = TK_POINTSTO;
                    getChar();
                } else {
                    syn = TK_MINUS;
                }
                break;
            case '/':
                syn = TK_DIVIDE;
                break;
            case '%':
                syn = TK_MOD;
                break;
            case '=':
                getChar();
                if (ch == '=') {
                    syn = TK_EQ;
                    getChar();
                } else {
                    syn = TK_ASSIGN;
                }
                break;
            case '!':
                getChar();
                if (ch == '=') {
                    syn = TK_NEQ;
                    getChar();
                } else {
                    ErrorHandler.error(fileName, lineNumber, "暂时不支持'!'(非操作符)", tkTable);
                }
                break;
            case '<':
                getChar();
                if (ch == '=') {
                    syn = TK_LEQ;
                    getChar();
                } else {
                    syn = TK_LT;
                }
                break;
            case '>':
                getChar();
                if (ch == '=') {
                    syn = TK_GEQ;
                    getChar();
                } else {
                    syn = TK_GT;
                }
                break;
            case '.':
                getChar();
                if (ch == '.') {
                    getChar();
                    if (ch != '.') {
                        ErrorHandler.error(fileName, lineNumber, "省略号拼写错误", tkTable);
                    } else {
                        syn = TK_ELLIPSIS;
                        getChar();
                    }
                } else {
                    syn = TK_DOT;
                }
                break;
            case '&':
                syn = TK_AND;
                getChar();
                break;
            case ';':
                syn = TK_SEMICOLON;
                getChar();
                break;
            case ']':
                syn = TK_CLOSEBR;
                getChar();
                break;
            case '}':
                syn = TK_END;
                getChar();
                break;
            case ')':
                syn = TK_CLOSEPA;
                getChar();
                break;
            case '[':
                syn = TK_OPENBR;
                getChar();
                break;
            case '{':
                syn = TK_BEGIN;
                getChar();
                break;
            case ',':
                syn = TK_COMMA;
                getChar();
                break;
            case '(':
                syn = TK_OPENPA;
                getChar();
                break;
            case '*':
                syn = TK_STAR;
                getChar();
                break;
            case '\'':
                parseString(ch);
                syn = TK_CCHAR;
                break;
            case '\"':
                parseString(ch);
                syn = TK_CSTR;
                break;
            case '@':           // FIXME: 16-9-3 此处判断文件结尾的方式错误！
                syn = TK_EOF;
                break;
            default:
                ErrorHandler.error(fileName, lineNumber, "未知字符" + ch, tkTable);
                getChar();
                break;
        }
    }

    /**
     * 更新单词表(添加单词)
     *
     * @return
     */
    private TkWord addIdentifier() {
        TkWord result = null;
        for (int i = 0; i < tkTable.size(); i++) {
            if (tkTable.get(i).spelling.equals(tokenStr.toString())) {
                result = tkTable.get(i);
                break;
            }
        }
        if (result == null) {
            result = new TkWord(tkTable.size(), tokenStr.toString(), null, null);
            tkTable.add(result);
        }

        return result;
    }

    /**
     * 从源代码字符串中读取一个字符
     */
    public void getChar() {
        ch = sourceCode.charAt(curIndex++);
    }

    /**
     * 预处理，忽略空白字符及注释
     */
    private void preprocess() {
        while (true) {
            if (ch == ' ' || ch == '\t' || ch == '\n') {
                skipWhiteSpace();
            } else if (ch == '/') {             //可能是注释开始了
                getChar();
                if (ch == '*') {
                    System.out.print("/*");
                    parseComment();
                } else {
                    curIndex--;
                    ch = '/';
                    break;
                }
            } else {
                break;
            }
        }
    }

    /**
     * 解析注释
     */
    private void parseComment() {
        getChar();
        do {
            do {
                if (curIndex >= sourceCode.length() || ch == '\n' || ch == '*') {
                    break;
                } else {
                    System.out.print(ch);
                    getChar();
                }
            } while (true);
            if (ch == '\n') {
                System.out.println();
                lineNumber++;
                if (checkCurIndex()) {
                    getChar();
                } else {
                    ErrorHandler.error(fileName, lineNumber, "注释未关闭!", tkTable);
                    return;
                }
            } else if (ch == '*') {
                System.out.print("*");
                getChar();
                if (ch == '/') {
                    System.out.print("/");
                    getChar();
                    return;
                }
            } else {
                ErrorHandler.error(fileName, lineNumber, "注释未关闭!", tkTable);
                return;
            }
        } while (true);
    }

    /**
     * 检查当前位置是否已经越界
     *
     * @return 是否越界
     */
    private boolean checkCurIndex() {
        return (curIndex >= 0) && (curIndex < sourceCode.length());
    }

    /**
     * 忽略空格，缩进和回车
     * // TODO: 16-9-3 暂时只考虑Unix风格换行符
     */
    private void skipWhiteSpace() {
        while (ch == ' ' || ch == '\t' || ch == '\n') {
            if (ch == '\n') {
                lineNumber++;
            }
            System.out.print(ch);
            getChar();
        }
    }

    /**
     * 判断c是否为字母或下划线
     *
     * @param c 待判断的字符值
     * @return
     */
    private boolean isLetterOrUnderscore(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_');
    }

    /**
     * 判断c是否为数字
     *
     * @param c 字符值
     * @return
     */
    private boolean isDigit(char c) {
        return (c >= '0') && (c <= '9');
    }

    /**
     * 解析标识符
     */
    private void parseIdentifier() {
        tokenStr.setLength(0);
        tokenStr.append(ch);
        getChar();
        while (isLetterOrUnderscore(ch) || isDigit(ch)) {
            tokenStr.append(ch);
            getChar();
        }
    }

    /**
     * 解析整型常量
     * // TODO: 16-9-3 暂时只考虑十进制
     */
    private void parseNumber() {
        tokenStr.setLength(0);
        tokenStr.append(ch);
        getChar();
        while (isDigit(ch)) {
            tokenStr.append(ch);
            getChar();
        }
        if (ch == '.') {
            tokenStr.append(ch);
            getChar();
            while (isDigit(ch)) {
                tokenStr.append(ch);
                getChar();
            }
        }
        intVal = Integer.valueOf(tokenStr.toString());
    }

    /**
     * 解析字符常量，字符串常量
     */
    private void parseString(char sep) {
        tokenStr.setLength(0);
        sourceStr.setLength(0);
        sourceStr.append(sep);
        getChar();

        char c;
        for (; ; ) {
            if (ch == sep) {
                break;
            } else if (ch == '\\') {
                sourceStr.append(ch);
                getChar();
                switch (ch) {
                    case '0':
                        c = '\0';
                        break;
                    case 'a':
                        c = 'a';            // TODO: 16-9-3 Java中好像不支持\a!!!
                        break;
                    case 'b':
                        c = '\b';
                        break;
                    case 't':
                        c = '\t';
                        break;
                    case 'n':
                        c = '\n';
                        break;
                    case 'v':
                        c = 'v';            // TODO: 16-9-3 Java中好像不支持\v!!!
                        break;
                    case 'f':
                        c = '\f';
                        break;
                    case 'r':
                        c = '\r';
                        break;
                    case '\"':
                        c = '\"';
                        break;
                    case '\'':
                        c = '\'';
                        break;
                    case '\\':
                        c = '\\';
                        break;
                    default:
                        c = ch;
                        if (c >= '!' && c <= '~') {
                            ErrorHandler.warning(fileName, lineNumber,
                                    "非法转义字符: \'\\" + c + "\'", tkTable);
                        } else {
                            ErrorHandler.warning(fileName, lineNumber,
                                    "非法转义字符: \'\\0x" + c + "\'", tkTable);
                        }
                        break;
                }
                tokenStr.append(c);
                sourceStr.append(ch);
                getChar();
            } else {
                tokenStr.append(ch);
                sourceStr.append(ch);
                getChar();
            }
        }
        sourceStr.append(sep);
        getChar();
    }

    /**
     * 词法着色
     * // TODO: 16-9-3 之后完善终端着色程序！
     *
     * @param lexState
     */
    public void colorful(int lexState) {
        switch (lexState) {
            case ILexState.LEX_NORMAL:
                String p = getTokenStr(syn);
                if (syn >= TK_INDENT) {         //标识符为绿色
                    p = "\033[32m" + p + "\033[0m";
                } else if (syn >= KW_CHAR) {        //关键字为红色
                    p = "\033[31m" + p + "\033[0m";
                } else if (syn >= TK_CINT) {        //常量为黄色
                    p = "\033[33m" + p + "\033[0m";
                } else {            //算符界符的颜色

                }
                System.out.print(p);
                break;
        }
    }

    /**
     * 取得状态syn所代表的源码字符串
     *
     * @param syn 状态
     * @return 源码字符串
     */
    private String getTokenStr(int syn) {
        if (syn > tkTable.size()) {
            return "";
        } else if (syn > TK_CINT && syn <= TK_CSTR) {
            return sourceStr.toString();
        } else if (syn == TK_CINT) {
            return intVal + "";
        } else {
            return tkTable.get(syn).spelling;
        }
    }
}
