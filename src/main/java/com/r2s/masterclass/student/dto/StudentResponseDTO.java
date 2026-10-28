package com.r2s.masterclass.student.dto;

import com.r2s.masterclass.student.entity.StudentEntity;
import com.r2s.masterclass.student.entity.StudentStatus;

import java.util.UUID;

/**
 * Dữ liệu trả về cho client. SĐT và CCCD luôn được che (masking PII),
 * dữ liệu gốc không bao giờ rời khỏi tầng Service.
 */
public record StudentResponseDTO(
        UUID id,
        String fullName,
        String email,
        String maskedPhone,
        String maskedNationalId,
        String university,
        String major,
        StudentStatus status
) {

    /** Chuyển Entity -> DTO và che thông tin nhạy cảm ngay tại đây. */
    public static StudentResponseDTO fromEntity(StudentEntity entity) {
        return new StudentResponseDTO(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                maskPhone(entity.getPhone()),
                maskNationalId(entity.getNationalId()),
                entity.getUniversity(),
                entity.getMajor(),
                entity.getStatus()
        );
    }

    // 0901234567 -> 090*****67
    static String maskPhone(String phone) {
        return mask(phone, 3, 2);
    }

    // 079123456789 -> 079******789
    static String maskNationalId(String nationalId) {
        return mask(nationalId, 3, 3);
    }

    /** Giữ lại `head` ký tự đầu và `tail` ký tự cuối, phần giữa thay bằng '*'. */
    private static String mask(String value, int head, int tail) {
        if (value == null || value.length() <= head + tail) {
            return value;
        }
        return value.substring(0, head)
                + "*".repeat(value.length() - head - tail)
                + value.substring(value.length() - tail);
    }
}
