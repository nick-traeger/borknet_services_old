#!/usr/bin/env python
# Echo client program
import socket
import sys

HOST = "127.0.0.1"
PORT = 4444
PASS = "passwordgoeshere!"

command=""
if (len(sys.argv) > 1):
 command=sys.argv[1]
else:
 print 'Example Usage: ./w.py "Q say #coder-com Python!"'
 exit()

try:
 s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
except socket.error, msg:
 print "[ERROR] "+msg[1]
 sys.exit(1)

try:
 s.connect((HOST, PORT))
except socket.error, msg:
 print "[ERROR] "+msg[1]
 sys.exit(1)

try:
 s.send("PASS " + PASS + "\n")
 data = s.recv(1024).strip(" \t\n\r")
 if data == "OK":
  s.send(command+"\n")
  data = s.recv(1024).strip(" \t\n\r")
  if data == "OK":
   print "Command sent ok."
  else:
   print "Command could not be sent."
 else:
  print "Could not authenticate."
 s.send(".\n")
 s.close()
except socket.error, msg:
 print "[ERROR] "+msg[1]
 sys.exit(1)
 
sys.exit(0)
