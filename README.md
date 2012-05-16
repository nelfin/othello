ANU COMP3130 Group Research Project
===================================

AI agent for playing a modified version of 10x10 Othello/Reversi in Java. Four
locations randomly determined at the start of each game will be blocked. The
agent will compete in a tournament against others completing the same project.

Contributors
------------

* [Andrew Haigh](https://github.com/nelfin)
* [Tim Cosgrove](https://github.com/CapnP)
* [Joshua Nelson](https://github.com/cyberdash)

Building
--------

```bash
% git clone --recursive git://github.com/nelfin/othello.git
% cd othello/
% make
```

Running
-------

### Standard play:
```bash
% ./client <white|black> [[host:]port] [strategy]
```

### Learning:
```bash
% ./plain_arena
```
```bash
% ./elo_arena
```
