package com.codenzic.workspace.team.domain;
import jakarta.persistence.*; import lombok.*; import java.util.*; import java.time.LocalDate;
@Entity @Table(name="teams") @Getter @NoArgsConstructor
public class Team { @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; UUID organizationId; UUID departmentId; @Column(nullable=false,length=150) String name; @Column(length=500) String description;
 public Team(UUID organizationId,UUID departmentId,String name,String description) { this.organizationId=organizationId;this.departmentId=departmentId;this.name=name;this.description=description; }
 public void update(UUID departmentId,String name,String description) { this.departmentId=departmentId;this.name=name;this.description=description; }
}