package com.r2s.masterclass.student.controller;

import com.r2s.masterclass.student.dto.StudentRequestDTO;
import com.r2s.masterclass.student.dto.StudentResponseDTO;
import com.r2s.masterclass.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/** REST API đăng ký sinh viên OJT. Chỉ làm việc với DTO, không chứa logic nghiệp vụ. */
@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /** POST /api/v1/students/register -> 201 Created + header Location trỏ tới bản ghi mới. */
    @PostMapping("/register")
    public ResponseEntity<StudentResponseDTO> register(@Valid @RequestBody StudentRequestDTO request) {
        StudentResponseDTO created = studentService.register(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/students/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /** GET /api/v1/students/{id} -> 200 OK, hoặc 404 nếu không tồn tại. */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    /** GET /api/v1/students -> 200 OK. */
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAll() {
        return ResponseEntity.ok(studentService.getAll());
    }
}
