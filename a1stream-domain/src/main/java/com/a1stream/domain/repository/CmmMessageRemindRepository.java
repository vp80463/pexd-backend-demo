package com.a1stream.domain.repository;

import com.a1stream.domain.entity.CmmMessageRemind;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author dong zhen
 */
@Repository
public interface CmmMessageRemindRepository extends JpaExtensionRepository<CmmMessageRemind, Long> {

    List<CmmMessageRemind> findBySiteIdAndReadType(String siteId, String readType);

    List<CmmMessageRemind> findByMessageRemindIdIn(List<Long> messageRemindIdList);
}
