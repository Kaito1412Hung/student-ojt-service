package com.r2s.masterclass.student.controller;

import com.r2s.masterclass.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    private static final String URL = "/api/v1/students";
    private static final String REGISTER_URL = URL + "/register";
    private static final String DUPLICATE_MESSAGE = "Số CCCD hoặc Email đã tồn tại trên hệ thống OJT";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void cleanDb() {
        studentRepository.deleteAll();
    }

    private static String body(String email, String phone, String nationalId) {
        return """
                {"fullName":"Nguyễn Văn A","email":"%s","phone":"%s","nationalId":"%s",
                 "university":"Đại học Bách Khoa","major":"Công nghệ Phần mềm"}
                """.formatted(email, phone, nationalId);
    }

    @Test
    void register_returns201_withMaskedPii() throws Exception {
        mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(body("a@r2s.com", "0901234567", "079123456789")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(URL + "/")))
                .andExpect(jsonPath("$.maskedPhone").value("090*****67"))
                .andExpect(jsonPath("$.maskedNationalId").value("079******789"))
                .andExpect(jsonPath("$.university").value("Đại học Bách Khoa"))
                .andExpect(jsonPath("$.major").value("Công nghệ Phần mềm"))
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$.phone").doesNotExist())
                .andExpect(jsonPath("$.nationalId").doesNotExist());
    }

    @Test
    void register_invalidInput_returns400_withFieldErrors() throws Exception {
        mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"","email":"sai-email","phone":"12345","nationalId":"abc"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Dữ liệu đầu vào không hợp lệ"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors.fullName").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.phone").value("Số điện thoại phải đủ 10 chữ số hợp lệ tại Việt Nam"))
                .andExpect(jsonPath("$.errors.nationalId").value("Số CCCD phải đúng 12 chữ số"));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(body("dup@r2s.com", "0901234567", "079123456789")))
                .andExpect(status().isCreated());

        // Email khác hoa/thường vẫn bị coi là trùng
        mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(body("DUP@r2s.com", "0912345678", "079000000001")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(DUPLICATE_MESSAGE));
    }

    @Test
    void register_duplicateNationalId_returns409() throws Exception {
        mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(body("x@r2s.com", "0901234567", "079123456789")))
                .andExpect(status().isCreated());

        mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(body("y@r2s.com", "0912345678", "079123456789")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(DUPLICATE_MESSAGE));
    }

    @Test
    void getById_returns200_withMaskedPii() throws Exception {
        String created = mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(body("get@r2s.com", "0901234567", "079123456789")))
                .andReturn().getResponse().getContentAsString();
        String id = com.jayway.jsonpath.JsonPath.read(created, "$.id");

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.maskedPhone").value("090*****67"));
    }

    @Test
    void getById_unknown_returns404() throws Exception {
        mockMvc.perform(get(URL + "/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Không tìm thấy hồ sơ sinh viên với ID cung cấp"));
    }

    @Test
    void getAll_returns200() throws Exception {
        mockMvc.perform(get(URL)).andExpect(status().isOk());
    }
}
