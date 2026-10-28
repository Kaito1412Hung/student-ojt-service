# CODE AUDIT CHECKLIST — DEFINITION OF DONE (DoD)

| STT | Hạng mục Quality Gate | Tiêu chuẩn kỹ thuật bắt buộc | Bằng chứng trong code | Trạng thái |
|---|---|---|---|---|
| 1 | Red-Zone Security | Có file `.copilotignore` / `.cursorignore` tại root project; 0% rò rỉ Secrets/YML. | `.copilotignore`, `.cursorignore` | [x] PASSED |
| 2 | Clean Architecture | Controller chỉ nhận/trả DTO Records; 0% JPA `@Entity` rò rỉ ra lớp Presentation. | `StudentController` chỉ dùng `StudentRequestDTO` / `StudentResponseDTO` | [x] PASSED |
| 3 | PII Masking | CCCD (12 số) và SĐT (10 số) được che mờ tự động tại ResponseDTO. | `StudentResponseDTO.fromEntity()` → `090*****67`, `079******789` | [x] PASSED |
| 4 | Input Validation | Đầy đủ Jakarta Validation (`@NotBlank`, `@Email`, `@Pattern`) trên RequestDTO. | `StudentRequestDTO` | [x] PASSED |
| 5 | Duplicate Business Logic | Bắt lỗi trùng Email/CCCD, ném Exception trả về 409 Conflict. | `StudentServiceImpl.register()` + `StudentAlreadyExistsException` | [x] PASSED |
| 6 | Exception Handling | `@RestControllerAdvice` bắt lỗi Validation, ẩn 100% SQL Stacktrace. | `GlobalExceptionHandler` + `server.error.include-stacktrace: never` | [x] PASSED |
| 7 | Prompt Evidence | Có file `Dev_Prompt_Library.md` chứa Prompt 5 thành tố. | `Dev_Prompt_Library.md` | [x] PASSED |
