import subprocess
from optparse import OptionParser

def runBenchmarks(repeatCount, path, dataFn):  

  cmd = "cd {0}; java SealedDES {1} {2}"
  
  threadCounts = [1,2,4,8,16,32]
  keyLengths = [18,19,20,21,22,23,24]
  keySpace = [262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216]

  for thread in threadCounts:
    for key in keyLengths:
      aggregate = 0
      for i in range(0,repeatCount):
        cmd = "cd {0}; java SealedDES {1} {2}".format(path, thread, key)
        output = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT).stdout.read()
 #       print output
        time = output.split("\n")[-2].split()[-2]
        aggregate += int(time)
      aggregate = float(aggregate) / float(repeatCount)
      aggregate = aggregate / 1000.0
      print "Average execution time for {0} threads and {1} keylength over {2} runs: {3} seconds, {4} keys examined per second".format(thread, key, repeatCount, aggregate, int(pow(2, key) / aggregate))
      with open(dataFn, 'a') as dataFile:
        dataFile.write("{0}, {1}, {2}, {3}, {4}\n".format(thread, key, pow(2, key), aggregate, int(pow(2,key) / aggregate)))




if __name__ =='__main__':

  usage = "Usage: %prog [options]"
  parser = OptionParser(usage=usage)

  parser.add_option("-r", "--repeat", type="int", dest="repeat",
                     default="20", help="Specify how many times to repeat experiment.",
                     metavar="#REPS")

  parser.add_option("-p", "--path", type="string", dest="path",
                     default="/home/ncarey/gitrepos/ParaProg/HW2/Parallel-DES-Key-Cracker-Benchmark/source", 
                     help="Specify complete path to SealedDES.class",
                     metavar="#PATH")
  parser.add_option("-d", "--data", type="string", dest="data",
                     default="/home/ncarey/gitrepos/ParaProg/HW2/Parallel-DES-Key-Cracker-Benchmark/data/bench.out", 
                     help="Specify complete path output file for benchmark data",
                     metavar="#DATA")

  (options, args) = parser.parse_args()

  runBenchmarks(options.repeat, options.path, options.data)


