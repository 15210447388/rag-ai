/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai.common.paredocument;

import java.io.InputStream;

public interface DocumentParser {

    /** 支持的文件类型（大写），如 "PDF"、"DOCX" */
    String supportedType();

    /** 解析文件，返回解析结果 */
    ParseResult parse(InputStream inputStream, String fileName);
}