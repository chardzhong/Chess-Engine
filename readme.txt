Chess Engine

UI - to be implemented
Board representation - boards are represented as 'bit boards' where fourteen 8 byte long variables represents each type of piece for both black and white.

Move Generation -
Valid moves calculated by searching through rays that represents each pieces attacking squares and constructing bit board that represents all valid moving squares.

Engine -
Minimax search tree implementing iterative deepening alpha beta pruning, transposition tables, and quiescence search to generate best moves. (To be implemented)