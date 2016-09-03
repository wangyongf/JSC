/*
 * Copyright (C) 1996-2016 YONGF Inc.All Rights Reserved.
 * Scott Wang blog.54yongf.com | blog.csdn.net/yongf2014	
 * 文件名：TokenCode						
 * 描述：
 * 
 * 修改历史
 * 版本号    作者                     日期                    简要描述
 *  1.0         Scott Wang         16-9-2             新增：Create	
 */

package com.yongf.compiler.bean;

/**
 * 单词编码定义
 *
 * @author Scott Wang
 * @version 1.0, 16-9-2
 * @see
 * @since JSC 1.0
 */
public interface ITokenCode {

    /*算符界符*/
    int TK_PLUS = 0;                //+加号
    int TK_MINUS = 1;               //-减号
    int TK_STAR = 2;                    //*星号
    int TK_DIVIDE = 3;                  ///除号
    int TK_MOD = 4;                 //%求余运算符
    int TK_EQ = 5;                      //==等于号
    int TK_NEQ = 6;                     //!=不等于号
    int TK_LT = 7;                      //<小于号
    int TK_LEQ = 8;                     //<=小于等于号
    int TK_GT = 9;                          //>大于号
    int TK_GEQ = 10;                         //>=大于等于号
    int TK_ASSIGN = 11;                  //=赋值运算符
    int TK_POINTSTO = 12;                //->指向结构体成员的运算符
    int TK_DOT = 13;                          //.结构体成员运算符
    int TK_AND = 14;                         //&地址与运算符
    int TK_OPENPA = 15;                  //(左小括号
    int TK_CLOSEPA = 16;                 //)右小括号
    int TK_OPENBR = 17;                  //[左中括号
    int TK_CLOSEBR = 18;                 //]右中括号
    int TK_BEGIN = 19;                       //{左大括号
    int TK_END = 20;                             //}右大括号
    int TK_SEMICOLON = 21;               //;分号
    int TK_COMMA = 22;                       //,逗号
    int TK_ELLIPSIS = 23;                        //...省略号
    int TK_EOF = 24;                             //文件结束符

    /*常量*/
    int TK_CINT = 25;                        //整型常量
    int TK_CCHAR = 26;                    //字符常量
    int TK_CSTR = 27;                        //字符串常量

    /*关键字*/
    int KW_CHAR = 28;                    //char关键字
    int KW_SHORT = 29;                   //short关键字
    int KW_INT = 30;                         //int关键字
    int KW_VOID = 31;                        //void关键字
    int KW_STRUCT = 32;                  //struct关键字
    int KW_IF = 33;                              //if关键字
    int KW_ELSE = 34;                            //else关键字
    int KW_FOR = 35;                             //for关键字
    int KW_CONTINUE = 36;                //continue关键字
    int KW_BREAK = 37;                       //break关键字
    int KW_RETURN = 38;                      //return关键字
    int KW_SIZEOF = 39;                          //sizeof关键字

    int KW_ALIGN = 40;                           //__align关键字
    int KW_CDECL = 41;                           //__cdecl关键字
    int KW_STDCALL = 42;                     //__stdcall关键字

    /*标识符*/
    int TK_INDENT = 43;
}
