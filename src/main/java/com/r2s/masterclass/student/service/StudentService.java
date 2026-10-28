package com.r2s.masterclass.student.service;

import com.r2s.masterclass.student.dto.StudentRequestDTO;
import com.r2s.masterclass.student.dto.StudentResponseDTO;

import java.util.List;
import java.util.UUID;

/** Hợp đồng nghiệp vụ: chỉ nhận/trả DTO, không lộ Entity ra ngoài. */
public interface StudentService {

    StudentResponseDTO register(StudentRequestDTO request);

    StudentResponseDTO getById(UUID id);

    List<StudentResponseDTO> getAll();
}
