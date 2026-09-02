plugins {
    `java-library`
}

// Applied by every module that has tests, so the stack is one decision instead of seven.
//
// Modules used to declare these themselves and had already drifted: one had the Mockito JUnit
// extension and another did not, and two declared the whole set while containing no tests at all.
dependencies {
    "testImplementation"("org.junit.jupiter:junit-jupiter:${Versions.JUNIT}")
    "testImplementation"("org.assertj:assertj-core:${Versions.ASSERTJ}")
    "testImplementation"("org.mockito:mockito-core:${Versions.MOCKITO}")
    "testImplementation"("org.mockito:mockito-junit-jupiter:${Versions.MOCKITO}")
    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
}
