package com.r2s.masterclass.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dữ liệu client gửi lên khi đăng ký sinh viên OJT.
 * Mọi ràng buộc được kiểm tra tự động nhờ @Valid ở Controller.
 */
public record StudentRequestDTO(

        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
        String fullName,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        // SĐT di động VN: 10 số, bắt đầu bằng 03/05/07/08/09
        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^0[35789]\\d{8}$", message = "Số điện thoại phải đủ 10 chữ số hợp lệ tại Việt Nam")
        String phone,

        // CCCD gắn chip: đúng 12 chữ số
        @NotBlank(message = "Số CCCD không được để trống")
        @Pattern(regexp = "^\\d{12}$", message = "Số CCCD phải đúng 12 chữ số")
        String nationalId,

        @Size(max = 150, message = "Tên trường tối đa 150 ký tự")
        String university,

        @Size(max = 100, message = "Chuyên ngành tối đa 100 ký tự")
        String major
) {
}
