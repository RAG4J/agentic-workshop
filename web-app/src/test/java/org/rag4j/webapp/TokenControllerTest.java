package org.rag4j.webapp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TokenControllerTest {
    @Test
    @DisplayName("homePage adds error when token is missing")
    void homePageAddsErrorWhenTokenIsMissing() {
        TokenController controller = new TokenController("http://proxy", Optional.empty());
        Model model = Mockito.mock(Model.class);
        String view = controller.homePage(model);
        Mockito.verify(model).addAttribute(Mockito.eq("error"), Mockito.anyString());
        assertThat(view).isEqualTo("token");
    }

    @Test
    @DisplayName("homePage does not add error when token is present")
    void homePageDoesNotAddErrorWhenTokenIsPresent() {
        TokenController controller = new TokenController("http://proxy", Optional.of("sometoken"));
        Model model = Mockito.mock(Model.class);
        String view = controller.homePage(model);
        Mockito.verify(model, Mockito.never()).addAttribute(Mockito.eq("error"), Mockito.any());
        assertThat(view).isEqualTo("token");
    }

    @Test
    @DisplayName("handleFetchToken returns error when userId is null")
    void handleFetchTokenReturnsErrorWhenUserIdIsNull() {
        TokenController controller = new TokenController("http://proxy", Optional.of("sometoken"));
        Model model = Mockito.mock(Model.class);
        String view = controller.handleFetchToken(null, model);
        Mockito.verify(model).addAttribute("confirmation", null);
        Mockito.verify(model).addAttribute("error", "You need to provide a username.");
        assertThat(view).isEqualTo("token");
    }

    @Test
    @DisplayName("handleFetchToken returns error when userId is empty")
    void handleFetchTokenReturnsErrorWhenUserIdIsEmpty() {
        TokenController controller = new TokenController("http://proxy", Optional.of("sometoken"));
        Model model = Mockito.mock(Model.class);
        String view = controller.handleFetchToken("   ", model);
        Mockito.verify(model).addAttribute("confirmation", null);
        Mockito.verify(model).addAttribute("error", "You need to provide a username.");
        assertThat(view).isEqualTo("token");
    }
}

