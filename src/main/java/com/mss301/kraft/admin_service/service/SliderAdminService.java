package com.mss301.kraft.admin_service.service;

import com.mss301.kraft.admin_service.dto.SliderDtos.SliderRequest;
import com.mss301.kraft.admin_service.dto.SliderDtos.SliderResponse;
import com.mss301.kraft.cms_service.Slider;
import com.mss301.kraft.cms_service.SliderRepository;
import org.springframework.stereotype.Service;
import com.mss301.kraft.admin_service.dto.SliderPageResponse;

import java.util.List;
import java.util.UUID;

@Service
public class SliderAdminService {

    private final SliderRepository sliderRepository;

    public SliderAdminService(SliderRepository sliderRepository) {
        this.sliderRepository = sliderRepository;
    }

    public List<SliderResponse> list() {
        return sliderRepository.findAll().stream().map(this::toResponse).toList();
    }

    public SliderPageResponse listPaged(int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<Slider> result = sliderRepository.findAllByOrderByCreatedAtDesc(pageable);
        SliderPageResponse resp = new SliderPageResponse();
        resp.setItems(result.getContent().stream().map(this::toResponse).toList());
        resp.setPage(result.getNumber());
        resp.setSize(result.getSize());
        resp.setTotalElements(result.getTotalElements());
        resp.setTotalPages(result.getTotalPages());
        return resp;
    }

    public List<SliderResponse> listActive() {
        return sliderRepository.findAllByActiveTrueOrderByPositionAscCreatedAtAsc()
                .stream().map(this::toResponse).toList();
    }

    public SliderResponse get(UUID id) {
        return sliderRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Slider not found"));
    }

    public SliderResponse create(SliderRequest req) {
        Slider s = new Slider();
        apply(s, req);
        s = sliderRepository.save(s);
        return toResponse(s);
    }

    public SliderResponse update(UUID id, SliderRequest req) {
        Slider s = sliderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Slider not found"));
        apply(s, req);
        s = sliderRepository.save(s);
        return toResponse(s);
    }

    public void delete(UUID id) {
        sliderRepository.deleteById(id);
    }

    private void apply(Slider s, SliderRequest req) {
        if (req.title() != null)
            s.setTitle(req.title());
        if (req.subtitle() != null)
            s.setSubtitle(req.subtitle());
        if (req.description() != null)
            s.setDescription(req.description());
        if (req.imageUrl() != null)
            s.setImageUrl(req.imageUrl());
        if (req.ctaLabel() != null)
            s.setCtaLabel(req.ctaLabel());
        if (req.ctaLink() != null)
            s.setCtaLink(req.ctaLink());
        if (req.position() != null)
            s.setPosition(req.position());
        if (req.active() != null)
            s.setActive(req.active());
    }

    private SliderResponse toResponse(Slider s) {
        return new SliderResponse(
                s.getId(), s.getTitle(), s.getSubtitle(), s.getDescription(), s.getImageUrl(),
                s.getCtaLabel(), s.getCtaLink(), s.getPosition(), s.isActive());
    }
}
