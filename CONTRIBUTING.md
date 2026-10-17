# Contributing to Create Shared

Thank you for your interest in contributing to **Create Shared**! Whether you are fixing bugs, improving documentation, or adding new features, all contributions are greatly appreciated.

---

## 🛠️ Development Setup

To set up the workspace for development:

1. **Prerequisites**:
   - [Java Development Kit (JDK) 21](https://adoptium.net/) or higher.
   - [Git](https://git-scm.com/).
   - An IDE such as IntelliJ IDEA or Eclipse.

2. **Clone the Repository**:
   ```bash
   git clone https://github.com/YigitGuven/Create-Shared.git
   cd Create-Shared
   ```

3. **Prepare the Project**:
   - For **IntelliJ IDEA**: Open the folder as a Gradle project.
   - Run the Gradle build:
     ```bash
     ./gradlew build
     ```
   - Generate IDE run configurations:
     ```bash
     ./gradlew genIntellijRuns
     ```
   - To launch the development client:
     ```bash
     ./gradlew runClient
     ```

---

## 🐛 Reporting Bugs

- Search existing [Issues](https://github.com/YigitGuven/Create-Shared/issues) to ensure your issue hasn't already been reported.
- If not, open a new issue using the **Bug Report** or **Crash Report** template.
- Provide all requested information: Minecraft version, NeoForge version, Create version, steps to reproduce, and logs/crash reports via [mclo.gs](https://mclo.gs/).

---

## 💡 Suggesting Features

- Open an issue using the **Feature Request** template.
- Clearly describe the idea, how it relates to Create mechanics, and why it would benefit players.

---

## 🔀 Submitting Pull Requests

1. Fork the repository and create a branch from the active development branch (e.g., `1.21.1-neoforge`).
2. Implement your changes following project conventions.
3. Test thoroughly in client and dedicated server runs.
4. Ensure `./gradlew build` completes with zero errors.
5. Submit a Pull Request targeting the appropriate branch and fill in the PR template.

---

## 📜 Code of Conduct

Please note that this project is governed by the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.
