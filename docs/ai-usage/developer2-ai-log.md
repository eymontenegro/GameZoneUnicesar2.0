# AI Usage Log — Developer 2 (Person Module)

## Tool used

Claude (Anthropic)

## How AI was used

### Git and GitHub

- Asked how to use Git commands through Windows PowerShell, since I did not have much experience using it.
- Asked about the correct Git Flow for this project (feature branches from develop) and the Conventional Commits message format required by the assignment.
- Asked why a Pull Request cannot be approved by its own author.

### Conceptual questions about OOP

- Asked why a subclass would extend an abstract class in Java, and what the `extends` keyword means.
- Asked what `super()` does inside a subclass constructor and why it is needed when the parent class has private attributes.
- Asked why a list attribute needs to be initialized in the constructor before it can be used.
- Asked for an explanation of `try-with-resources` and exception handling (`try/catch`) in Java, since these were new concepts for me.

### Java and Maven

- Asked how to organize packages correctly inside a Maven project (model, persistence, service).
- Asked for suggestions on English naming conventions for classes, attributes and methods.


## Exam — Accessory Module (Requerimiento 1)

### Tool used
Claude (Anthropic)

### How AI was used

#### Git and PowerShell commands
- Asked how to clone the repository, list and switch branches, and do add/commit/push following Conventional Commits.
- Asked how to join a feature branch already created by another team member instead of duplicating it.

#### Object-oriented design concepts
- Asked why `Controller` and `Memory` implement the `ConsoleCompatible` interface instead of that behavior living in the abstract `Accessory` class, and what advantage that has over placing it directly in the superclass.
- Asked why `Cable` does not need to implement `ConsoleCompatible` even though it is also an accessory.
- Asked how a "type discriminator" works in a CSV file, to be able to reconstruct objects of different subclasses (`Controller`, `Cable`, `Memory`) when reading the file.
- Asked why file-saving/loading logic should not live in the `model` package classes, and why it belongs in `persistence` instead.

#### Implementation
- Asked for help drafting the preloaded data file `accessories.csv`.