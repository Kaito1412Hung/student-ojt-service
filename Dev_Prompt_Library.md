# DEV PROMPT LIBRARY — BỘ SỔ TAY PROMPT 5 THÀNH TỐ CHUẨN DOANH NGHIỆP

> **Dự án:** Student OJT Management Service
> **Lập trình viên:** [Họ và Tên Học Viên]
> **Lớp:** 6H Masterclass — AI Agent & Code Audit (R2S Academy)

----

## 1. CẤU HÌNH LÁ CHẮN VÙNG ĐỎ (.copilotignore)

```gitignore
src/main/resources/application*.yml
src/main/resources/application*.properties
.env
*.pem
*.key
secrets/
```

----

## 2. PROMPT 5 THÀNH TỐ (R-C-T-C-O) TẠO REST API & PII MASKING

```text
[ROLE]
Bạn là Senior Java Architect với 10+ năm kinh nghiệm thiết kế REST API Spring Boot Enterprise
và tuân thủ nghiêm ngặt Clean Architecture.

[CONTEXT]
Tôi đang viết API Đăng ký Sinh viên OJT trên Spring Boot 3.2, Java 21, Spring Data JPA,
H2 Database và Jakarta Validation. Package chính là com.r2s.masterclass.student.

[TASK]
Khởi tạo toàn bộ bộ mã nguồn Java cho phân hệ Student OJT Service gồm 7 file:
1. StudentRequestDTO (Java Record + Jakarta Validation Annotations)
2. StudentResponseDTO (Java Record + Logic Masking PII)
3. StudentEntity (JPA Entity với ID UUID, status mặc định PENDING_APPROVAL)
4. StudentRepository (Spring Data JPA với phương thức existsByEmail và existsByNationalId)
5. StudentService & StudentServiceImpl (Có logic kiểm tra trùng Email/CCCD)
6. StudentController (REST API: POST /api/v1/students/register -> 201 Created,
   GET /api/v1/students/{id} -> 200 OK)
7. GlobalExceptionHandler (@RestControllerAdvice bắt lỗi Validation 400, Duplicate 409, Not Found 404)

[CONSTRAINT]
- CẤM TUYỆT ĐỐI trả StudentEntity ra Controller. Controller chỉ giao tiếp qua DTO Records.
- BẮT BUỘC Validate RequestDTO: fullName (@NotBlank), email (@Email),
  phone (@Pattern 10 số VN), nationalId (@Pattern 12 số).
- BẮT BUỘC Masking PII trong ResponseDTO: Phone (090*****67), NationalId (079******789).
- BẮT BUỘC kiểm tra trùng Email/CCCD trong Service, ném StudentAlreadyExistsException trả về 409 Conflict.
- Ẩn toàn bộ thông tin SQL Stacktrace khi bị lỗi.
- JSON lỗi theo chuẩn { "timestamp", "status", "message", "errors" }.

[OUTPUT]
Mã nguồn Java sạch sẽ, đầy đủ annotation, có comment giải thích ngắn gọn bằng tiếng Việt,
phân chia rõ tên file và đường dẫn package com.r2s.masterclass.student.
```

----

## 3. NHẬN XÉT AUDIT CỦA LẬP TRÌNH VIÊN (HUMAN-IN-THE-LOOP)

- **Lỗi AI sinh ra ban đầu:**
  - Endpoint đăng ký sinh ra là `POST /api/v1/students`, sai API Contract (`/register`) → lệnh cURL của đề trả về 500.
  - ResponseDTO dùng tên field `phone` / `nationalId` thay vì `maskedPhone` / `maskedNationalId`, thiếu `university`, `major`.
  - JSON lỗi dùng định dạng `ProblemDetail` (`detail`) thay vì `{ status, message, errors }` như đặc tả.
  - Regex SĐT trong đề `^(0[3|5|7|8|9])+\d{8}$` có lỗi: ký tự `|` trong `[...]` bị hiểu là ký tự thường, và `+` cho phép lặp tiền tố. Đã thay bằng `^0[35789]\d{8}$`.
- **Thao tác Refactor của tôi:**
  - Đổi mapping sang `@PostMapping("/register")`, header `Location` trỏ về `/api/v1/students/{id}`.
  - Bổ sung `university`, `major` vào Request/Response/Entity; đổi tên field masking theo hợp đồng API.
  - Viết record `ErrorResponse` và refactor `GlobalExceptionHandler` trả đúng định dạng lỗi; thêm 405/415 để request sai không rơi vào 500.
  - Custom Exception `StudentAlreadyExistsException` ánh xạ `@ResponseStatus(HttpStatus.CONFLICT)`.
  - Unique constraint ở DB + `saveAndFlush` để chặn trùng khi 2 request chạy song song (vẫn trả 409, không lộ SQL).
- **Kết quả Audit:** Code đạt 100% tiêu chí DoD, chạy test cURL thành công trên Terminal (201 / 400 / 409 / 200 / 404).
