# Tiny_C-Compiler

This project was one of six practical projects provided by the basic lecture "Programming 2" at Saarland University in Summer Semester 2022. The lecture was hold by Prof. Dr. Sebastian Hack.


### TinyC is a reduced version of C. The most notable restrictions and deviations with regard to C, mentioned in the project description(*), are:
* There are only three built-in base types: char, int, and void. On top of that, the type constructor for pointers is defined. Functions are never arguments of type constructors.
* There are no structs and unions.
* Not all unary/binary operators are available.
* A function may have at most four arguments.

This high-level compiler translates the language Tiny_C to the low-level language "MIPS-assembly" using "syntax-directed code generation" technique and emits verification conditions for the correctness of the program.

The main goal was to implement in java three phases of Tiny_C compilation, which I implemented in this project:
1. Semantic Analysis
2. assembly code generation
3. generation of verification condition

This means that the lexer and parser was provided by the teaching team.

The implementations can be found in https://github.com/azazi1/TinyC-Compiler/tree/main/src/tinycc/implementation

There are also tests for each of these 3 phases, most of which were written by me, the rest by teaching team.
Those tests cn be found in https://github.com/azazi1/TinyC-Compiler/tree/main/src/tinycc/tests

______________________________________
(*) https://cms.sic.saarland/prog2_22/dl/57/Compiler.pdf
