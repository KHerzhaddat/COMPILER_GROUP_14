# COMPILER_GROUP_14

## Group Members

1. PS/CSC/23/0148 - Ampofo Richard
2. PS/CSC/23/0147 - Somuah Amofah Richard
3. PS/CSC/23/0150 - APPIAH DOMINIC ASANTE
4. PS/CSC/23/0151 - Obadiah-Ephraim Mongson
5. PS/CSC/23/0152 - Serebour Adams Dennis
6. PS/CSC/23/0153 - Daniel Sam
7. PS/CSC/23/0154 - Caleb Acquah
8. PS/CSC/23/0155 - Thomas Karikari
9. PS/CSC/23/0156 - ANTWI EMMANUEL AGYAKWA




## Phase 5 - Sample Program

File: tests/sample_program.txt

if (5 > 1) { print 1; }
if (5 < 10) { print 0; }
int x = 10;
print x;

Expected Output:
1
0
10

Evidence:
- evidence/terminal_transcript.txt

[line 1] Parse Error: Expect expression.
[line 1] Parse Error: Expect expression.
[line 2] Parse Error: Expect expression.
[line 2] Parse Error: Expect expression.
=== IR ===
print 1.0
print 0.0
x = 10.0
print x

=== Assembly ===
PRINT 1.0
PRINT 0.0
MOV x, 10.0
PRINT x


AI Assistance Disclosure:
Debugging help from Meta AI was used to find a bad character in test7.txt. All code is our own work.