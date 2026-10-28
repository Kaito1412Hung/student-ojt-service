package com.r2s.masterclass.student.repository;

import com.r2s.masterclass.student.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, UUID> {

    // Spring Data tự sinh câu SELECT COUNT/EXISTS theo tên phương thức
    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);
}
