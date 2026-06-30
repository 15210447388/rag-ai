/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai.repository;

import com.ragai.entity.IndexTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndexTaskRepository extends JpaRepository<IndexTask, Long> {

    Optional<IndexTask> findTopByDocIdOrderByCreatedAtDesc(Long docId);
}