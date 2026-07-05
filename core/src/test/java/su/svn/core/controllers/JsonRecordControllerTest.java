package su.svn.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfiguration;
import su.svn.core.domain.entities.UserName;
import su.svn.core.models.dto.NewJsonRecord;
import su.svn.core.models.dto.ResourceJsonRecord;
import su.svn.core.services.domain.JsonRecordService;
import su.svn.core.services.domain.UserNameService;
import su.svn.core.services.security.JwtService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@ExtendWith(SpringExtension.class)
@Import({JsonRecordControllerTest.TestConfig.class, JsonRecordControllerTest.TestSecurityConfig.class})
@WebMvcTest(JsonRecordController.class) // Focuses on web layer (controllers)
@WithMockUser(username = JsonRecordControllerTest.ROOT, authorities = JsonRecordControllerTest.USER)
class JsonRecordControllerTest {

    static final String ROOT = "root";
    static final String USER = "USER";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JsonRecordService jsonRecordService;

    @Autowired
    JwtService jwtService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserNameService userNameService;

    @BeforeEach
    void beforeEach() throws ChangeSetPersister.NotFoundException {
        when(jwtService.extractUserName(any())).thenReturn(ROOT);
        when(jwtService.extractGroups(any())).thenReturn(Set.of(USER));
        when(jwtService.isTokenValid(any(), any())).thenReturn(true);
        when(userNameService.findByUserName(any())).thenReturn(UserName.builder().userName(ROOT).build());
    }

    // -----------------------------
    // GET by id
    // -----------------------------
    @Test
    void shouldReturnRecordById() throws Exception {
        UserDetailsServiceAutoConfiguration u;
        UUID id = UUID.randomUUID();

        ResourceJsonRecord response = ResourceJsonRecord.builder()
                .id(id)
                .title("test")
                .json(Map.of("key", "value"))
                .postAt(OffsetDateTime.now())
                .visible(true)
                .flags(1)
                .build();

        when(jsonRecordService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/core/api/v2/json-record/{id}", id)
                        .header("Authorization", "Bearer fake")) // Include fake token
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("test"));

        verify(jsonRecordService).findById(id);
    }

    // -----------------------------
    // POST create
    // -----------------------------
    @Test
    void shouldCreateRecord() throws Exception {

        NewJsonRecord request = NewJsonRecord.builder()
                .title("new title")
                .json(Map.of("a", "b"))
                .postAt(OffsetDateTime.now())
                .visible(true)
                .flags(0)
                .build();

        ResourceJsonRecord response = ResourceJsonRecord.builder()
                .id(UUID.randomUUID())
                .title("new title")
                .json(Map.of("a", "b"))
                .visible(true)
                .flags(0)
                .build();

        when(jsonRecordService.save(any())).thenReturn(response);

        mockMvc.perform(post("/core/api/v2/json-record")
                        .header("Authorization", "Bearer fake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("new title"));

        verify(jsonRecordService).save(any());
    }

    // -----------------------------
    // PUT update
    // -----------------------------
    @Test
    void shouldUpdateRecord() throws Exception {

        UUID id = UUID.randomUUID();

        var request = Map.of(
                "id", id.toString(),
                "title", "updated"
        );

        ResourceJsonRecord response = ResourceJsonRecord.builder()
                .id(id)
                .title("updated")
                .build();

        when(jsonRecordService.update(any())).thenReturn(response);

        mockMvc.perform(put("/core/api/v2/json-record")
                        .header("Authorization", "Bearer fake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("updated"));

        verify(jsonRecordService).update(any());
    }

    // -----------------------------
    // DELETE (disable)
    // -----------------------------
    @Test
    void shouldDisableRecord() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing().when(jsonRecordService).disable(id);

        mockMvc.perform(delete("/core/api/v2/json-record/{id}", id)
                        .header("Authorization", "Bearer fake")
                )
                .andExpect(status().isNoContent());

        verify(jsonRecordService).disable(id);
    }

    @TestConfiguration

    static class TestConfig {
        @Bean
        public JsonRecordService jsonRecordService() {
            return Mockito.mock(JsonRecordService.class);
        }

        @Bean
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }

        @Bean
        public UserNameService userNameService() {
            return Mockito.mock(UserNameService.class);
        }
    }

    @EnableWebSecurity
    @EnableMethodSecurity
    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http.csrf(AbstractHttpConfigurer::disable)
                    // Своего рода отключение CORS (разрешение запросов со всех доменов)
                    .cors(cors -> cors.configurationSource(request -> {
                        var corsConfiguration = new CorsConfiguration();
                        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        corsConfiguration.setAllowedHeaders(List.of("*"));
                        corsConfiguration.setAllowCredentials(true);
                        return corsConfiguration;
                    }))
                    // Настройка доступа к конечным точкам
                    .authorizeHttpRequests(request -> request
                            .requestMatchers("/core/**")
                            .authenticated())
                    .build();
        }
    }
}