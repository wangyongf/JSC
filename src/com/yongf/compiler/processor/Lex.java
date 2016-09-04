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

import com.yongf.compiler.bean.TkWord;

import java.util.ArrayList;
import java.util.List;

import static com.yongf.compiler.bean.ILexState.LEX_NORMAL;
import static com.yongf.compiler.bean.IStorageClass.*;
import static com.yongf.compiler.bean.ISyntaxState.*;
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

    /**
     * 语法状态
     */
    private int syntaxState = SNTX_NUL;

    /**
     * 缩进级别
     */
    private int syntaxLevel = 0;

    private StringBuilder errorMsg = new StringBuilder();

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
     * 扫描整个源码字符串(语法分析)
     */
    public void scan() {
        if (sourceCode == null || sourceCode.length() == 0) {
            return;
        }

        init();
        getChar();
        scanUnit();
        translationUnit();
        System.out.println("\n代码行数: " + lineNumber + "行");
        System.out.println("单词表大小: " + tkTable.size());
        System.out.println(fileName + "语法分析完毕!");
    }

    /**
     * 扫描整个源码字符串(词法分析，但是已经不可用！)
     */
    public void scan0() {
        if (sourceCode == null || sourceCode.length() == 0) {
            return;
        }

        init();
        getChar();
        while (curIndex < sourceCode.length()) {
            scanUnit();
            colorful(LEX_NORMAL);
        }
        System.out.println("\n代码行数: " + lineNumber + "行");
        System.out.println("单词表大小: " + tkTable.size());
        System.out.println(fileName + "词法分析完毕!");
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
                    ErrorHandler.error(errorMsg, fileName, lineNumber, "暂时不支持'!'(非操作符)", tkTable);
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
                        ErrorHandler.error(errorMsg, fileName, lineNumber, "省略号拼写错误", tkTable);
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
            case '\n':              //正常情况下，\n换行符都被过滤掉了，这里出现了，说明到达文件尾了
                syn = TK_EOF;
                break;
            default:
                ErrorHandler.error(errorMsg, fileName, lineNumber, "未知字符" + ch, tkTable);
                getChar();
                break;
        }
        syntaxIndent();
    }

    /**
     * 语法缩进
     */
    private void syntaxIndent() {
        switch (syntaxState) {
            case SNTX_NUL:
                colorful(LEX_NORMAL);
                break;
            case SNTX_SP:
                System.out.print(" ");
                colorful(LEX_NORMAL);
                break;
            case SNTX_LF_HT:
                if (syn == TK_END) {
                    syntaxLevel--;              //遇到'}'，缩进减少一级
                }
                System.out.println();
                printIndent(syntaxLevel);

                colorful(LEX_NORMAL);

                break;
            case SNTX_DELAY:
                break;
        }
        syntaxState = SNTX_NUL;
    }

    /**
     * 缩进n次
     * <p>
     * // TODO: 16-9-4 后面考虑缩进方式：空格 or Tab
     *
     * @param n 缩进次数
     */
    private void printIndent(int n) {
        for (int i = 0; i < n; i++) {
            System.out.print("    ");           //打印缩进
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
        if (checkCurIndex()) {
            ch = sourceCode.charAt(curIndex++);
        }
    }

    /**
     * 预处理，忽略空白字符及注释
     */
    private void preprocess() {
        while (checkCurIndex()) {
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
                    ErrorHandler.error(errorMsg, fileName, lineNumber, "注释未关闭!", tkTable);
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
                ErrorHandler.error(errorMsg, fileName, lineNumber, "注释未关闭!", tkTable);
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
        char pre = ch;
        while (checkCurIndex() && (ch == ' ' || ch == '\t' || ch == '\n')) {
            int count = 0;
            while (checkCurIndex() && (pre == ch)) {
                getChar();
                count++;
            }
            System.out.print(pre);
            if (pre == '\n') {
                lineNumber += count;
            }
            pre = ch;
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
                            ErrorHandler.warning(errorMsg, fileName, lineNumber,
                                    "非法转义字符: \'\\" + c + "\'", tkTable);
                        } else {
                            ErrorHandler.warning(errorMsg, fileName, lineNumber,
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
            case LEX_NORMAL:
                String p = getTokenStr(syn);
                if (syn == TK_EOF) {            //文件结束符
                    p = "\n" + p;
                } else if (syn >= TK_INDENT) {         //标识符为绿色
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


    /**
     * <translation_unit::={<external_declaration>}<TK_EOF></TK_EOF>
     */
    public void translationUnit() {
//        while (syn != TK_EOF) {
        while (curIndex < sourceCode.length()) {
            externalDeclaration(SC_GLOBAL);
        }
    }

    /**
     * 解析外部声明
     *
     * @param type 存储类型，局部or全局
     */
    public void externalDeclaration(int type) {
        if (!typeSpecifier()) {
            ErrorHandler.expect(errorMsg, fileName, lineNumber, "<类型区分符>", tkTable);
        }

        if (syn == TK_SEMICOLON) {
            scanUnit();
            return;
        }
        while (true) {          //逐步分析声明或函数定义
            declarator();
            if (syn == TK_BEGIN) {
                if (type == SC_LOCAL) {
                    ErrorHandler.error(errorMsg, fileName, lineNumber, "不支持函数嵌套定义", tkTable);
                }
                functionBody();
                break;
            } else {
                if (syn == TK_ASSIGN) {
                    scanUnit();
                    initializer();
                }

                if (syn == TK_COMMA) {
                    scanUnit();
                } else {
                    if (checkCurIndex() && sourceCode.charAt(curIndex) != '\n') {
                        syntaxState = SNTX_LF_HT;
                    }
                    skip(TK_SEMICOLON);
                    break;
                }
            }
        }
    }

    /**
     * 解析类型区分符
     * <p>
     * <type_specifier>::=<KW_INT>
     * |<KW_CHAR>
     * |<KW_SHORT>
     * |<KW_VOID>
     * |<struct_specifier>
     *
     * @return true-发现合法的类型区分符
     */
    public boolean typeSpecifier() {
        boolean result = false;
        switch (syn) {
            case KW_CHAR:
                result = true;
                syntaxState = SNTX_SP;
                scanUnit();
                break;
            case KW_SHORT:
                result = true;
                syntaxState = SNTX_SP;
                scanUnit();
                break;
            case KW_VOID:
                result = true;
                syntaxState = SNTX_SP;
                scanUnit();
                break;
            case KW_INT:
                result = true;
                syntaxState = SNTX_SP;
                scanUnit();
                break;
            case KW_STRUCT:
                result = true;
                syntaxState = SNTX_SP;
                structSpecifier();
                break;
            default:
                break;
        }

        return result;
    }

    /**
     * 结构区分符
     * <p>
     * <struct_specifier>::=
     * <KW_STRUCT><IDENTIFIER><TK_BEGIN><struct_declaration_list><TK_END>
     * |<KW_STRUCT><IDENTIFIER>
     */
    public void structSpecifier() {
        scanUnit();
        int v = syn;

        syntaxState = SNTX_DELAY;               //延迟到取出下一个单词之后确定输出格式
        scanUnit();
        if (syn == TK_BEGIN) {          //结构体定义
            syntaxState = SNTX_LF_HT;
        } else if (syn == TK_CLOSEPA) {             //sizeof(struct struct_name)
            syntaxState = SNTX_NUL;
        } else {
            syntaxState = SNTX_SP;              //结构变量声明
        }
        syntaxIndent();

        if (v < TK_INDENT) {            //关键字不能作为结构体名称
            ErrorHandler.expect(errorMsg, fileName, lineNumber, "结构体名", tkTable);
        }

        if (syn == TK_BEGIN) {
            structDeclarationList();
        }
    }

    /**
     * 结构声明符表
     * <p>
     * <struct_declaration_list>::=<struct_declaration>{<struct_declaration>}
     */
    public void structDeclarationList() {
        int maxAlign, offset;

        syntaxState = SNTX_LF_HT;
        syntaxLevel++;

        scanUnit();
        while (syn != TK_END) {
            structDeclaration();
        }
        skip(TK_END);

        syntaxState = SNTX_LF_HT;
    }

    /**
     * <struct_declaration>::=
     * <type_specifier><declarator>{<TK_COMMA><declarator>}
     * <TK_SEMICOLON>
     */
    public void structDeclaration() {
        typeSpecifier();
        while (true) {
            declarator();

            if (syn == TK_SEMICOLON) {
                break;
            }
            skip(TK_COMMA);
        }
        syntaxState = SNTX_LF_HT;
        skip(TK_SEMICOLON);
    }

    /**
     * 函数调用约定：__cdecl, __stdcall, __fastcall
     * <p>
     * <function_calling_convention>::=<KW_CDECL>|<KW_STDCALL>
     *
     * @param fc 函数调用约定
     */
    public void functionCallingConvention(int[] fc) {
        fc[0] = KW_CDECL;               //默认函数调用约定
        if (syn == KW_CDECL || syn == KW_STDCALL) {
            fc[0] = syn;
            syntaxState = SNTX_SP;
            scanUnit();
        }
    }

    /**
     * 结构成员对齐
     * <p>
     * <struct_member_alignment>::=<KW_ALIGN><TK_OPENPA><TK_CINT><TK_CLOSEPA>
     */
    public void structMemberAlignment() {
        if (syn == KW_ALIGN) {
            scanUnit();
            skip(TK_OPENPA);
            if (syn == TK_CINT) {
                scanUnit();
            } else {
                ErrorHandler.expect(errorMsg, fileName, lineNumber, "整数常量", tkTable);
            }
            skip(TK_CLOSEPA);
        }
    }

    /**
     * 声明符
     * <p>
     * <declarator>::={<TK_STAR>}[<function_calling_convention>]
     * [<struct_member_alignment>]<direct_declarator>
     */
    public void declarator() {
        int[] fc = new int[1];
        while (syn == TK_STAR) {
            scanUnit();
        }
        functionCallingConvention(fc);
        structMemberAlignment();
        directDeclarator();
    }

    /**
     * 直接声明符
     * <p>
     * <direct_declarator>::=<IDENTIFIER><direct_declarator_postfix>
     */
    public void directDeclarator() {
        if (syn >= TK_INDENT) {
            scanUnit();
        } else {
            ErrorHandler.expect(errorMsg, fileName, lineNumber, "标识符", tkTable);
        }
        directDeclaratorPostfix();
    }

    /**
     * 直接声明符后缀
     * <p>
     * <direct_declarator_postfix>::={<TK_OPENBR><TK_CINT><TK_CLOSEBR>
     * |<TK_OPENBR><TK_CLOSEBR>
     * |<TK_OPENPA><parameter_type_list><TK_CLOSEPA>
     * |<TK_OPENPA><TK_CLOSEPA>}
     */
    public void directDeclaratorPostfix() {
        int n;
        if (syn == TK_OPENPA) {
            parameterTypeList();
        } else if (syn == TK_OPENBR) {
            scanUnit();
            if (syn == TK_CINT) {
                scanUnit();
                n = intVal;
            }
            skip(TK_CLOSEBR);
            directDeclaratorPostfix();
        }
    }

    /**
     * 解析形参类型表
     * <p>
     * <parameter_type_list>::=<type_specifier>{<declarator>}
     * {<TK_COMMA><type_specifier>{<declarator>}}{<TK_COMMA><TK_ELLIPSIS>}
     *
     * @param
     */
    public void parameterTypeList() {
        scanUnit();
        while (syn != TK_CLOSEPA) {
            if (!typeSpecifier()) {
                ErrorHandler.error(errorMsg, fileName, lineNumber, "无效类型标识符", tkTable);
            }
            declarator();
            if (syn == TK_CLOSEPA) {
                break;
            }
            skip(TK_COMMA);
            if (syn == TK_ELLIPSIS) {
//                func_call = KW_CDECL;
                scanUnit();
                break;
            }
        }

        syntaxState = SNTX_DELAY;
        skip(TK_CLOSEPA);
        if (syn == TK_BEGIN && sourceCode.charAt(curIndex) != '\n') {          //函数定义
            syntaxState = SNTX_LF_HT;
        } else {            //函数声明
            syntaxState = SNTX_NUL;
        }
        syntaxIndent();
    }

    /**
     * 函数体
     * <p>
     * <funcbody>::=<compound_statement>
     */
    public void functionBody() {
        int[] bsym = new int[1];
        int[] csym = new int[1];
        compoundStatement(bsym, csym);
    }

    /**
     * 初值符
     * <p>
     * <initializer>::=<assignment_expression>
     */
    public void initializer() {
        assignmentExpression();
    }

    /**
     * 语句
     * <p>
     * <statement>::=<compound_statement>
     * |<if_statement>
     * |<return_statement>
     * |<break_statement>
     * |<continue_statement>
     * |<for_statement>
     * |<expression_statement>
     */
    public void statement(int[] bsym, int[] csym) {
        switch (syn) {
            case TK_BEGIN:
                compoundStatement(bsym, csym);
                break;
            case KW_IF:
                ifStatement(bsym, csym);
                break;
            case KW_RETURN:
                returnStatement();
                break;
            case KW_BREAK:
                breakStatement();
                break;
            case KW_CONTINUE:
                continueStatement();
                break;
            case KW_FOR:
                forStatement(bsym, csym);
                break;
            default:
                expressionStatement();
                break;
        }
    }

    /**
     * 复合语句
     * <p>
     * <compound_statement>::=<TK_BEGIN>{<declaration>}{<statement>}<TK_END>
     */
    public void compoundStatement(int[] bsym, int[] csym) {
        syntaxState = SNTX_LF_HT;
        syntaxLevel++;              //复合语句，缩进增加一级

        scanUnit();
        while (isTypeSpecifier(syn)) {
            externalDeclaration(SC_LOCAL);
        }
        while (syn != TK_END) {
            statement(bsym, csym);
        }

        syntaxState = SNTX_LF_HT;
        scanUnit();
    }

    /**
     * 判断是否为类型区分符
     *
     * @param syn
     * @return true-是类型区分符
     */
    private boolean isTypeSpecifier(int syn) {
        switch (syn) {
            case KW_CHAR:
            case KW_SHORT:
            case KW_INT:
            case KW_VOID:
            case KW_STRUCT:
                return true;
            default:
                return false;
        }
    }

    /**
     * 表达式语句
     * <p>
     * <expression_statement>::=<TK_SEMICOLON>|<expression><TK_SEMICOLON>
     */
    private void expressionStatement() {
        if (syn != TK_SEMICOLON) {
            expression();
        }
        syntaxState = SNTX_LF_HT;
        skip(TK_SEMICOLON);
    }

    /**
     * 选择语句
     * <p>
     * <if_statement>::=<KW_IF><TK_OPENPA><expression>
     * <TK_CLOSEPA><statement>[<KW_ELSE><statement>]
     */
    private void ifStatement(int[] bsym, int[] csym) {
        syntaxState = SNTX_SP;
        scanUnit();
        skip(TK_OPENPA);
        expression();
        syntaxState = SNTX_LF_HT;
        skip(TK_CLOSEPA);
        statement(bsym, csym);
        if (syn == KW_ELSE) {
            syntaxState = SNTX_LF_HT;
            scanUnit();
            statement(bsym, csym);
        }
    }

    /**
     * 循环语句
     * <p>
     * <for_statement>::=<KW_FOR><TK_OPENPA><expression_statement>
     * <expression_statement><expression><TK_CLOSEPA><statement>
     */
    private void forStatement(int[] bsym, int[] csym) {
        scanUnit();
        skip(TK_OPENPA);
        if (syn != TK_SEMICOLON) {
            expression();
        }
        skip(TK_SEMICOLON);
        if (syn != TK_SEMICOLON) {
            expression();
        }
        skip(TK_SEMICOLON);
        if (syn != TK_CLOSEPA) {
            expression();
        }
        syntaxState = SNTX_LF_HT;
        skip(TK_CLOSEPA);
        statement(bsym, csym);
    }

    /**
     * 跳转语句
     * <p>
     * <continue_statement>::=<KW_CONTINUE><TK_SEMICOLON>
     */
    private void continueStatement() {
        scanUnit();
        syntaxState = SNTX_LF_HT;
        skip(TK_SEMICOLON);
    }

    /**
     * break语句
     * <p>
     * <break_statement>::=<KW_BREAK><TK_SEMICOLON>
     */
    private void breakStatement() {
        scanUnit();
        syntaxState = SNTX_LF_HT;
        skip(TK_SEMICOLON);
    }

    /**
     * return语句
     * <p>
     * <return_statement>::=<KW_RETURN><TK_SEMICOLON>
     * |<KW_RETURN><expression><TK_SEMICOLON>
     */
    private void returnStatement() {
        syntaxState = SNTX_DELAY;
        scanUnit();
        if (syn == TK_SEMICOLON) {          //return;
            syntaxState = SNTX_NUL;
        } else {                                                    //return <expression>;
            syntaxState = SNTX_SP;
        }
        syntaxIndent();

        if (syn != TK_SEMICOLON) {
            expression();
        }
        syntaxState = SNTX_LF_HT;
        skip(TK_SEMICOLON);
    }

    /**
     * 表达式
     * <p>
     * <expression>::=<assignment_expression>{<TK_COMMA><assignment_expression>}
     */
    private void expression() {
        while (true) {
            assignmentExpression();
            if (syn != TK_COMMA) {
                break;
            }
            scanUnit();
        }
    }

    /**
     * 此处进行了非等价变换，将语言的范围扩大了
     * <p>
     * <assignment_expression>::=<equality_expression>
     * {<TK_ASSIGN><assignment_expression>}
     */
    private void assignmentExpression() {
        equalityExpression();
        if (syn == TK_ASSIGN) {
            scanUnit();
            assignmentExpression();
        }
    }

    /**
     * 相等类表达式
     * <p>
     * <equality_expression>::=<relational_expression>
     * |{<TK_EQ><relational_expression>}
     * |{<TK_NEQ><relational_expression>}
     */
    private void equalityExpression() {
        relationalExpression();
        while (syn == TK_EQ || syn == TK_NEQ) {
            scanUnit();
            relationalExpression();
        }
    }

    /**
     * 关系表达式
     * <p>
     * <relational_expression>::=<additive_expression>{
     * <TK_LT><additive_expression>
     * |<TK_GT><additive_expression>
     * |<TK_LEQ><additive_expression>
     * |<TK_GEQ><additive_expression>
     * }
     */
    private void relationalExpression() {
        additiveExpression();
        while (syn == TK_LT || syn == TK_LEQ ||
                syn == TK_GT || syn == TK_GEQ) {
            scanUnit();
            additiveExpression();
        }
    }

    /**
     * 加减类表达式
     * <p>
     * <additive_expression>::=<multiplicative_expression>
     * {<TK_PLUS><multiplicative_expression>
     * |<TK_MINUS><multiplicative_expression>}
     */
    private void additiveExpression() {
        multiplicativeExpression();
        while (syn == TK_PLUS || syn == TK_MINUS) {
            scanUnit();
            multiplicativeExpression();
        }
    }

    /**
     * 乘除类表达式
     * <p>
     * <multiplicative_expression>::=<unary_expression>
     * {<TK_STAR><unary_expression>
     * |<TK_DIVIDE><unary_expression>
     * |<TK_MOD><unary_expression>}
     */
    private void multiplicativeExpression() {
        unaryExpression();
        while (syn == TK_STAR || syn == TK_DIVIDE || syn == TK_MOD) {
            scanUnit();
            unaryExpression();
        }
    }

    /**
     * 一元表达式
     * <p>
     * <unary_expression>::=<postfix_expression>
     * |<TK_AND><unary_expression>
     * |<TK_STAR><unary_expression>
     * |<TK_PLUS><unary_expression>
     * |<TK_MINUS><unary_expression>
     * |<sizeof_expression>
     */
    private void unaryExpression() {
        switch (syn) {
            case TK_AND:
                scanUnit();
                unaryExpression();
                break;
            case TK_STAR:
                scanUnit();
                unaryExpression();
                break;
            case TK_PLUS:
                scanUnit();
                unaryExpression();
                break;
            case TK_MINUS:
                scanUnit();
                unaryExpression();
                break;
            case KW_SIZEOF:
                sizeofExpression();
                break;
            default:
                postfixExpression();
                break;
        }
    }

    /**
     * sizeof表达式
     * <p>
     * <sizeof_expression>::=
     * <KW_SIZEOF><TK_OPENPA><type_specifier><TK_CLOSEPA>
     */
    private void sizeofExpression() {
        scanUnit();
        skip(TK_OPENPA);
        typeSpecifier();
        skip(TK_CLOSEPA);
    }

    /**
     * 后缀表达式
     * <p>
     * <postfix_expression>::=<primary_expression>
     * {<TK_OPENBR><expression><TK_CLOSEBR>
     * |<TK_OPENPA><TK_CLOSEPA>
     * |<TK_OPENPA><argument_expression_list><TK_CLOSEPA>
     * |<TK_DOT><IDENTIFIER>
     * |<TK_POINTSTO><IDENTIFIER>}
     */
    private void postfixExpression() {
        primaryExpression();
        while (true) {
            if (syn == TK_DOT || syn == TK_POINTSTO) {
                scanUnit();
                syn |= SC_MEMBER;
                scanUnit();
            } else if (syn == TK_OPENBR) {
                scanUnit();
                expression();
                skip(TK_CLOSEBR);
            } else if (syn == TK_OPENPA) {
                argumentExpressionList();
            } else {
                break;
            }
        }
    }

    /**
     * 初值表达式
     * <p>
     * <primary_expression>::=<IDENTIFIER>
     * |<TK_CINT>
     * |<TK_CSTR>
     * |<TK_CCHAR>
     * |<TK_OPENPA><expression><TK_CLOSEPA>
     */
    private void primaryExpression() {
        int t;
        switch (syn) {
            case TK_CINT:
            case TK_CCHAR:
                scanUnit();
                break;
            case TK_CSTR:
                scanUnit();
                break;
            case TK_OPENPA:
                scanUnit();
                expression();
                skip(TK_CLOSEPA);
                break;
            default:
                t = syn;
                scanUnit();
                if (t < TK_INDENT) {
                    ErrorHandler.expect(errorMsg, fileName, lineNumber, "标识符或常量", tkTable);
                }
                break;
        }
    }

    /**
     * 实参表达式表
     * <p>
     * <argument_expression_list>::=<assignment_expression>
     * {<TK_COMMA><assignment_expression>}
     */
    private void argumentExpressionList() {
        scanUnit();
        if (syn != TK_CLOSEPA) {
            for (; ; ) {
                assignmentExpression();
                if (syn == TK_CLOSEPA) {
                    break;
                }
                skip(TK_COMMA);
            }
        }
        skip(TK_CLOSEPA);
    }

    /**
     * 跳过单词c，取下一单词，如果当前单词不是c，提示错误
     *
     * @param c 要跳过的单词
     */
    private void skip(int c) {
        if (syn != c) {
            ErrorHandler.error(errorMsg, fileName, lineNumber, "缺少\'" +
                    getTokenStr(c) + "\'", tkTable);
        }
        scanUnit();
    }
}
