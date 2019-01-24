# WF 2017-02-10
# Python access to OBD trial
import serial
import time
import logging
import string
import io
import os
import sys

#
# 
# Elm327 helper class
#
class Elm327:
  # initialize the serial communication
  def __init__(self,port="/dev/rfcomm0",baud=115200,timeout=1):
    self.log = logging.getLogger(__name__)
    self.port=port
    self.baud=baud
    self.timeout=timeout
    self.ser = serial.Serial(port,baud)
    self.ser.timeout=timeout

  # send the given string 
  def send(self,s):
    if (self.ser):
      self.ser.flushInput()
      self.ser.write(bytes(s + '\r\n', encoding = 'utf-8'))
      self.ser.flush()
      return self.read()
    else:
      raise RuntimeError('Serial port not initialized') from error

  # read data from the serial device
  # http://stackoverflow.com/a/13018267/1497139
  def read(self):
    if (not self.ser):
      raise RuntimeError('Serial port not initialized') from error
    buffer=bytearray()
    time.sleep(1)
    # get all available data
    waiting=self.ser.inWaiting()
    if (waiting>0):
      readbytes= self.ser.read(self.ser.inWaiting())
    else:
      readbytes=[]
    # check 
    if not readbytes:
      self.log.warning("read from "+self.port+" failed")

    buffer.extend(readbytes)

    return buffer.decode()

  def close(self):
    if (not self.ser):
      raise RuntimeError('Serial port not initialized') from error
    self.ser.close()

  def dcmd(self,cmd):
    response=self.send(cmd)
    print(response)

#elm=Elm327("/dev/rfcomm0",115200)
elm=Elm327()
elm.dcmd("ATD")
elm.dcmd("ATZ")
elm.dcmd("ATE0") # switch off echo
elm.dcmd("ATL1") # switch on newlines
elm.dcmd("ATI")
elm.dcmd("ATSP6")
elm.dcmd("ATDP")
elm.dcmd("ATH1")
elm.dcmd("ATD1")
elm.dcmd("ATCAF0")
if (len(sys.argv) >1):
  pid=sys.argv[1]
  elm.dcmd("ATCRA "+pid)
  elm.dcmd("AT MA")
  while True:
    response=elm.read()
    print(response)
elm.close()

#s = input('Enter AT command --> ')
#print ('AT command = ' + s)
#ser.timeout = 1
#response = ser.read(999).decode('utf-8')
#print(response)
#ser.close()  
