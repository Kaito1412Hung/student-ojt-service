package com.r2s.masterclass.student.service.impl;

import com.r2s.masterclass.student.dto.StudentRequestDTO;
import com.r2s.masterclass.student.dto.StudentResponseDTO;
import com.r2s.masterclass.student.entity.StudentEntity;
import com.r2s.masterclass.student.exception.StudentAlreadyExistsException;
import com.r2s.masterclass.student.exception.StudentNotFoundException;
import com.r2s.masterclass.student.repository.StudentRepository;
import com.r2s.masterclass.student.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    // Constructor injection: dễ test, không cần @Autowired
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public StudentResponseDTO register(StudentRequestDTO request) {
        // Chuẩn hoá email để "A@x.com" và "a@x.com" bị coi là trùng
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String nationalId = request.nationalId().trim();

        if (studentRepository.existsByEmail(email) || studentRepository.existsByNationalId(nationalId)) {
            throw new StudentAlreadyExistsException();
        }

        StudentEntity entity = new StudentEntity(
                request.fullName().trim(),
                email,
                request.phone().trim(),
                nationalId,
                trimOrNull(request.university()),
                trimOrNull(request.major())
        );
        // saveAndFlush để lỗi unique constraint (nếu có race condition) nổ ngay trong transaction này
        return StudentResponseDTO.fromEntity(studentRepository.saveAndFlush(entity));
    }

    @Override
    public StudentResponseDTO getById(UUID id) {
        return studentRepository.findById(id)
                .map(StudentResponseDTO::fromEntity)
                .orElseThrow(() -> new StudentNotFoundException());
    }

    @Override
    public List<StudentResponseDTO> getAll() {
        return studentRepository.findAll().stream()
                .map(StudentResponseDTO::fromEntity)
                .toList();
    }

    private static String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
}
