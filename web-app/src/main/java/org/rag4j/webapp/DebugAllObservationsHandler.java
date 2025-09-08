package org.rag4j.webapp;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DebugAllObservationsHandler implements ObservationHandler<Observation.Context> {

    private static final Logger logger = LoggerFactory.getLogger(DebugAllObservationsHandler.class);

    @Override
    public void onStart(Observation.Context context) {
        String name = context.getName();
        String contextClassName = context.getClass().getName();
        
        // Highlight ToolCallingObservationContext if found
        if (contextClassName.contains("ToolCalling")) {
            logger.info("🚨🔧 FOUND TOOL CALLING CONTEXT! === {} ===", name != null ? name : "[NULL NAME]");
            logger.info("🚨🔧 Context class: {}", contextClassName);
        } else {
            logger.info("=== ALL OBSERVATIONS STARTED: {} ===", name != null ? name : "[NULL NAME]");
            logger.info("Context class: {}", contextClassName);
        }
        
        // Log key-value pairs
        context.getLowCardinalityKeyValues().forEach(kv -> 
            logger.info("  LowCard: {} = {}", kv.getKey(), kv.getValue())
        );
        
        context.getHighCardinalityKeyValues().forEach(kv -> {
            String value = kv.getValue();
            if (value != null && value.length() > 200) {
                value = value.substring(0, 200) + "... [TRUNCATED]";
            }
            logger.info("  HighCard: {} = {}", kv.getKey(), value);
        });
        
        if (contextClassName.contains("ToolCalling")) {
            logger.info("🚨🔧 === END TOOL CALLING CONTEXT ===");
        } else {
            logger.info("=== END ALL OBSERVATIONS STARTED ===");
        }
    }

    @Override
    public void onError(Observation.Context context) {
        logger.info("=== ALL OBSERVATIONS ERROR: {} ===", context.getName());
    }

    @Override
    public void onEvent(Observation.Event event, Observation.Context context) {
        logger.info("=== ALL OBSERVATIONS EVENT: {} for {} ===", event.getName(), context.getName());
    }

    @Override
    public void onScopeOpened(Observation.Context context) {
        // Suppress for now to reduce noise
    }

    @Override
    public void onScopeClosed(Observation.Context context) {
        // Suppress for now to reduce noise
    }

    @Override
    public void onStop(Observation.Context context) {
        logger.info("=== ALL OBSERVATIONS STOPPED: {} ===", context.getName());
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        // Support all contexts for debugging
        return true;
    }
}
