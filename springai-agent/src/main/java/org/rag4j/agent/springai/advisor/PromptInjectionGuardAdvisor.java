package org.rag4j.agent.springai.advisor;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.core.Ordered;

public class PromptInjectionGuardAdvisor implements CallAdvisor {

    public enum Mode {BLOCK, SANITIZE}

    private final Mode mode;
    private final int order;
    private final List<Pattern> riskyPatterns;

    public PromptInjectionGuardAdvisor() {
        this(Mode.BLOCK, Ordered.HIGHEST_PRECEDENCE); // run early
    }

    public PromptInjectionGuardAdvisor(Mode mode, int order) {
        this.mode = mode;
        this.order = order;

        // Heuristics adapted from common jailbreak patterns
        this.riskyPatterns = List.of(
                // Attempts to override system/developer instructions
                Pattern.compile("\\b(ignore|bypass|override)\\b\\s+(all\\s+)?(previous|prior)\\s+(instructions|rules)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("\\b(disregard|forget)\\b\\s+(the\\s+)?(system|developer)\\s+(prompt|instructions)", Pattern.CASE_INSENSITIVE),
                // Attempts to exfiltrate hidden/system content or secrets
                Pattern.compile("\\b(show|reveal|print|dump)\\b.*\\b(system|developer|hidden|internal)\\b.*\\b(prompt|instructions|policy|secrets?)", Pattern.CASE_INSENSITIVE),
                // Role playing as system/developer
                Pattern.compile("\\b(as\\s+the\\s+system|you\\s+are\\s+now\\s+the\\s+system|act\\s+as\\s+developer)\\b", Pattern.CASE_INSENSITIVE),
                // Data-exfil via links or base64
                Pattern.compile("(?i)(follow|read|comply with).*https?://.*(policy|instructions|system|prompt)"),
                Pattern.compile("(?i)base64\\b.{0,40}\\b(decode|decoding)"),
                // Token smuggling / “start output with” instruction takeover
                Pattern.compile("(?i)start\\s+your\\s+output\\s+with\\s+\"?[<\\[{(]"),
                // “do anything now” / DAN-style attacks
                Pattern.compile("(?i)do\\s+anything\\s+now|DAN\\b"),
                // Common prompt injection patterns
                Pattern.compile("(?i).*ignore\\s+(all|previous|above).*instructions.*", Pattern.DOTALL),
                Pattern.compile("(?i).*forget\\s+(everything|all|previous).*", Pattern.DOTALL),
                Pattern.compile("(?i).*you\\s+are\\s+now.*", Pattern.DOTALL),
                Pattern.compile("(?i).*act\\s+as\\s+(if|though).*", Pattern.DOTALL),
                Pattern.compile("(?i).*pretend\\s+(to\\s+be|you\\s+are).*", Pattern.DOTALL),
                Pattern.compile("(?i).*system\\s*:.*", Pattern.DOTALL),
                Pattern.compile("(?i).*\\[\\s*system\\s*\\].*", Pattern.DOTALL),
                Pattern.compile("(?i).*<\\s*system\\s*>.*", Pattern.DOTALL),
                Pattern.compile("(?i).*override\\s+(previous|system).*", Pattern.DOTALL),
                Pattern.compile("(?i).*new\\s+role.*", Pattern.DOTALL)
        );
    }

    @Override
    public String getName() {
        return "PromptInjectionGuardAdvisor";
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        var userText = request.prompt().getUserMessage().getText();
        var hit = patternHit(userText);

        if (hit == null) {
            // No risks detected → continue normally
            return chain.nextCall(request);
        }

//        if (mode == Mode.SANITIZE) {
//            // Remove the matched segment and continue
//            String sanitized = hit.matcher(userText).replaceAll("[redacted by prompt injection guard]");
//            UserMessage.Builder userMessage = request.prompt().getUserMessage().mutate().text(sanitized);
//            List<Message> messages = request.prompt().getInstructions();
//            messages.removeLast();
//            messages.add(userMessage.build());
//            request.prompt().mutate().messages(messages);
//            return chain.nextCall(request);
//        }

        // BLOCK mode: short-circuit with a safe response
        var assistant = new AssistantMessage("""
                {
                    "reasoning": "I can’t comply with instructions that try to override system rules or extract hidden prompts.",
                    "selection": "BLOCKED"
                }
                """);

        var response = new ChatResponse(
                List.of(new Generation(assistant)),
                ChatResponseMetadata.builder().model("guard/PromptInjectionGuardAdvisor").build()
        );

        return ChatClientResponse.builder()
                .chatResponse(response)
                .build();
    }

    private Pattern patternHit(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        for (var p : riskyPatterns) {
            if (p.matcher(text).find()) {
                return p;
            }
        }
        return null;
    }
}
