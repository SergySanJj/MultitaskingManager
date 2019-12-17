# MultitaskingManager
Tested on Windows 10

Program uses windows cmd command for debugging processes and windows only library for keyboard hooks.

## Description
This programm executes two functions in separate processes and handles result in operation that has `zero` value, so if one zero value is received other process stops. 


Connection between processes is implemented via Java nio server
