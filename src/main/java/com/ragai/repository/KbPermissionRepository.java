/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai.repository;

import com.ragai.entity.KbPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KbPermissionRepository extends JpaRepository<KbPermission, Long> {

    List<KbPermission> findBySubjectTypeAndSubjectId(String subjectType, String subjectId);

    boolean existsByKbIdAndSubjectTypeAndSubjectId(
            Long kbId, String subjectType, String subjectId);

    boolean existsByKbIdAndSubjectTypeAndSubjectIdAndPermissionIn(
            Long kbId, String subjectType, String subjectId, List<String> permissions);

    List<KbPermission> findByKbId(Long kbId);

    void deleteByKbId(Long kbId);
}