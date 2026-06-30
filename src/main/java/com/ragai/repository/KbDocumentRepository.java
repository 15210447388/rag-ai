/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai.repository;

import com.ragai.entity.KbDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KbDocumentRepository extends JpaRepository<KbDocument, Long> {

    List<KbDocument> findByKbIdAndIsDeletedFalse(Long kbId);

    @Query("SELECT COUNT(d) FROM KbDocument d WHERE d.status = :status")
    long countByStatus(KbDocument.DocumentStatus status);
}