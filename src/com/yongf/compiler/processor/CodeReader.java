/*
 * Copyright (C) 1996-2016 YONGF Inc.All Rights Reserved.
 * Scott Wang blog.54yongf.com | blog.csdn.net/yongf2014	
 * 文件名：CodeReader
 * 描述：
 * 
 * 修改历史
 * 版本号    作者                     日期                    简要描述
 *  1.0         Scott Wang         16-9-1             新增：Create	
 */

package com.yongf.compiler.processor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 负责读取源代码
 *
 * @author Scott Wang
 * @version 1.0, 16-9-1
 * @see
 * @since JSC 1.0
 */
public class CodeReader {

    //源代码路径
    private String mFilePath;

    public CodeReader(String filePath) {
        mFilePath = filePath;
    }

    /**
     * 读取源代码文件
     *
     * @return 源代码字符串
     */
    public String read() {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        String str;
        try {
            reader = new BufferedReader(new FileReader(mFilePath));
            while ((str = reader.readLine()) != null) {
                String separator = System.getProperty("line.separator", "\n");
                sb.append(str).append(separator);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }
}
