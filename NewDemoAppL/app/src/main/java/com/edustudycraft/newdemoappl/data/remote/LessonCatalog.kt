package com.edustudycraft.newdemoappl.data.remote

import com.edustudycraft.newdemoappl.data.remote.dto.CourseDto
import com.edustudycraft.newdemoappl.data.remote.dto.RemoteLesson
import com.edustudycraft.newdemoappl.domain.model.CourseProgress

/**
 * The bundled catalog matches the assignment payload (progress + lesson count).
 * Lesson titles are expanded here so completion can be stored per lesson.
 */
object LessonCatalog {
    fun lessonsFor(course: CourseDto): List<RemoteLesson> {
        val known = TITLES[course.id]
        val names = if (known != null && known.size == course.lessonCount) {
            known
        } else {
            List(course.lessonCount) { index -> "Lesson ${index + 1}" }
        }
        val completedCount = CourseProgress.seededCompletedCount(course.progress, names.size)
        return names.mapIndexed { index, title ->
            RemoteLesson(
                id = course.id * 100 + index + 1,
                title = title,
                completed = index < completedCount,
            )
        }
    }

    private val TITLES: Map<Int, List<String>> = mapOf(
        1 to listOf(
            "Introduction",
            "Variables & Data Types",
            "Operators & Expressions",
            "Conditional Statements",
            "Loops",
            "Functions",
            "Lists & Tuples",
            "Dictionaries & Sets",
            "Strings",
            "File Handling",
            "Exception Handling",
            "Modules & Packages",
            "Object-Oriented Programming",
            "Inheritance & Polymorphism",
            "Decorators",
            "Generators & Iterators",
            "Comprehensions",
            "Working with APIs",
            "Testing with pytest",
            "Capstone Project",
        ),
        2 to listOf(
            "What is Generative AI",
            "How Large Language Models Work",
            "Prompt Engineering Basics",
            "Tokens, Context, and Temperature",
            "Chat vs Completion Models",
            "Retrieval-Augmented Generation",
            "Embeddings & Vector Search",
            "Fine-Tuning Overview",
            "Image Generation",
            "Multimodal Models",
            "Evaluation & Hallucinations",
            "Safety & Guardrails",
            "Agents & Tool Use",
            "Building a Chatbot",
            "Cost, Latency, and Deployment",
            "Capstone: AI Study Assistant",
        ),
        3 to listOf(
            "How the Web Works",
            "HTML Foundations",
            "CSS Layout",
            "JavaScript Basics",
            "DOM and Events",
            "Git and GitHub",
            "HTTP and REST APIs",
            "Node.js Fundamentals",
            "Express Routing",
            "Databases and SQL",
            "Data Modeling",
            "Authentication Basics",
            "Frontend Frameworks",
            "React Components",
            "State Management",
            "Forms and Validation",
            "Styling Systems",
            "Connecting Frontend to API",
            "Error Handling",
            "Testing Fundamentals",
            "Deployment",
            "CI Basics",
            "Performance",
            "Security Essentials",
            "Accessibility",
            "Observability",
            "Project Architecture",
            "Capstone Project",
        ),
    )
}
