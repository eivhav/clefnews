# -*- coding: utf-8 -*-
#!/usr/bin/env python

'''
    simple socket client
'''
import socket
import sys
import time
import select
import getopt

def get_args():
    options, args = getopt.getopt(sys.argv[1:], "", ["data="])
    data = ""
    if options != []:
        for name, value in options:
            if name == "--data":
                data = value
    return data

def load_data():
    for line in open("test.txt"):
        line = line.strip()
        yield line

def client(host, port):
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.connect((host, port))
    input = [client, sys.stdin]
    cnt = 0
    data = get_args()
    datalst = [data]
    #for line in load_data():
    for line in datalst:
        if line == "": break
        if cnt > 10: print "End with load data"; break
        else: cnt = cnt + 1
        print "Read data from file..."
        client_data = line
        client.send("Send time: %s:\t%s" % (time.ctime(), client_data))
        #input_data, output_data, except_data = select.select(input, [], [])
        data = client.recv(1024)
        print "Get response from server: ", data

if __name__ == '__main__':
    host = ""
    port = 8888
    client(host, port)
