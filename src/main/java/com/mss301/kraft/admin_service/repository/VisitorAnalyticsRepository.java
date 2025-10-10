package com.mss301.kraft.admin_service.repository;

import com.mss301.kraft.admin_service.entity.VisitorAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface VisitorAnalyticsRepository extends JpaRepository<VisitorAnalytics, UUID> {

    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM VisitorAnalytics v WHERE v.visitDate = :date")
    Long countUniqueVisitorsByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(v) FROM VisitorAnalytics v WHERE v.visitDate = :date")
    Long countTotalVisitsByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM VisitorAnalytics v WHERE v.visitDate BETWEEN :startDate AND :endDate")
    Long countUniqueVisitorsBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(v) FROM VisitorAnalytics v WHERE v.visitDate BETWEEN :startDate AND :endDate")
    Long countTotalVisitsBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT v.visitDate, COUNT(DISTINCT v.ipAddress) as uniqueVisitors, COUNT(v) as totalVisits " +
           "FROM VisitorAnalytics v " +
           "WHERE v.visitDate BETWEEN :startDate AND :endDate " +
           "GROUP BY v.visitDate " +
           "ORDER BY v.visitDate")
    List<Object[]> getVisitorStatsBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT v.deviceType, COUNT(DISTINCT v.ipAddress) FROM VisitorAnalytics v " +
           "WHERE v.visitDate BETWEEN :startDate AND :endDate " +
           "GROUP BY v.deviceType")
    List<Object[]> getDeviceTypeStats(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT v.country, COUNT(DISTINCT v.ipAddress) FROM VisitorAnalytics v " +
           "WHERE v.visitDate BETWEEN :startDate AND :endDate " +
           "AND v.country IS NOT NULL " +
           "GROUP BY v.country " +
           "ORDER BY COUNT(DISTINCT v.ipAddress) DESC")
    List<Object[]> getCountryStats(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    boolean existsByIpAddressAndVisitDate(String ipAddress, LocalDate visitDate);
}
