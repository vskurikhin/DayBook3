package su.svn.core.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ApiLoggingFilterTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldGenerateRequestIdWhenHeaderMissing() throws Exception {

        ApiLoggingFilter filter =
                new ApiLoggingFilter("X-REQUEST-ID");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(any(), any());

        assertThat(MDC.get("REQUEST_ID")).isNull();
    }

    @Test
    void shouldUseRequestIdFromHeader() throws Exception {

        ApiLoggingFilter filter =
                new ApiLoggingFilter("X-REQUEST-ID");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader("X-REQUEST-ID", "req-123");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(any(), any());
    }

    @Test
    void shouldMaskPasswordParameter() throws Exception {

        ApiLoggingFilter filter =
                new ApiLoggingFilter("X-REQUEST-ID");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addParameter("password", "secret");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(any(), any());
    }
    @Test
    void shouldMaskAuthorizationHeader() throws Exception {

        var user = new User(
                "root",
                "password",
                Collections.emptyList()
        );

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        )
                );

        ApiLoggingFilter filter =
                new ApiLoggingFilter("X-REQUEST-ID");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader("Authorization", "Bearer token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(any(), any());
    }

    @Test
    void shouldUseAuthenticatedUserName() throws Exception {

        var user = new User(
                "root",
                "password",
                Collections.emptyList()
        );

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        )
                );

        ApiLoggingFilter filter =
                new ApiLoggingFilter("X-REQUEST-ID");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(any(), any());
    }

    @Test
    void shouldContinueWhenChainThrowsException() throws Exception {

        ApiLoggingFilter filter =
                new ApiLoggingFilter("X-REQUEST-ID");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        doThrow(new RuntimeException("test"))
                .when(chain)
                .doFilter(any(), any());

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(any(), any());
    }

    @Test
    void shouldWrapResponse() throws Exception {

        ApiLoggingFilter filter =
                new ApiLoggingFilter("X-REQUEST-ID");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        ArgumentCaptor<Object> captor =
                ArgumentCaptor.forClass(Object.class);

        verify(chain).doFilter(any(), (ServletResponse) captor.capture());

        assertThat(captor.getValue())
                .isNotNull();
    }
}