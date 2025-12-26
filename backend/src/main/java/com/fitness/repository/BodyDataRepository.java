package com.fitness.repository;

import com.fitness.entity.BodyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 身体数据Repository
 */
@Repository
public interface BodyDataRepository extends JpaRepository<BodyData, Long> {

    List<BodyData> findByUserIdOrderByRecordDateDesc(Long userId);

    List<BodyData> findTop30ByUserIdOrderByRecordDateDesc(Long userId);
}
