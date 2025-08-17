package org.rag4j.agent.embabel

import com.embabel.agent.api.common.support.SelfToolGroup
import com.embabel.agent.common.Constants
import com.embabel.agent.core.ToolGroupDescription
import com.embabel.agent.core.ToolGroupPermission
import com.embabel.common.core.types.Semver
import org.springframework.ai.support.ToolCallbacks

/**
 * Kotlin wrapper to satisfy SelfToolGroup's value-class returns.
 * Delegates to the existing Java class for real work and tool discovery.
 */
class EmbabelConferenceToolsAdapter(
    private val delegate: EmbabelConferenceTools
) : SelfToolGroup {

    override val description: ToolGroupDescription
        get() = ToolGroupDescription(
            description = "Conference talks tools: use when you need to have information about conference talks.",
            role = "talks")

    override val provider: String
        get() = Constants.EMBABEL_PROVIDER

    override val version: Semver
        get() = Semver(0,1,0)

    override val permissions: Set<ToolGroupPermission>
        get() = emptySet()

    // Publish the Java class's @Tool methods
    override val toolCallbacks = ToolCallbacks.from(delegate).toList()

    // name + metadata default implementations come from the interface
    // (getName() default == javaClass.name; metadata is computed)
}