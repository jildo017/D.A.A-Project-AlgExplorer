# Algorithm Explorer (Java Swing)

Requires JDK 8 or newer (tested on JDK 21). No libraries, no Maven, no Gradle.

## Run
Windows: double-click `run.bat`   |   Mac/Linux: `sh run.sh`

Or manually from this folder:

    javac -encoding UTF-8 -d out src/utils/*.java src/datastructures/*.java src/algorithms/*.java src/ui/*.java
    java -cp out ui.MainFrame

## Structure
- utils: ExecutionTracker, ArrayUtils, ComplexityInfo
- datastructures: Node, CustomStack, CustomQueue, CustomLinkedList
- algorithms: StepListener, SortingAlgorithms (10 sorts), RecursiveAlgorithms (4)
- ui: MainFrame (main), HomePanel, CategoryPanel, WorkspacePanel, VisualizerPanel, BenchmarkPanel

Only java.util.Random is imported from java.util. No built-in collections or sort methods are used.
