# Socket Programming
      
## How to compile and make the programs
- You need minimum of JDK 7
- Available command:
`make all`
`make server`
`make client`



## Server program
~~~~
Number of parameters: 1
Parameter:
      $1: <req_code>
How to run:
      ./server $1
~~~~

## Client program
~~~~
Number of parameters: 4
Parameter:
      $1: <server_address>
      $2: <n_port>
      $3: <req_code>
      $4: <message>
How to run:
      ./client $1 $2 $3 $4
~~~~

## Built and Tested Machines
1. Built and tested client and server in two different student.cs.machines
      - ubuntu1404-002.student.uwaterloo.ca
      - ubuntu1404-004.student.uwaterloo.ca
2. Built and tested both client and server in a single student.cs.machine
      - ubuntu1404-004.student.uwaterloo.ca
