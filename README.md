# Algorithm Explorer

Algorithm Explorer is a Java desktop application for exploring common algorithms and data structures through visual, interactive demonstrations. It is built with JavaFX and uses FXML and CSS for its interface.

## What it includes

- **Sorting:** 16 algorithms, including Bubble, Selection, Insertion, Merge, Quick, Heap, Counting, Radix, and Tim Sort. Follow comparisons and array changes step by step.
- **Searching:** Linear, Binary, Jump, and Interpolation Search.
- **Data structures:** Singly and doubly linked lists, binary search trees, stacks, and queues, with interactive operations.
- **Recursion:** Factorial, Fibonacci, Tower of Hanoi, and recursive Binary Search demonstrations.
- **Benchmarking:** Compare sorting algorithm timings across configurable input sizes and data distributions.
- **Complexity reference:** Review algorithm time and space complexity information.

## Requirements

- Java Development Kit (JDK) with `javac` and `java` available on `PATH`.
- JavaFX SDK with the `controls` and `fxml` modules.
- Visual Studio Code and the **Extension Pack for Java** (recommended for editing and Java support).

The provided scripts currently expect JavaFX SDK `27.0.1` in a specific local folder. If your SDK is installed elsewhere, update the `FX_PATH` value near the top of both `build.bat` and `run.bat` to point to the JavaFX SDK directory that contains `lib`.

## Run in VS Code on Windows

1. Install a JDK and JavaFX SDK. Make sure `java` and `javac` work in a new terminal.
2. Open this project folder in VS Code.
3. If needed, update `FX_PATH` in `build.bat` and `run.bat` to your JavaFX SDK location.
4. Open the VS Code terminal (**Terminal > New Terminal**) and build the application:

   ```bat
   .\build.bat
   ```

5. Start the application:

   ```bat
   .\run.bat
   ```

You can also open `build.bat` or `run.bat` in VS Code and use **Run > Run Without Debugging**. The first script compiles sources into `bin`; the second launches the modular JavaFX application.

## Operating system support

- **Windows:** Supported by the included `build.bat` and `run.bat` scripts, after configuring the JavaFX SDK path.
- **macOS and Linux:** Java and JavaFX are available on these platforms, but the included scripts are Windows batch files and will not run as-is. Use platform-specific compile/run commands with your JavaFX SDK, or create equivalent shell scripts.

The project has not been validated on macOS or Linux by the included run scripts.

## Project layout

```text
src/
  algexplorer/            Application entry point and shared UI context
    navigation/           Screen navigation
    searching/            Search algorithm implementations
    sorting/               Sorting algorithm implementations
    ui/                    JavaFX screens, controllers, FXML, and CSS
build.bat                  Compile the application on Windows
run.bat                    Launch the application on Windows
