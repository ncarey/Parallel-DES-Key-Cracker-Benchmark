
from optparse import OptionParser



if __name__ =='__main__':

  usage = "Usage: %prog [options]"
  parser = OptionParser(usage=usage)

  parser.add_option("-d", "--dummy", type="string", dest="dummydest",
                     default="dumdum", help="This is a placeholder template option",
                     metavar="#DUMMY")


  (options, args) = parser.parse_args()


