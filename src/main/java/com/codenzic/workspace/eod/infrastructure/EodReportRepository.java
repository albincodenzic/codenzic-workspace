package com.codenzic.workspace.eod.infrastructure;
import com.codenzic.workspace.eod.domain.EodReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;
public interface EodReportRepository extends JpaRepository<EodReport,UUID> {
 Optional<EodReport> findByIdAndOrganizationId(UUID id,UUID org);
 List<EodReport> findAllByOrganizationIdAndEmployeeIdOrderByReportDateDesc(UUID org,UUID employee);
 List<EodReport> findAllByOrganizationIdAndReportDateBetweenOrderByReportDateDesc(UUID org,LocalDate from,LocalDate to);
 boolean existsByEmployeeIdAndReportDate(UUID employeeId,LocalDate date);

 @Query(
         value = """
                 select report from EodReport report
                 where report.organizationId = :organizationId
                   and (:employeeId is null or report.employeeId = :employeeId)
                   and (:from is null or report.reportDate >= :from)
                   and (:to is null or report.reportDate <= :to)
                   and (:status is null or report.status = :status)
                   and (:teamScoped = false or exists (
                        select scopedMember.id from TeamMember scopedMember
                        where scopedMember.employeeId = report.employeeId and scopedMember.teamId in :teamIds
                          and scopedMember.organizationId = :organizationId))
                   and (:teamId is null or exists (
                        select member.id from TeamMember member
                        where member.employeeId = report.employeeId and member.teamId = :teamId
                          and member.organizationId = :organizationId))
                 """,
         countQuery = """
                 select count(report) from EodReport report
                 where report.organizationId = :organizationId
                   and (:employeeId is null or report.employeeId = :employeeId)
                   and (:from is null or report.reportDate >= :from)
                   and (:to is null or report.reportDate <= :to)
                   and (:status is null or report.status = :status)
                   and (:teamScoped = false or exists (
                        select scopedMember.id from TeamMember scopedMember
                        where scopedMember.employeeId = report.employeeId and scopedMember.teamId in :teamIds
                          and scopedMember.organizationId = :organizationId))
                   and (:teamId is null or exists (
                        select member.id from TeamMember member
                        where member.employeeId = report.employeeId and member.teamId = :teamId
                          and member.organizationId = :organizationId))
                 """
 )
 Page<EodReport> search(
         @Param("organizationId") UUID organizationId,
         @Param("employeeId") UUID employeeId,
         @Param("teamId") UUID teamId,
         @Param("from") LocalDate from,
         @Param("to") LocalDate to,
         @Param("status") String status,
         @Param("teamScoped") boolean teamScoped,
         @Param("teamIds") List<UUID> teamIds,
         Pageable pageable
 );
}
