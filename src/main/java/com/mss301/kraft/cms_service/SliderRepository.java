package com.mss301.kraft.cms_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface SliderRepository extends JpaRepository<Slider, UUID> {
    List<Slider> findAllByActiveTrueOrderByPositionAscCreatedAtAsc();

    Page<Slider> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
