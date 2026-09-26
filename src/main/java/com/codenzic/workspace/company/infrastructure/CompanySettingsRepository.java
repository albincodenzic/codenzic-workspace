package com.codenzic.workspace.company.infrastructure;
import com.codenzic.workspace.company.domain.CompanySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface CompanySettingsRepository extends JpaRepository<CompanySettings, UUID> {}
