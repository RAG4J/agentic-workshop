# What are we going to cover in this workshop?
- What are Agents?
- Implement basic agent concepts in plain java 
  - prompting, 
  - reasoning, 
  - tool use, 
  - memory, 
  - multi-agent, 
  - observability, 
  - Guardrails
- Implementing Agents with Spring AI
- Testing and Evaluating Agents
- Implementing Agents with Embabel

# What is an Agent?
Merriam-Webster: “One that acts or exerts power”
https://www.merriam-webster.com/dictionary/agent?utm_source=chatgpt.com

Wikipedia: Perceives environment, acts autonomously to achieve goals, and can improve via learning. (Wikipedia)



## Recap using an LLM -> Prompting, Context, Reasoning
### Introduce the sample application with the most basic agent (no tools, no reasoning, no memory) and the gui
- Demonstrate working with OpenAI and check the proxy and obtaining the token. 
- Let them ask questions about Science Fiction.
- Write a prompt to get the LLM to answer questions about Science Fiction only.
- Explain the importance of the system message, user message, and assistant message.
  - Interesting that System prompts are also becoming Developer prompts.
## Explain ReAct
- Reasoning + Acting, by now a lot of models have it baked in, just like JSON output and tool integration.
- You can do it by yourself as well, as you will do in the workshop.
## Explain Tool Use
- An agent becomes valuable when it can use tools to perform tasks and retrieve information
- Introduce the conference tools agent
## Explain Memory
- Memory is what makes an agent capable of multi-turn conversations and complex tasks
- Different types of memory: short-term, long-term, external (e.g., databases)
## Explain the difference between RAG and tools
## What is more useful than an agent? Having more agents.
- Discuss the different patterns for agents tot work together
- Talk about the memory, being shared between agents or not.
## Recap difference between Agents and LLMs
- Agents are LLMs with tools and memory

## Guardrails

## Observability

## Evaluation

# Implementing Agents with Spring AI

# Embabel
## Why?
## Building blocks
- Actions: The steps an agent takes
- Goals: What the agent is trying to achieve
- Conditions: Assessed before an action is taken and to check if a goal is achieved
- Domain model: Objects underpinning the flow and informing Actions, Goals and Conditions.
- Plan: THe sequence of actions to achieve a goal. The plan is created by Embabel based on the goals, actions and conditions defined. It is recreated after each action.


# Assignments

00:00 - 00:15: Introduction + What are agents?
00:15 - 00:50: Setup sample application on participants machines
- Assignment 1: Basic Agent with LLM (SciFi case)
  - Assure all non SciFI related questions are answered with "I don't know"
- Assignment 2: Add tools + Memory (Conference case)
  - Add a tool to the agent to answer questions about the conference
- Assignment 3: Multi-agent (Conference case + SciFi case)
  - Add a router Agent to route between the SciFi agent and the conference agent
- Optional: Add observability to the application
00:50 - 01:00: Discussie
01:00 - 01:15: Muti-agent discussion + Spring AI Introduction + McP server
01:15 - 01:50: Setup Spring AI sample application on participants machines
- Assignment 4: Basic Agent with LLM (SciFi case)
  - Assure all non SciFI related questions are answered with "I don't know"
- Assignment 5: Add tools + Memory (Conference case)
  - Add a tool to the agent to answer questions about the conference
- Assignment 6: Multi-agent (Conference case + SciFi case)
  - Add a router Agent to route between the SciFi agent and the conference agent
- Assignment 7: Add McP server for time questions (Time Agent)
  - Add a new Agent using McP server to answer time related questions, add to routing agent
- Optional: Add observability to the application
01:50 - 02:00: Discussie
02:00 - 02:15: Evaluations + GuardRails
02:15 - 02:50: Setup Evaluations + GuardRails sample application on participants machines
- Assignment 8: Add GuardRails to the application
- Assignment 9: Add evaluation to the application
02:50 - 03:00: Discussion + Wrap-up

Extra: Embabel
- Assignment 1: Basic Agent with LLM (SciFi case)
    - Assure all non SciFI related questions are answered with "I don't know"
- Assignment 2: Add tools + Memory (Conference case)
    - Add a tool to the agent to answer questions about the conference
- Assignment 3: Multi-agent (Conference case + SciFi case)
    - Add a router Agent to route between the SciFi agent and the conference agent
