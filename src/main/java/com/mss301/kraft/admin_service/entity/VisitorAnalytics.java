package com.mss301.kraft.admin_service.entity;

import com.mss301.kraft.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "visitor_analytics", indexes = {
    @Index(name = "idx_visitor_analytics_date", columnList = "visit_date"),
    @Index(name = "idx_visitor_analytics_ip", columnList = "ip_address")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorAnalytics extends BaseEntity {

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "referrer", length = 500)
    private String referrer;

    @Column(name = "page_url", length = 1000)
    private String pageUrl;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "is_unique_visitor")
    @Builder.Default
    private Boolean isUniqueVisitor = true;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "device_type", length = 50)
    private String deviceType; // desktop, mobile, tablet

    @Column(name = "browser", length = 100)
    private String browser;

    @Column(name = "os", length = 100)
    private String os;
}
