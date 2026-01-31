package com.aiboomi.edupath.controllers;

import com.aiboomi.edupath.entities.Profile;
import com.aiboomi.edupath.entities.Student;
import com.aiboomi.edupath.services.ProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileControllerTest {

    MockMvc mockMvc;

    ProfileService profileService = org.mockito.Mockito.mock(ProfileService.class);

    public ProfileControllerTest() {
        ProfileController controller = new ProfileController(profileService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void generateProfile_endpoint_returnsOk() throws Exception {
        Profile p = new Profile();
        p.setId(1L);
        p.setProfileJson("{}");
        p.setNarrative("narrative");
        p.setCreatedAt(OffsetDateTime.now());
        Student s = new Student(); s.setId(1L);
        p.setStudent(s);
        Mockito.doReturn(p).when(profileService).generateProfileForStudent(1L);
        com.aiboomi.edupath.dtos.ProfileDTO dto = new com.aiboomi.edupath.dtos.ProfileDTO(1L, 1L, java.util.Map.of(), "narrative", p.getCreatedAt());
        Mockito.doReturn(dto).when(profileService).toDto(p);

        mockMvc.perform(post("/api/students/1/profiles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.narrative").value("narrative"));
    }

    @Test
    void getLatest_returnsNotFoundWhenAbsent() throws Exception {
        Mockito.doReturn(Optional.empty()).when(profileService).getLatestProfileForStudent(1L);
        mockMvc.perform(get("/api/students/1/profiles/latest")).andExpect(status().isNotFound());

        // when present
        Profile p = new Profile(); p.setId(2L); p.setProfileJson("{}"); p.setNarrative("narr"); p.setCreatedAt(java.time.OffsetDateTime.now()); Student s = new Student(); s.setId(1L); p.setStudent(s);
        Mockito.doReturn(Optional.of(p)).when(profileService).getLatestProfileForStudent(1L);
        com.aiboomi.edupath.dtos.ProfileDTO dto2 = new com.aiboomi.edupath.dtos.ProfileDTO(2L, 1L, java.util.Map.of(), "narr", p.getCreatedAt());
        Mockito.doReturn(dto2).when(profileService).toDto(p);
        mockMvc.perform(get("/api/students/1/profiles/latest")).andExpect(status().isOk()).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.narrative").value("narr"));
    }

    @Test
    void generateAll_returnsOk() throws Exception {
        Mockito.doReturn(List.of()).when(profileService).generateProfilesForAll();
        mockMvc.perform(post("/api/students/generate-profiles")).andExpect(status().isOk());
    }
}
