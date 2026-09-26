package com.codenzic.workspace.attendance.domain;
import jakarta.persistence.*; import lombok.*; import java.time.*; import java.util.UUID;
@Entity
@Table(name="attendance_records", uniqueConstraints=@UniqueConstraint(columnNames={"employee_id","attendance_date"}))
@Getter
@NoArgsConstructor
public class Attendance {
 @Id
 @GeneratedValue(strategy=GenerationType.UUID)
 UUID id;
 @Column(name="organization_id",nullable=false)
 UUID organizationId;
 @Column(name="employee_id",nullable=false)
 UUID employeeId;
 @Column(name="attendance_date",nullable=false)
 LocalDate attendanceDate;
 @Column(nullable=false,length=20)
 String status;
 Instant checkIn; Instant checkOut;
 @Column(length=500) String notes;
 public Attendance(UUID org,UUID employee,LocalDate date,String status,Instant in,Instant out,String notes){organizationId=org;employeeId=employee;attendanceDate=date;this.status=status;checkIn=in;checkOut=out;this.notes=notes;}
 public void checkIn(Instant time) { if(checkIn!=null) throw new IllegalStateException("Already checked in"); checkIn=time; if("ABSENT".equals(status)) status="PRESENT"; }
 public void checkOut(Instant time) { if(checkIn==null) throw new IllegalStateException("Check in before checking out"); if(checkOut!=null) throw new IllegalStateException("Already checked out"); if(time.isBefore(checkIn)) throw new IllegalStateException("Check-out must not precede check-in"); checkOut=time; }
}
