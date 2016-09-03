/*
 * Copyright (C) 1996-2016 YONGF Inc.All Rights Reserved.
 * Scott Wang blog.54yongf.com | blog.csdn.net/yongf2014	
 * 文件名：LexicalAnalyzer						
 * 描述：
 *
 * 修改历史
 * 版本号    作者                     日期                    简要描述
 *  1.0         Scott Wang         16-9-1             新增：Create	
 */

package com.yongf.compiler.processor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 词法分析器
 *
 * @author Scott Wang
 * @version 1.0, 16-9-1
 * @see
 * @since JSC 1.0
 */
public class LexicalAnalyzer {

    private final String RESULT = "/home/scottwang/桌面/result.txt";
    //关键字数组
    private final String[] mKeyword1 = {"begin", "if", "then", "while", "do", "end"};
    private final String[] mKeyword2 = {"const", "var", "procedure", "call"};

    //源代码
    private String mSrccode;
    //单个字符单元（标识符，变量，数字，关键字，算符，界符等）
    private StringBuilder mToken = new StringBuilder();
    //每次读取的一个字符
    private char mChar;
    //各类字符单元对应的数字（事先定义）
    private int mSyn;
    //记录当前扫描到源码的位置
    private int mCur;
    //记录行号
    private int mLineNumber;
    //计算单个数字的大小（暂时只考虑整数）
    private int mSum;

    //词法分析结果
    private StringBuilder result = new StringBuilder();

    public LexicalAnalyzer(String code) {
        mSrccode = code;
    }

    /**
     * 扫描整个源代码进行词法分析
     */
    public void scan() {
        if (mSrccode == null || mSrccode.length() == 0) {
            return;
        }

        mLineNumber = 1;
        result.append("词法分析的结果为：\n");
        while (mCur < mSrccode.length()) {
            scanUnit();
            switch (mSyn) {
                case 11:
                    result.append("(" + mSyn + " => " + mSum + ")\n");
                    break;
                case -1:
                    result.append("Error in line " + mLineNumber + "!\n");
                    break;
                case -2:
                    mLineNumber++;
                    break;
                default:
                    result.append("(" + mSyn + " => " + mToken.toString() + ")\n");
                    break;
            }
        }
        result.append("\n词法分析完毕！\n");
    }

    /**
     * 保存词法分析的结果到磁盘上
     */
    public void storeResult() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(RESULT));
            writer.write(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 每次扫描从源代码中扫描一个字符单元
     */
    private void scanUnit() {
        mToken.setLength(0);        //mToken清空
        mSum = 0;           //数字清零

        //去除空格
        while ((mChar = mSrccode.charAt(mCur++)) == ' ') ;

        //规定变量（关键字）只能以字母开头
        if ((mChar >= 'a' && mChar <= 'z') || (mChar >= 'A' && mChar <= 'Z')) {
            //变量中可以包含字母，数字
            while ((mChar >= '0' && mChar <= '9') || (mChar >= 'a' && mChar <= 'z')
                    || (mChar >= 'A' && mChar <= 'Z')) {
                mToken.append(mChar);
                mChar = mSrccode.charAt(mCur++);
            }
            mCur--;             //变量所在字符单元结束，需要回退一步
            mSyn = 10;          //先假定为普通变量（即非关键字）

            //判断该变量是否为关键字
            for (int i = 0; i < mKeyword1.length; i++) {
                if (mToken.toString().equals(mKeyword1[i])) {
                    mSyn = i + 1;
                    break;
                }
            }
            for (int i = 0; i < mKeyword2.length; i++) {
                if (mToken.toString().equals(mKeyword2[i])) {
                    mSyn = i + 31;
                    break;
                }
            }
        } else if (mChar >= '0' && mChar <= '9') {              //如果是数字
            while (mChar >= '0' && mChar <= '9') {
                mSum = mSum * 10 + mChar;
                mChar = mSrccode.charAt(mCur++);
            }
            mCur--;             //数字所在字符单元结束，回退一步
            mSyn = 11;
        } else switch (mChar) {                 //如果是其他字符
            case '<':
                mToken.append(mChar);
                mChar = mSrccode.charAt(mCur++);
                if (mChar == '>') {         //不等号
                    mSyn = 21;
                    mToken.append(mChar);
                } else if (mChar == '=') {          //小于等于
                    mSyn = 22;
                    mToken.append(mChar);
                } else {        //只是一个小于号
                    mSyn = 23;
                    mCur--;         //序号回退
                }

                break;
            case '>':
                mToken.append(mChar);
                mChar = mSrccode.charAt(mCur++);
                if (mChar == '=') {         //大于等于
                    mSyn = 24;
                    mToken.append(mChar);
                } else {                    //只是一个大于号
                    mSyn = 20;
                    mCur--;             //序号回退
                }

                break;
            case ':':
                mToken.append(mChar);
                mChar = mSrccode.charAt(mCur++);
                if (mChar == '=') {         //赋值运算符
                    mSyn = 18;
                    mToken.append(mChar);
                } else {                    //仅仅是一个:
                    mSyn = 17;
                    mCur--;
                }

                break;
            case '*':
                mSyn = 13;
                mToken.append(mChar);

                break;
            case '/':
                mSyn = 14;
                mToken.append(mChar);

                break;
            case '+':
                mSyn = 15;
                mToken.append(mChar);

                break;
            case '-':
                mSyn = 16;
                mToken.append(mChar);

                break;
            case '=':
                mSyn = 25;
                mToken.append(mChar);

                break;
            case ';':
                mSyn = 26;
                mToken.append(mChar);

                break;
            case '(':
                mSyn = 27;
                mToken.append(mChar);

                break;
            case ')':
                mSyn = 28;
                mToken.append(mChar);

                break;
            case ',':
                mSyn = 29;
                mToken.append(mChar);

                break;
            case '!':
                mSyn = 30;
                mToken.append(mChar);

                break;
            case '.':
                mSyn = 0;
                mToken.append(mChar);

                break;
            case '\n':
                mSyn = -2;

                break;
            default:                //出现未定义字符，直接报错
                mSyn = -1;

                break;
        }
    }
}
