# -*- coding: utf-8 -*-
#!/usr/bin/env python

'''
    simple socket server
'''
import socket
import sys
import thread
import time
import select
import json

#Function for handling connections. This will be used to create threads
def clientthread(conn):
    #Sending message to connected client
    conn.send('Welcome to the server. Type something and hit enter\n') #send only takes string
        
    #infinite loop so that function do not terminate and thread do not end.
    while True:
        #Receiving from client
        data = conn.recv(1024)
        reply = 'OK...' + data
        if not data:
            break
        conn.sendall(reply)
    #came out of loop
    conn.close()

def server(host, port):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    #s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    print "SOCKET CREATED"

    # Bind socket to a local host and port
    try:
        s.bind((host, port))
    except socket.error as msg:
        print "bind socket failed. Error Code: "+str(msg[0]) + " Message" + msg[1]
        sys.exit()
    print "Socket bind complete"

    # Start listening on socket
    s.listen(10)
    print 'Socket now listening'
    input = [s]
    print "Connecting..."

    # now keep talking with the client
    while 1:
        input_data, output_data, except_data = select.select(input, [], [])
        if input_data:
            for receive_data in input_data:
                if receive_data == s:
                    client,addr = s.accept()
                    print 'Connected with ' + addr[0] + ':' + str(addr[1])
                    input.append(client)
                else:
                    try:
                        data = receive_data.recv(1024)
                        if data:
                            # insert recommendation algorithms here ...
                            
                            print "Msg from Client: ", data
                            client.send("Get data from client!")
                    except Exception, e: pass

    s.close()


if __name__ == '__main__':
    host = ""
    port = 8888
    server(host, port)
