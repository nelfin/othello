#!/usr/bin/env python
from __future__ import print_function
from subprocess import *
from sys import stdout, argv
import atexit

CLASSPATH='org.nelfin.othello/bin/'
#Default strategies
us_strategy = "negamax"
them_strategy = ""
if len(argv) < 2:
    iterations = 100
else:
    iterations = int(argv[1])
    if len(argv) == 3:
        us_strategy = argv[2]
    if len(argv) == 4:
        us_strategy = argv[2]
        them_strategy = argv[3]


global server, devnull, output

@atexit.register
def shutdown():
    server.kill()
    output.close()

def opponent(colour):
    if colour == 'white':
        return 'black'
    else:
        return 'white'

devnull = open('/dev/null', 'w')
output = open('output.log', 'w')

as_white = list()
as_black = list()
for colour, results in [('white', as_white), ('black', as_black)]:
    server = Popen(['othello-reference/server_headless', '0'],
            stderr=devnull, stdout=PIPE)
    print(us_strategy," is ",colour)
    print(them_strategy," is ",opponent(colour))
    for i in xrange(iterations):
        us = Popen(['java', '-cp', CLASSPATH, 'iago/Client', colour, "3130", us_strategy],
                stdin=devnull, stderr=output, stdout=output)
        if(them_strategy == ""): #If we haven't been told what strategy to use for the opponent, use the reference opponent
            them = Popen(['othello-reference/client', opponent(colour)],
                    stdin=devnull, stderr=output, stdout=output)
        else:
            them = Popen(['java', '-cp', CLASSPATH, 'iago/Client', opponent(colour), "3130", them_strategy],
                stdin=devnull, stderr=output, stdout=output)
        us.wait()
        them.wait()

        result = server.stdout.readline()
        output.write(server.stdout.readline())
        output.write(server.stdout.readline())

        # One of W, B, T
        results.append(result[:1])
        print(result[:1], end='')
        stdout.flush()
    server.kill()
    print()

print()

bar_length = 50
for results, symbol in [(as_white, 'W'), (as_black, 'B')]:
    wins = results.count(symbol)
    ties = results.count('T')
    bar  = "*" * int(bar_length * (wins / float(iterations)))
    bar += "." * int(bar_length * (ties / float(iterations)))
    print("{0}: {1:>6} {2:>6} {3}".format(symbol, wins, ties, bar))

